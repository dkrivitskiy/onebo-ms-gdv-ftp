package com.one.gdvftp.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.one.gdvftp.boot.Application;
import com.one.gdvftp.repository.ContractRepository;

import java.time.LocalDate;
import java.util.Objects;
import java.util.stream.Collectors;

import lombok.val;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class, webEnvironment = SpringBootTest.WebEnvironment.NONE)
public class ContractServiceIT {

  @Autowired
  private ContractRepository repo;

  @Autowired
  private ContractService service;

  @Test
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
  }

  @Test
  public void testWriteZentralrufRecords() {
    int count = service.writeZentralrufRecords();
    assertThat(count).isPositive();
  }

}
