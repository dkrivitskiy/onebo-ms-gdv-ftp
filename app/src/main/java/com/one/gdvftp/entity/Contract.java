package com.one.gdvftp.entity;

import java.time.LocalDate;
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
@Builder
public class Contract implements Display {

  @Id
  @Column(name="pkexternalid__c")
  private String pk;

  @Column(name="isdeleted")
  private Boolean deleted;

  @Column(name="statusone__c")
  private String statusOne;

  @Column(name="name")
  private String name;

  @Column(name="symassid__c")
  private String symassid;

  @Column(name="validto__c")
  private LocalDate validTo;

  @Column(name="acquisitionchannel__c")
  private String acquisitionChannel;


  @ManyToOne
  @JoinColumn(name = "country__r__pkexternalid__c")
  private Country country;

  @ManyToOne
  @JoinColumn(name = "productgroup__r__pkexternalid__c")
  private ProductGroup productGroup;

  @ManyToOne
  @JoinColumn(name = "customer__r__pkexternalid__c")  // strange mapping, because table customer is missing
  private Account Account;

  @Setter
  @OneToMany(mappedBy = "contract", fetch = FetchType.EAGER)
  private List<ContractDetail> details;

  @Override
  public String display() {
    return "Contract(pk=" + this.getPk() + ", deleted=" + this.getDeleted() +
        ", name=" + this.getName() + ", symassid=" + this.getSymassid() +
        ", validTo=" + this.getValidTo() + ", country=" + Display.display(this.getCountry()) +
        ", productGroup=" + Display.display(this.getProductGroup()) +
        ", details=" + Display.display(this.getDetails()) + ")";
  }
}
