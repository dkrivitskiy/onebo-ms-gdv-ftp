package com.one.gdvftp.repository;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;

import com.one.gdvftp.boot.Application;
import com.one.gdvftp.entity.Contract;
import com.one.gdvftp.entity.ContractDetail;
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
    private final ContractDetail detail = new ContractDetail("d1", false, "ACTIVE", null, emptyList(), null);

    private final Contract goodContract = new Contract("c1",false,"Active","one", "1111", tomorrow,
            germany, motor, emptyList());

    @Test
    public void findContractsForZentralruf() {
        assertThat(manager).isNotNull();
        assertThat(repo).isNotNull();

        // Contracts that fits the criteria for Zentralruf (ONEBACK-2444)
        val good = persist(goodContract
            .withDetails(singletonList(detail)));

        // Contracts that do not fit the criteria
        persist(goodContract.withPk("c2").withDeleted(true)
            .withDetails(singletonList(detail.withPk("d2"))));
        persist(goodContract.withPk("c3").withCountry(schweiz)
            .withDetails(singletonList(detail.withPk("d3"))));
        persist(goodContract.withPk("c4").withProductGroup(haus)
            .withDetails(singletonList(detail.withPk("d4"))));
        persist(goodContract.withPk("c5").withValidTo(yesterday)
            .withDetails(singletonList(detail.withPk("d5"))));
        manager.flush();

        // All must be persisted.
        val all = repo.findAll();
        assertThat(all).hasSize(5);

        val liste = repo.findContractsForZentralruf(today, 10);
        // Only one fits the criteria.
        assertThat(liste).isNotEmpty();
        assertThat(liste).hasSize(1);

        val contract = liste.get(0);
        // That one contract must be the goodContract, and all fields must have the correct value.
        assertThat(contract).isEqualToComparingFieldByFieldRecursively(good);
    }

    private Contract persist(Contract c) {
        manager.persist(c.getCountry());
        manager.persist(c.getProductGroup());
        c.setDetails(
            c.getDetails().stream().map(cd ->
            manager.persist(cd.withContract(c))
        ).collect(toList()));
        manager.persist(c);
        return c;
    }
}
