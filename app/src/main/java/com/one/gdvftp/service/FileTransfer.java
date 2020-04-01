package com.one.gdvftp.service;

import com.amazonaws.services.s3.model.S3ObjectSummary;
import java.io.File;
import java.util.List;
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

  public List<S3ObjectSummary> list() {
    val list = s3Client.listObjects(s3Bucket).getObjectSummaries();
    return list;
  }

  public void upload(String name, File file) {
    s3Client.putObject(s3Bucket, name, file);
  }

}
