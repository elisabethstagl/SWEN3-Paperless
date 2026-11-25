import logging
import os

import pika
import requests
from minio import Minio

logging.basicConfig(level=logging.INFO)
log = logging.getLogger("genai-worker")

queue = os.environ["GENAI_REQUEST_QUEUE"]
bucket = os.environ.get("MINIO_BUCKET", "documents")
api_key = os.environ["OPENROUTER_API_KEY"]
model = os.environ.get("OPENROUTER_MODEL", "openai/gpt-4o-mini")

minio = Minio(
    os.environ["MINIO_ENDPOINT"],
    access_key=os.environ["MINIO_ROOT_USER"],
    secret_key=os.environ["MINIO_ROOT_PASSWORD"],
    secure=False,
)

OPENROUTER_URL = "https://openrouter.ai/api/v1/chat/completions"
SYSTEM_PROMPT = "You summarize documents. Write a summary of the given text in english. Use at most 5 sentences."


def load_text(document_id):
    response = minio.get_object(bucket, f"{document_id}.txt")
    try:
        return response.read().decode("utf-8")
    finally:
        response.close()
        response.release_conn()


def summarize(text):
    response = requests.post(
        OPENROUTER_URL,
        headers={"Authorization": f"Bearer {api_key}"},
        json={
            "model": model,
            "messages": [
                {"role": "system", "content": SYSTEM_PROMPT},
                {"role": "user", "content": text},
            ],
        },
        timeout=120,
    )
    response.raise_for_status()
    return response.json()["choices"][0]["message"]["content"]


def on_message(channel, method, _, body):
    document_id = body.decode()
    log.info("received genai request for document %s", document_id)
    try:
        text = load_text(document_id)
        if text.strip():
            summary = summarize(text)
            log.info("summary for document %s: %s", document_id, summary)
        else:
            log.warning("summary skipped: document %s has no text", document_id)
    except Exception:
        log.exception("summary failed for document %s", document_id)
    channel.basic_ack(method.delivery_tag)


parameters = pika.ConnectionParameters(
    host=os.environ["RABBITMQ_HOST"],
    port=int(os.environ["RABBITMQ_PORT"]),
    credentials=pika.PlainCredentials(
        os.environ["RABBITMQ_USERNAME"],
        os.environ["RABBITMQ_PASSWORD"],
    ),
    heartbeat=1800,
    blocked_connection_timeout=1800,
    connection_attempts=10,
    retry_delay=3,
)


channel = pika.BlockingConnection(parameters).channel()
channel.queue_declare(queue=queue, durable=True)
channel.basic_consume(queue=queue, on_message_callback=on_message)
channel.start_consuming()
