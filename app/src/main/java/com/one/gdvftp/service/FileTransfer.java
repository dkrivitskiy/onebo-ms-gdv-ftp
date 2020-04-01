package com.one.gdvftp.service;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import com.amazonaws.services.s3.AmazonS3;

@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class FileTransfer {

  @Value("${amazon.s3.bucket}")
  private String s3Bucket;

  private final @NonNull AmazonS3 s3Client;

  public void upload() {
    val list = s3Client.listObjects(s3Bucket).getObjectSummaries();
    list.forEach(System.out::println);
  }

}
