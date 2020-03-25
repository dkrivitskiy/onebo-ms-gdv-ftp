package com.one.gdvftp.repository;

import com.one.gdvftp.boot.Application;
import com.one.gdvftp.entity.Contract;
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

import java.util.Collections;

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

    private final Country germany = new Country("de", "Deutschland", "DE");
    private final Country schweiz = new Country("ch", "Schweiz", "CH");
    private final ProductGroup motor = new ProductGroup("m", "Motor");
    private final ProductGroup haus = new ProductGroup("h", "Hausrat");
    private final Contract good = new Contract("1",false,"one", "1111",
            germany, motor, Collections.EMPTY_LIST);

    @Test
    public void findContractsForZentralruf() throws Exception {
        assertThat(manager).isNotNull();
        assertThat(repo).isNotNull();
        persist(good);
        persist(good.withPk("2").withDeleted(true).withDetails(Collections.EMPTY_LIST));
        persist(good.withPk("3").withCountry(schweiz).withDetails(Collections.EMPTY_LIST));
        persist(good.withPk("4").withProductGroup(haus).withDetails(Collections.EMPTY_LIST));

        manager.flush();
        // All must be persisted.
        assertThat(repo.findAll()).hasSize(4);

        val liste = repo.findContractsForZentralruf(10);
        // Only one fits the criteria.
        assertThat(liste).hasSize(1);

        val contract = liste.get(0);
        // That one must be the good one, and all fields must have the correct value.
        assertThat(contract).isEqualToComparingFieldByFieldRecursively(good);
    }

    private void persist(Contract c) {
        manager.persist(c);
        manager.persist(c.getCountry());
        manager.persist(c.getProductGroup());
    }
}