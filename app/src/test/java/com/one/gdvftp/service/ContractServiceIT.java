package com.one.gdvftp.service;

import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;

import com.one.gdvftp.boot.Application;
import com.one.gdvftp.entity.Contract;
import com.one.gdvftp.entity.ContractDetail;
import com.one.gdvftp.entity.ContractDetailParameter;
import com.one.gdvftp.entity.Parameter;
import com.one.gdvftp.entity.ProductParameter;
import com.one.gdvftp.repository.ContractRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Collectors;

import lombok.val;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class, webEnvironment = SpringBootTest.WebEnvironment.NONE)
public class ContractServiceIT {

  @Autowired
  private ContractRepository repo;

  @Autowired
  private ContractService service;

  @Test @Ignore // TODO: put at least one matching contract in the dev database
  public void testConvertSomeContractsToZentralrufDTO() throws ContractException {

    val contractsTops = repo.findContractsForZentralruf(
            LocalDate.of(1970,1,1), 10);

    assertThat(contractsTops).isNotEmpty();
      System.out.println("found "+contractsTops.size()+" contracts");

    val records = contractsTops.stream().
        map(c -> {
          try {return service.zentralrufRecordDTO(c);}
          catch(Exception e) { System.err.println(e.getMessage()); return null;}
        }).
        filter(Objects::nonNull).
        collect(Collectors.toList());

    // TODO: remove later
    records.forEach(System.out::println);
    System.out.println((""+records.size()+" records"));
  }

  @Test
  public void testWriteZentralrufOneRecord() {
    val parameters = Arrays.asList(
        parameter("productType", "KH", "KH.insurances.product"),
        parameter("deductible", "0", "KH.insurances.deduction"),
        parameter("NormalizedLicensePlate", "ABC1234"),
        parameter("Assistance", "true"),
        parameter("vehicleHSN", "1234"),
        parameter("vehicleTSN", "TSN"),
        parameter("firstRegistrationDateInsured", "1000000000")
    );
    val detail = ContractDetail.builder()
        .deleted(false)
        .status("ACTIVE")
        .validFrom(LocalDateTime.of(2020,1,1,0,0))
        .parameters(parameters)
        .build();
    val contract = Contract.builder()
        .deleted(false)
        .symassid("symass1234")
        .details(singletonList(detail))
        .build();
    int count = service.writeZentralrufRecords("test/", singletonList(contract));
    assertThat(count).isPositive();
  }

  private static ContractDetailParameter parameter(String name, String value) {
    return ContractDetailParameter.builder().deleted(false)
        .parameter(Parameter.builder().name(name).build())
        .valueToShow(value).build();
  }

  private static ContractDetailParameter parameter(String name, String value, String product) {
    return ContractDetailParameter.builder().deleted(false)
        .parameter(Parameter.builder().name(name).build())
        .valueToShow(value)
        .productParameter(ProductParameter.builder().bindingFieldToSubmit("KH.insurances.deduction").build())
        .build();
  }

}
