package info.mastera.cloudstorage.service;

import com.amazonaws.services.s3.model.Bucket;
import com.amazonaws.services.s3.model.PutObjectResult;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface IAmazonS3Service {

    List<Bucket> getBuckets();

    Bucket createBucket(String name);

    void deleteBucket(String bucketName);

    Bucket getBucket(String bucket_name);

    PutObjectResult upload(MultipartFile file);

    PutObjectResult upload(String bucketName, MultipartFile file);

    List<S3ObjectSummary> getObjects(String bucketName);

    void deleteObject(String bucketName, String key);

    void getObject(String bucketName, String key);
}
