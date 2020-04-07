package com.one.gdvftp.entity;

import java.time.LocalDateTime;
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
@Table(name = "contractdetail__c")
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Getter
@With
@Builder
public class ContractDetail implements Display {

  @Id
  @Column(name="pkexternalid__c")
  private String pk;

  @Column(name="isdeleted")
  private Boolean deleted;

  @Column(name="status__c")
  private String status;

  @Column(name="validfrom__c")
  private LocalDateTime validFrom;

  @OneToMany(mappedBy = "contractDetail", fetch = FetchType.EAGER)
  private List<ContractDetailParameter> parameters;

  @ManyToOne
  @JoinColumn(name = "contract__r__pkexternalid__c")
  @ToString.Exclude private Contract contract;

  @Override
  public String display() {
    return "ContractDetail(pk=" + this.getPk() + ", deleted=" + this.getDeleted() +", status=" + this.getStatus() +
        ", validFrom=" + this.getValidFrom() + ", parameters=" + Display.display(this.getParameters()) + ")";
  }
}
