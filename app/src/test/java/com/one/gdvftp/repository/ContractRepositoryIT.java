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

  @Test @Ignore // TODO: put at least one matching contract in the dev database
  public void testFindContractsForZentralruf() {

    val zentralrufContractsTop10 = repo.findContractsForZentralruf(LocalDate.of(1970,1,1), 10);

    assertThat(zentralrufContractsTop10).isNotEmpty();

    zentralrufContractsTop10.forEach(c -> {
      assertThat(c.getDeleted()).isFalse();
      assertThat(c.getCountry().getIsoCountryCode()).isEqualTo("DE");
      assertThat(c.getProductGroup().getName()).isEqualTo("Motor");
    });

  }
}
