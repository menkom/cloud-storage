package info.mastera.cloudstorage.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
import info.mastera.cloudstorage.config.AmazonSettings;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AmazonS3Service {

    AmazonS3 amazonS3;

    AmazonSettings amazonSettings;

    public List<Bucket> getBuckets() {
        return amazonS3.listBuckets();
    }

    /**
     * <a href="https://docs.aws.amazon.com/AmazonS3/latest/userguide/bucketnamingrules.html">Bucket name rules</a>
     */
    public Bucket createBucket(String name) {
        if (amazonS3.doesBucketExistV2(name)) {
            return getBucket(name);
        }
        return amazonS3.createBucket(name);
    }

    public void deleteBucket(String bucketName) {
        log.info("Attempt to delete bucket: {}", bucketName);
        if (amazonS3.doesBucketExistV2(bucketName)) {
            amazonS3.deleteBucket(bucketName);
        }
    }

    public Bucket getBucket(String bucket_name) {
        return amazonS3.listBuckets()
                .stream()
                .filter(bucket -> bucket.getName().equals(bucket_name))
                .findFirst()
                .orElse(null);
    }

    public PutObjectResult upload(MultipartFile file) {
        log.info("Uploading file. filename: {}, type: {}, size: {}", file.getOriginalFilename(), file.getContentType(), file.getSize());
        PutObjectResult result = null;
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(file.getSize());

        try {
            String etag = getETag(file);
            String md5 = getMD5(file);

            result = amazonS3.putObject(
                    amazonSettings.getDefaultBucket(),
                    UUID.randomUUID() + "/" + file.getOriginalFilename(),
                    file.getInputStream(),
                    metadata
            );
            log.info("source etag: {}; source md5: {}; target etag: {}; target md5: {}; exp: {}; meta: {}", etag, md5, result.getETag(), result.getContentMd5(), result.getExpirationTime(), result.getMetadata());
        } catch (IOException e) {
            log.error("IO exception: " + e.getMessage());
        } catch (NoSuchAlgorithmException e) {
            log.error("Algorithm exception: " + e.getMessage());
        }
        return result;
    }

    /**
     * Method to calculate MD5 in HEX format.
     * Note: getBytes() is very expensive operation as it copies InputStream to byte array
     */
    private String getETag(MultipartFile file) throws NoSuchAlgorithmException, IOException {
        MessageDigest mdigest = MessageDigest.getInstance("MD5");
        byte[] digest = mdigest.digest(file.getBytes());
        return new BigInteger(1, digest).toString(16);
    }

    /**
     * Method to calculate MD5 in text format.
     * Note: getBytes() is very expensive operation as it copies InputStream to byte array
     */
    private String getMD5(MultipartFile file) throws IOException {
        byte[] resultByte = DigestUtils.md5(file.getBytes());
        return new String(Base64.encodeBase64(resultByte));
    }

    public List<S3ObjectSummary> getObjects(String bucketName) {
        if (amazonS3.doesBucketExistV2(bucketName)) {
            return amazonS3.listObjects(bucketName).getObjectSummaries();
        } else {
            return null;
        }
    }

    public void deleteObject(String bucketName, String key) {
        amazonS3.deleteObject(bucketName, key);
    }

    public void getObject(String bucketName, String key) {
        if (amazonS3.doesObjectExist(bucketName, key)) {
            S3Object object = amazonS3.getObject(bucketName, key);
            saveToFile(object.getObjectContent(), new File(object.getKey()));
        }
    }

    private void saveToFile(InputStream input, File file) {
        try (OutputStream output = new FileOutputStream(file)) {
            input.transferTo(output);
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }
}
