import json
import logging
import os
import socket
import time

import pika

logging.basicConfig(
    level=logging.INFO,
    format="%(asctime)s %(levelname)s %(message)s"
)
logger = logging.getLogger("indexing-worker")

RABBITMQ_HOST = os.getenv("RABBITMQ_HOST", "rabbitmq")
RABBITMQ_PORT = int(os.getenv("RABBITMQ_PORT", "5672"))
RABBITMQ_USERNAME = os.environ["RABBITMQ_USERNAME"]
RABBITMQ_PASSWORD = os.environ["RABBITMQ_PASSWORD"]
INDEXING_QUEUE = os.getenv("INDEXING_QUEUE", "indexing.request")

LOGSTASH_HOST = os.getenv("LOGSTASH_HOST", "logstash")
LOGSTASH_PORT = int(os.getenv("LOGSTASH_PORT", "5000"))


def process_message(channel, method, properties, body):
    try:
        message = json.loads(body.decode("utf-8"))

        operation = message["operation"]
        document_id = message["id"]

        if operation not in ("index", "delete"):
            raise ValueError(f"Unknown operation: {operation}")

        if operation == "index":
            for field in ("title", "content", "fileName", "createdAt"):
                if field not in message:
                    raise ValueError(f"Missing field: {field}")

        payload = (
            json.dumps(message, ensure_ascii=False) + "\n"
        ).encode("utf-8")

        with socket.create_connection(
            (LOGSTASH_HOST, LOGSTASH_PORT),
            timeout=10
        ) as connection:
            connection.sendall(payload)

        logger.info(
            "Forwarded %s for document %s",
            operation,
            document_id
        )

        channel.basic_ack(
            delivery_tag=method.delivery_tag
        )

    except (ValueError, KeyError, UnicodeDecodeError):
        logger.exception("Invalid indexing message")

        # Reject malformed messages instead of retrying forever.
        channel.basic_reject(
            delivery_tag=method.delivery_tag,
            requeue=False
        )

    except Exception:
        logger.exception("Forwarding failed")

        # Close the RabbitMQ connection so the unacknowledged
        # message can be redelivered after reconnecting.
        raise


def main():
    while True:
        connection = None

        try:
            credentials = pika.PlainCredentials(
                RABBITMQ_USERNAME,
                RABBITMQ_PASSWORD
            )

            connection = pika.BlockingConnection(
                pika.ConnectionParameters(
                    host=RABBITMQ_HOST,
                    port=RABBITMQ_PORT,
                    credentials=credentials,
                    heartbeat=600,
                    blocked_connection_timeout=30
                )
            )

            channel = connection.channel()

            channel.queue_declare(
                queue=INDEXING_QUEUE,
                durable=True
            )

            channel.basic_qos(prefetch_count=1)

            channel.basic_consume(
                queue=INDEXING_QUEUE,
                on_message_callback=process_message
            )

            logger.info(
                "Waiting for messages on %s",
                INDEXING_QUEUE
            )

            channel.start_consuming()

        except Exception:
            logger.exception(
                "Worker disconnected; retrying in 5 seconds"
            )

        finally:
            if connection is not None and connection.is_open:
                connection.close()

        time.sleep(5)


if __name__ == "__main__":
    main()
