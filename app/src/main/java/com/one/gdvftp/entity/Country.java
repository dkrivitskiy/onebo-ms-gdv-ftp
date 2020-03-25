package com.one.gdvftp.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Getter;
import lombok.ToString;

@Entity
@Table(name = "country__c")
@ToString
@Getter
public class Country implements Display {

  @Id
  @Column(name="pkexternalid__c")
  private String pk;

  @Column(name="name")
  private String name;

  @Column(name="isocountrycode__c")
  private String isoCountryCode;

  @Override
  public String display() {
    return getIsoCountryCode();
  }

}
