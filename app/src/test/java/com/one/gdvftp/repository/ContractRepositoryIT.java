package com.one.gdvftp.repository;

import com.one.gdvftp.boot.Application;

import lombok.val;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;



@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class, webEnvironment = SpringBootTest.WebEnvironment.NONE)
public class ContractRepositoryIT {


  @Autowired
  private ContractRepository repo;

  @Test
  public void testRetrieveSomeContracts() {

      val contractsTop10 = repo.findAll(PageRequest.of(0, 10)).getContent();

      assertThat(contractsTop10).isNotEmpty();

  }

  @Test
  public void testFindContractsForZentralruf() {

    val zentralrufContractsTop10 = repo.findContractsForZentralruf(LocalDate.of(1970,1,1), 10);

    assertThat(zentralrufContractsTop10).isNotEmpty();

    zentralrufContractsTop10.forEach(c -> {
      assertThat(c.getDeleted()).isFalse();
      assertThat(c.getCountry().getIsoCountryCode()).isEqualTo("DE");
      assertThat(c.getProductGroup().getName()).isEqualTo("Motor");
    });

  }

  @Test
  public void testFindContractsForVwbRequest() {

    val someContracts = repo.findContractsForVwbRequest(10);

    assertThat(someContracts).isNotEmpty();

    someContracts.forEach(c -> {
      assertThat(c.getDeleted()).isFalse();
      assertThat(c.getStatusOne()).isEqualTo("Active");
      assertThat(c.getAcquisitionChannel()).isNotEqualTo("Check24");
      assertThat(c.getCountry().getIsoCountryCode()).isEqualTo("DE");
      assertThat(c.getProductGroup().getName()).isEqualTo("Motor");
      val details = c.getDetails();
      assertThat(details).isNotEmpty();
      assertThat(details.size()).isEqualTo(1);
      details.forEach(d-> {
        assertThat(d.getDeleted()).isFalse();
        assertThat(d.getStatus()).isEqualTo("ACTIVE");
        val params = d.getParameters();
        assertThat(params).isNotEmpty();
        boolean isSwitch = params.stream().map(p -> p.getProductParameter())
            .anyMatch(pp->"car.product-type".equals(pp.getApiKey()) && pp.getName().contains("Switch"));
        assertThat(isSwitch).isTrue();
      });
    });

  }
}
