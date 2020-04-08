package com.one.gdvftp.config;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FileTransferConfig {

  @Value("${amazon.s3.accesskey}")
  private String amazonS3AccessKey;

  @Value("${amazon.s3.secret}")
  private String amazonS3Secret;

  @Bean
  public AmazonS3 amazonS3Client(){

    final AWSCredentials credentials = new BasicAWSCredentials(amazonS3AccessKey, amazonS3Secret);

    return AmazonS3ClientBuilder
        .standard()
        .withCredentials(new AWSStaticCredentialsProvider(credentials))
        .withRegion(Regions.EU_CENTRAL_1)
        .build();
  }

}
