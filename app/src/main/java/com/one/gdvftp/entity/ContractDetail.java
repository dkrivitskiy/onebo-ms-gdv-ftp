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

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Table(name = "contractdetail__c")
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class ContractDetail {

  @Id
  @Column(name="pkexternalid__c")
  private String pk;

  @Column(name="isdeleted")
  private Boolean deleted;

  @OneToMany(mappedBy = "contractDetail", fetch = FetchType.EAGER)
  private List<ContractDetailParameter> parameters;

  @ManyToOne
  @JoinColumn(name = "contract__r__pkexternalid__c")
  @ToString.Exclude private Contract contract;

}
