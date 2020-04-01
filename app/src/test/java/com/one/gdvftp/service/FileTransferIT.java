package com.one.gdvftp.service;

import com.one.gdvftp.boot.Application;
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
  public void foo() {
    transfer.upload();
  }

}
