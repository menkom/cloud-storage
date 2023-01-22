package info.mastera.cloudstorage.controller;

import com.amazonaws.services.s3.model.Bucket;
import info.mastera.cloudstorage.service.AmazonS3Service;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RequestMapping("api/storage/buckets")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RestController
public class StorageBucketsController {

    AmazonS3Service amazonS3Service;

    @GetMapping(value = "")
    @ResponseStatus(HttpStatus.OK)
    public List<Bucket> getBuckets() {
        return amazonS3Service.getBuckets();
    }

    @PostMapping(value = "")
    @ResponseStatus(HttpStatus.OK)
    public Bucket createBucket(@RequestBody String name) {
        return amazonS3Service.createBucket(name);
    }

    @DeleteMapping(value = "{bucketName}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteBucket(@PathVariable String bucketName) {
        amazonS3Service.deleteBucket(bucketName);
    }
}
