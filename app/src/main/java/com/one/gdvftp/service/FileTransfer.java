package com.one.gdvftp.service;

import static java.util.stream.Collectors.toList;

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

  /** used for integration test */
  public List<String> listBuckets() {
    val list = s3Client.listBuckets().stream().
        map(b->b.getName()).collect(toList());
    return list;
  }

  /** used for integration test */
  public List<String> listFolder(String folder) {
    val list = s3Client.listObjectsV2(s3Bucket, folder).getObjectSummaries().stream().
        map(s->s.getKey()).collect(toList());
    return list;
  }

  /** used for integration test */
  public void upload(String kex, String content) {
    s3Client.putObject(s3Bucket, kex, content);
  }

  public void upload(String key, File file) {
    s3Client.putObject(s3Bucket, key, file);
  }

}
