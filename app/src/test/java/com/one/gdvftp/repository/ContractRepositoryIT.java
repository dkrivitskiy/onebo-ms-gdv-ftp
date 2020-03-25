package com.one.gdvftp.repository;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import com.one.gdvftp.boot.Application;

import lombok.val;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class, webEnvironment = SpringBootTest.WebEnvironment.NONE)
public class ContractRepositoryIT {


  @Autowired
  private ContractRepository repo;

  @Test
  public void testRetrieveSomeContracts() {


      val contractsTop10 = repo.findAll(PageRequest.of(0, 10)).getContent();

      val size = contractsTop10.size();
      assertTrue("No records retrieved from database.",size>0);

      // TODO: remove later
      System.out.println("found "+size+" contracts");
      contractsTop10.forEach(System.out::println);

  }

  @Test
  public void testRetrieveContractsOfGermanyAndMotor() {

    val isoCode = "DE";
    val group = "Motor";

    val germanContractsTop10 = repo.findByCountryIsoCountryCodeAndProductGroupNameAndDeleted(
            isoCode, group, false, PageRequest.of(0, 10)).getContent();

    val size = germanContractsTop10.size();
    assertTrue("No records retrieved from database.",size>0);

    germanContractsTop10.forEach(c -> {
      assertEquals("CountryIsoCode is wrong.", isoCode, c.getCountry().getIsoCountryCode());
      assertEquals("ProductGroupName is wrong.", group, c.getProductGroup().getName());
    });
  }
}
