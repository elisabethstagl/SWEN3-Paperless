import logging
import os

import pika

logging.basicConfig(level=logging.INFO)
log = logging.getLogger("ocr-worker")

queue = os.environ["OCR_REQUEST_QUEUE"]


def on_message(channel, method, _, body):
    log.info("received ocr request for document %s", body.decode())
    channel.basic_ack(method.delivery_tag)


parameters = pika.ConnectionParameters(
    host=os.environ["RABBITMQ_HOST"],
    port=int(os.environ["RABBITMQ_PORT"]),
    credentials=pika.PlainCredentials(
        os.environ["RABBITMQ_USERNAME"],
        os.environ["RABBITMQ_PASSWORD"],
    ),
    connection_attempts=10,
    retry_delay=3,
)

channel = pika.BlockingConnection(parameters).channel()
channel.queue_declare(queue=queue, durable=True)
channel.basic_consume(queue=queue, on_message_callback=on_message)
channel.start_consuming()
