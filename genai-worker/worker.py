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

result_queue = os.environ["GENAI_RESULT_QUEUE"]

minio = Minio(
    os.environ["MINIO_ENDPOINT"],
    access_key=os.environ["MINIO_ROOT_USER"],
    secret_key=os.environ["MINIO_ROOT_PASSWORD"],
    secure=False,
)

OPENROUTER_URL = "https://openrouter.ai/api/v1/chat/completions"
SYSTEM_PROMPT = "You summarize documents. Write a summary of the given text in english. Use at most 5 sentences."
MAX_RETRIES = 3
RETRY_DELAY = 2


def load_text(document_id):
    response = minio.get_object(bucket, f"{document_id}.txt")
    try:
        return response.read().decode("utf-8")
    finally:
        response.close()
        response.release_conn()


def summarize(text):
    for attempt in range(1, MAX_RETRIES + 1):
        try:
            response = requests.post(
                OPENROUTER_URL,
                headers={
                    "Authorization": f"Bearer {api_key}"
                },
                json={
                    "model": model,
                    "messages": [
                        {
                            "role": "system",
                            "content": SYSTEM_PROMPT
                        },
                        {
                            "role": "user",
                            "content": text
                        },
                    ],
                },
                timeout=120,
            )

            response.raise_for_status()

            summary = response.json()["choices"][0]["message"]["content"]

            if not isinstance(summary, str) or not summary.strip():
                raise ValueError("GenAI returned an empty or invalid summary")

            return summary.strip()

        except requests.HTTPError as e:
            status = e.response.status_code

            if status != 429 and not 500 <= status < 600:
                raise

            log.warning(
                "Temporary GenAI API error: HTTP %s (attempt %s/%s)",
                status, attempt, MAX_RETRIES
            )

            if attempt == MAX_RETRIES:
                raise

        except (requests.Timeout, requests.ConnectionError):
            log.warning(
                "GenAI connection failed (attempt %s/%s)",
                attempt, MAX_RETRIES
            )

            if attempt == MAX_RETRIES:
                raise

        time.sleep(RETRY_DELAY * attempt)


def on_message(channel, method, _, body):
    document_id = "<unknown>"

    try:
        document_id = body.decode()

        log.info(
            "Received GenAI request for document %s",
            document_id
        )

        text = load_text(document_id)

        if not text.strip():
            log.warning(
                "Summary skipped: document %s has no text",
                document_id
            )

            channel.basic_ack(method.delivery_tag)
            return

        summary = summarize(text)

        send_result(channel, document_id, summary)

        log.info(
            "Sent GenAI result for document %s",
            document_id
        )

    except S3Error as e:
        log.error(
            "MinIO error for document %s: %s",
            document_id, e.code
        )

        channel.basic_nack(
            method.delivery_tag,
            requeue=False
        )
        return

    except requests.RequestException as e:
        log.error(
            "GenAI API failed for document %s: %s",
            document_id, e
        )

        channel.basic_nack(
            method.delivery_tag,
            requeue=False
        )
        return

    except (ValueError, KeyError, IndexError, TypeError):
        log.exception(
            "Invalid GenAI request or response for document %s",
            document_id
        )

        channel.basic_nack(
            method.delivery_tag,
            requeue=False
        )
        return

    except Exception:
        log.exception(
            "Unexpected error for document %s",
            document_id
        )

        channel.basic_nack(
            method.delivery_tag,
            requeue=False
        )
        return

    channel.basic_ack(method.delivery_tag)

def send_result(channel, document_id, summary):
    channel.basic_publish(
        exchange="",
        routing_key=result_queue,
        body=json.dumps({
            "documentId": document_id,
            "summary": summary
        }),
        properties=pika.BasicProperties(
            content_type="application/json",
            delivery_mode=pika.DeliveryMode.Persistent,
        ),
    )


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
channel.queue_declare(queue=queue, durable=True)
channel.queue_declare(queue=result_queue, durable=True)
