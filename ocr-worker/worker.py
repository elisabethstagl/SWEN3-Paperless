
import json
import logging
import os

import pika
import pytesseract
from minio import Minio
from pdf2image import convert_from_bytes

logging.basicConfig(level=logging.INFO)
log = logging.getLogger("ocr-worker")

queue = os.environ["OCR_REQUEST_QUEUE"]
result_queue = os.environ["OCR_RESULT_QUEUE"]
bucket = os.environ.get("MINIO_BUCKET", "documents")

minio = Minio(
    os.environ["MINIO_ENDPOINT"],
    access_key=os.environ["MINIO_ROOT_USER"],
    secret_key=os.environ["MINIO_ROOT_PASSWORD"],
    secure=False,
)


def load_pdf(document_id):
    response = minio.get_object(bucket, document_id)
    try:
        return response.read()
    finally:
        response.close()
        response.release_conn()


OCR_DPI = 300
OCR_LANGUAGES = "deu+eng"


def ocr_page(page):
    return pytesseract.image_to_string(page, lang=OCR_LANGUAGES)


def ocr(pdf):
    pages = convert_from_bytes(pdf, dpi=OCR_DPI)
    page_texts = [ocr_page(page) for page in pages]
    return "\n\n".join(page_texts)


def send_result(channel, document_id, text):
    channel.basic_publish(
        exchange="",
        routing_key=result_queue,
        body=json.dumps({"documentId": document_id, "text": text}),
        properties=pika.BasicProperties(
            content_type="application/json",
            delivery_mode=pika.DeliveryMode.Persistent,
        ),
    )


def on_message(channel, method, _, body):
    document_id = body.decode()
    log.info("received ocr request for document %s", document_id)
    try:
        text = ocr(load_pdf(document_id))
        send_result(channel, document_id, text)
        log.info("sent ocr result for document %s (%d chars)", document_id, len(text))
    except Exception:
        log.exception("ocr failed for document %s", document_id)
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
channel.queue_declare(queue=result_queue, durable=True)
channel.basic_consume(queue=queue, on_message_callback=on_message)
channel.start_consuming()
