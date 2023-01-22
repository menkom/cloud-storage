package info.mastera.cloudstorage.controller;

import com.amazonaws.services.s3.model.PutObjectResult;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import info.mastera.cloudstorage.service.AmazonS3Service;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Slf4j
@RequestMapping("api/storage/objects")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RestController
public class StorageObjectsController {

    AmazonS3Service amazonS3Service;

    @PostMapping(value = "", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public PutObjectResult upload(@RequestPart("file") MultipartFile file) {
        return amazonS3Service.upload(file);
    }

    @GetMapping(value = "{bucketName}")
    @ResponseStatus(HttpStatus.OK)
    public List<S3ObjectSummary> getObjects(@PathVariable String bucketName) {
        return amazonS3Service.getObjects(bucketName);
    }

    @DeleteMapping(value = "{bucketName}/{key}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteObject(@PathVariable String bucketName, @PathVariable String key) {
        amazonS3Service.deleteObject(bucketName, key);
    }

    @GetMapping(value = "{bucketName}/{key}", produces = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public void downloadObject(@PathVariable String bucketName, @PathVariable String key) {
        amazonS3Service.getObject(bucketName, key);
    }
}
