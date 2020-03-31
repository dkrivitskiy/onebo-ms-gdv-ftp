package com.one.gdvftp.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Table(name = "contractdetailparameter__c")
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class ContractDetailParameter implements Display {

  @Id
  @Column(name="pkexternalid__c")
  private String pk;

  @Column(name="isdeleted")
  private Boolean deleted;

  @Column(name="valuetoshow__c")
  private String valueToShow;

  @ManyToOne
  @JoinColumn(name = "parameter__r__pkexternalid__c")
  private Parameter parameter;

  @ManyToOne
  @JoinColumn(name = "contractdetail__r__pkexternalid__c")
  @ToString.Exclude private ContractDetail contractDetail;

  @ManyToOne
  @JoinColumn(name = "productparameter__r__pkexternalid__c")
  private ProductParameter productParameter;

  @Override
  public String display() {
    return getParameter().getName()+"="+getValueToShow()+"<"+getProductParameter().getName()+">";
  }
}
