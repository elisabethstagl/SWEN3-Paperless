
package at.fhtw.backend.persistence;

import io.minio.BucketExistsArgs;
import io.minio.GetObjectArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import io.minio.errors.ErrorResponseException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.UUID;

@Repository
public class MinIORepository {

    private final MinioClient minioClient;
    private final String bucketName;

    public MinIORepository(
            MinioClient minioClient,
            @Value("${minio.bucket.name}") String bucketName
    ) {
        this.minioClient = minioClient;
        this.bucketName = bucketName;

        try {
            boolean exists = minioClient.bucketExists(
                    BucketExistsArgs.builder()
                            .bucket(bucketName)
                            .build()
            );

            if (!exists) {
                minioClient.makeBucket(
                        MakeBucketArgs.builder()
                                .bucket(bucketName)
                                .build()
                );
            }

        } catch (Exception e) {
            throw new IllegalStateException(
                    "Failed to initialize MinIO bucket " + bucketName,
                    e
            );
        }
    }

    public UUID save(InputStream stream, UUID id) {
        try {
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucketName)
                            .object(id.toString())
                            .stream(stream, -1, 10 * 1024 * 1024)
                            .contentType("application/pdf")
                            .build()
            );

            return id;

        } catch (Exception e) {
            throw new IllegalStateException(
                    "Failed to save PDF for document " + id,
                    e
            );
        }
    }

    public void saveText(UUID id, String text) {
        try {
            byte[] data = text.getBytes(StandardCharsets.UTF_8);

            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucketName)
                            .object(id + ".txt")
                            .stream(
                                    new ByteArrayInputStream(data),
                                    data.length,
                                    -1
                            )
                            .contentType("text/plain; charset=utf-8")
                            .build()
            );

        } catch (Exception e) {
            throw new IllegalStateException(
                    "Failed to save OCR text for document " + id,
                    e
            );
        }
    }

    public InputStream load(UUID id) {
        try {
            return minioClient.getObject(
                    GetObjectArgs.builder()
                            .bucket(bucketName)
                            .object(id.toString())
                            .build()
            );

        } catch (Exception e) {
            throw new IllegalStateException(
                    "Failed to load PDF for document " + id,
                    e
            );
        }
    }

    public void delete(UUID id) {
        try {
            minioClient.removeObject(
                    RemoveObjectArgs.builder()
                            .bucket(bucketName)
                            .object(id.toString())
                            .build()
            );

            minioClient.removeObject(
                    RemoveObjectArgs.builder()
                            .bucket(bucketName)
                            .object(id + ".txt")
                            .build()
            );

        } catch (Exception e) {
            throw new IllegalStateException(
                    "Failed to delete document " + id + " from MinIO",
                    e
            );
        }
    }

    public String loadText(UUID id) {
        try (InputStream stream = minioClient.getObject(
                GetObjectArgs.builder()
                        .bucket(bucketName)
                        .object(id + ".txt")
                        .build()
        )) {
            return new String(
                    stream.readAllBytes(),
                    StandardCharsets.UTF_8
            );

        } catch (Exception e) {
            throw new IllegalStateException(
                    "Failed to load OCR text for document " + id,
                    e
            );
        }
    }

    public Optional<String> loadTextIfExists(UUID id) {
        try (InputStream stream = minioClient.getObject(
                GetObjectArgs.builder()
                        .bucket(bucketName)
                        .object(id + ".txt")
                        .build()
        )) {
            return Optional.of(
                    new String(
                            stream.readAllBytes(),
                            StandardCharsets.UTF_8
                    )
            );

        } catch (ErrorResponseException e) {

            if ("NoSuchKey".equals(e.errorResponse().code())) {
                return Optional.empty();
            }

            throw new IllegalStateException(
                    "Failed to load OCR text for document " + id,
                    e
            );

        } catch (Exception e) {
            throw new IllegalStateException(
                    "Failed to load OCR text for document " + id,
                    e
            );
        }
    }
}
