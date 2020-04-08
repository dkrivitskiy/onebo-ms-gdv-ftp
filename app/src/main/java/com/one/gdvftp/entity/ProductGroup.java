package com.one.gdvftp.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Table(name = "productgroup__c")
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
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
