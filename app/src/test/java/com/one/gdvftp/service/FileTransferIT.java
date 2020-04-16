package com.one.gdvftp.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.one.gdvftp.boot.Application;
import lombok.val;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class, webEnvironment = SpringBootTest.WebEnvironment.NONE)
public class FileTransferIT {

  @Autowired
  private FileTransfer transfer;

  @Test
  public void testListBuckets() {
    val buckets = transfer.listBuckets();
    assertThat(buckets).isNotEmpty();
    assertThat(buckets).containsAnyOf("onebo-gdv-ftp-dev", "onebo-gdv-ftp-stg", "onebo-gdv-ftp-prod");
  }

  @Test
  public void testListObjects() {
    val obs = transfer.listFolder("test/");
    assertThat(obs).isNotEmpty();
    obs.forEach(path ->
        assertThat(path).startsWith("test/")
    );

  }

  @Test
  public void testUploadString() {
    transfer.upload("test/empty", "");
  }

}
