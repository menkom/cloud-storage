package info.mastera.cloudstorage.config;


import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@FieldDefaults(level = AccessLevel.PRIVATE)
@ConfigurationProperties(prefix = "amazon.s3")
public class AmazonSettings {

    String host;
    String region;
    String accessKey;
    String secretKey;
    String defaultBucket;
}
