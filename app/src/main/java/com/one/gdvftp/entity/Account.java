package com.one.gdvftp.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Table(name = "account")
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
public class Account {

  @Id
  @Column(name="pkexternalid__c")
  private String pk;

  @Column(name="firstname")
  private String firstName;

  @Column(name="lastname")
  private String lastName;

  /**
   * 0 - male
   * 1 - female
   */
  @Column(name="gen_sex__c")
  private String genSex;

  @Column(name="billingcity")
  private String billingCity;

  @Column(name="billingpostalcode")
  private String billingPostalCode;

  @Column(name="billingstreet")
  private String billingStreet;

  @Column(name="addresshousenumber__c")
  private String addressHouseNumber;

  @ManyToOne
  @JoinColumn(name = "country__r__pkexternalid__c")
  private Country country;

}
