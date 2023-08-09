package com.project.blogapp.config;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class StorageConfig {

    @Value("${s3.purpose}")
    private String purpose;
    @Value("${cloud.aws.credentials.access-key}")
    private String accessKey;
    @Value("${cloud.aws.credentials.secret-key}")
    private String accessSecret;
    @Value("${cloud.aws.region.static}")
    private String region;
    @Value("${cloud.aws.s3.endpoint}")
    private String endpoint;


    @Bean
    public AmazonS3 s3Client() {
        AmazonS3 s3;
        if (purpose.equals("test")) {
            s3 = AmazonS3ClientBuilder
                    .standard()
                    .withEndpointConfiguration(
                            new AwsClientBuilder.EndpointConfiguration(endpoint, region)
                    )
                    .withCredentials(
                            new AWSStaticCredentialsProvider(
                                    new BasicAWSCredentials(accessKey, accessSecret)
                            )
                    )
                    .build();
        } else {
            AWSCredentials credentials = new BasicAWSCredentials(accessKey, accessSecret);
            s3 = AmazonS3ClientBuilder.standard()
                    .withCredentials(new AWSStaticCredentialsProvider(credentials))
                    .withRegion(region).build();

        }
        return s3;
    }

}