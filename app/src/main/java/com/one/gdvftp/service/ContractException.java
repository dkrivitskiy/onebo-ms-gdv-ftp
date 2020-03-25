package com.one.gdvftp.service;

import com.one.gdvftp.entity.Display;

// Checked exceptions do not work with Java8 streams.
public class ContractException extends RuntimeException {

  public ContractException(String msg, String entity) {
    super(msg+" "+entity);
  }

  public ContractException(String msg, Display entity) {
    this(msg, entity.display());
  }

}
