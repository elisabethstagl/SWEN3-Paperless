
package at.fhtw.backend.persistence;

import io.minio.*;
import io.minio.errors.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

@Repository
public class MinIORepository {

    private final MinioClient minioClient;

    @Value("${minio.bucket.name}")
    private String bucketName;

    public MinIORepository(MinioClient minioClient) {
        this.minioClient = minioClient;
        try {
            boolean found = minioClient.bucketExists(BucketExistsArgs.builder().bucket("documents").build());
            if (!found) {
                minioClient.makeBucket(MakeBucketArgs.builder().bucket("documents").build());
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    public UUID save(InputStream stream, UUID id) {
        try {
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucketName)
                            .object(id.toString())
                            .stream(stream, stream.available(), -1)
                            .contentType("application/pdf")
                            .build());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return id;
    }

    public void saveText(UUID id, String text) {
        try {
            byte[] data = text.getBytes(StandardCharsets.UTF_8);
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucketName)
                            .object(id + ".txt")
                            .stream(new ByteArrayInputStream(data), data.length, -1)
                            .contentType("text/plain; charset=utf-8")
                            .build());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public InputStream load(UUID id) {
        try {
            return minioClient.getObject(GetObjectArgs.builder()
                    .bucket(bucketName)
                    .object(id.toString())
                    .build());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void delete(UUID id) {
        try {
            System.out.println("tried to delete" + id);
            minioClient.removeObject(RemoveObjectArgs.builder()
                    .bucket(bucketName)
                    .object(id.toString())
                    .build());
            minioClient.removeObject(RemoveObjectArgs.builder()
                    .bucket(bucketName)
                    .object(id + ".txt")
                    .build());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
