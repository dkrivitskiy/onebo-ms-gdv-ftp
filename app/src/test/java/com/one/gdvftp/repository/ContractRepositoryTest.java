package com.one.gdvftp.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.one.gdvftp.boot.Application;
import com.one.gdvftp.entity.Contract;
import com.one.gdvftp.entity.Country;
import com.one.gdvftp.entity.ProductGroup;
import java.time.LocalDate;
import lombok.val;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = Application.class)
@DataJpaTest
@TestPropertySource(properties = {
        "spring.jpa.properties.hibernate.default_schema=PUBLIC",
        "spring.jpa.hibernate.ddl-auto=create-drop"
})
public class ContractRepositoryTest {

    @Autowired
    private TestEntityManager manager;

    @Autowired
    private ContractRepository repo;

    private final LocalDate today = LocalDate.now();
    private final LocalDate tomorrow = today.plusDays(1);
    private final LocalDate yesterday = today.minusDays(1);

    private final Country germany = new Country("de", "Deutschland", "DE");
    private final Country schweiz = new Country("ch", "Schweiz", "CH");
    private final ProductGroup motor = new ProductGroup("m", "Motor");
    private final ProductGroup haus = new ProductGroup("h", "Hausrat");

    private final Contract goodContract = new Contract("1",false,"one", "1111", tomorrow,
            germany, motor, null);

    @Test
    public void findContractsForZentralruf() {
        assertThat(manager).isNotNull();
        assertThat(repo).isNotNull();

        // Contracts that fits the criteria for Zentralruf (ONEBACK-2444)
        persist(goodContract);

        // Contracts that do not fit the criteria
        persist(goodContract.withPk("2").withDeleted(true));
        persist(goodContract.withPk("3").withCountry(schweiz));
        persist(goodContract.withPk("4").withProductGroup(haus));
        persist(goodContract.withPk("5").withValidTo(yesterday));

        manager.flush();
        // All must be persisted.
        assertThat(repo.findAll()).hasSize(5);

        val liste = repo.findContractsForZentralruf(today, 10);
        // Only one fits the criteria.
        assertThat(liste).isNotEmpty();
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

}