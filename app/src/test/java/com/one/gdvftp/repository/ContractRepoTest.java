package com.one.gdvftp.repository;

import com.one.gdvftp.boot.Application;
import com.one.gdvftp.entity.Contract;
import com.one.gdvftp.entity.ContractDetail;
import com.one.gdvftp.entity.Country;
import com.one.gdvftp.entity.ProductGroup;
import lombok.val;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;


import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = Application.class)
@DataJpaTest
@TestPropertySource(properties = {
        "spring.jpa.properties.hibernate.default_schema=PUBLIC",
        "spring.jpa.hibernate.ddl-auto=create-drop"
})
public class ContractRepoTest {

    @Autowired
    private TestEntityManager manager;

    @Autowired
    private ContractRepository repo;

    private final LocalDateTime now = LocalDateTime.now();

    private final Country germany = new Country("de", "Deutschland", "DE");
    private final Country schweiz = new Country("ch", "Schweiz", "CH");
    private final ProductGroup motor = new ProductGroup("m", "Motor");
    private final ProductGroup haus = new ProductGroup("h", "Hausrat");
    private final ContractDetail future = new ContractDetail("11",false, now.plusDays(1),null,null);
    private final ContractDetail past = new ContractDetail("12",false, now.minusDays(1),null,null);
    private final Contract goodContract = new Contract("1",false,"one", "1111",
            germany, motor, null);

    @Test
    public void findContractsForZentralruf() throws Exception {
        assertThat(manager).isNotNull();
        assertThat(repo).isNotNull();

        persist(future.withContract(goodContract));
        persist(goodContract.withPk("2").withDeleted(true));
        persist(goodContract.withPk("3").withCountry(schweiz));
        persist(goodContract.withPk("4").withProductGroup(haus));
        persist(past.withContract(goodContract.withPk("5")));



        manager.flush();
        // All must be persisted.
        assertThat(repo.findAll()).hasSize(5);

        val liste = repo.findContractsForZentralruf(now, 10);
        // Only one fits the criteria.
        assertThat(liste).hasSize(1);

        val contract = liste.get(0);
        // That one contract must be the goodContract, and all fields must have the correct value.
        assertThat(contract).isEqualToComparingFieldByFieldRecursively(goodContract);
    }

    private Contract persist(Contract c) {
        manager.persist(c.getCountry());
        manager.persist(c.getProductGroup());
        manager.persist(c);
        return c;
    }

    private ContractDetail persist(ContractDetail c) {
        persist(c.getContract());
        manager.persist(c);
        return c;
    }
}