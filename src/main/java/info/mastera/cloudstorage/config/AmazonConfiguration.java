package info.mastera.cloudstorage.config;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AmazonConfiguration {

    @Bean
    public AmazonS3 amazonS3(AmazonSettings amazonSettings) {
        BasicAWSCredentials credentials =
                new BasicAWSCredentials(amazonSettings.getAccessKey(), amazonSettings.getSecretKey());
        return AmazonS3ClientBuilder
                .standard()
                .withEndpointConfiguration(
                        new AwsClientBuilder.EndpointConfiguration(amazonSettings.getHost(), amazonSettings.getRegion())
                )
                //specific setting. For me doesBucketExistV2 works incorrectly.
                //If you have dots in bucket name only with this option you can process it (requests with Virtual-host-style do not work)
                .withPathStyleAccessEnabled(true)
                .withCredentials(new AWSStaticCredentialsProvider(credentials))
                .build();
    }
}
