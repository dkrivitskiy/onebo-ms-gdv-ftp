package com.one.gdvftp.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Getter;
import lombok.ToString;

@Entity
@Table(name = "productgroup__c")
@ToString
@Getter
public class ProductGroup implements Display {

  @Id
  @Column(name="pkexternalid__c")
  private String pk;

  @Column(name="name")
  private String name;


  @Override
  public String display() {
    return getName();
  }
}
