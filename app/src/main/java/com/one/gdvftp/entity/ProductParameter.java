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
@Table(name = "productparameter__c")
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
public class ProductParameter implements Display {

  @Id
  @Column(name="pkexternalid__c")
  private String pk;

  @Column(name="name")
  private String name;

  @Column(name="bindingfieldtosubmit__c")
  private String bindingFieldToSubmit;

  @Column(name="apikey__c")
  private String apiKey;


  @Override
  public String display() {
    return getName();
  }
}
