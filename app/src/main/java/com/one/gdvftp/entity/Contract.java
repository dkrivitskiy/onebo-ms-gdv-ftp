package com.one.gdvftp.entity;

import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import lombok.*;

@Entity
@Table(name = "contract__c")
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Getter
@With
public class Contract {

  @Id
  @Column(name="pkexternalid__c")
  private String pk;

  @Column(name="isdeleted")
  private Boolean deleted;

  @Column(name="name")
  private String name;

  @Column(name="symassid__c")
  private String symassid;

  @ManyToOne
  @JoinColumn(name = "country__r__pkexternalid__c")
  private Country country;

  @ManyToOne
  @JoinColumn(name = "productgroup__r__pkexternalid__c")
  private ProductGroup productGroup;

  @OneToMany(mappedBy = "contract", fetch = FetchType.EAGER)
  private List<ContractDetail> details;

}
