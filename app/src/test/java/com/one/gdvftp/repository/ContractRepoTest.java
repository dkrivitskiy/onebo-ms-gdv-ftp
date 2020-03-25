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
    private final ProductGroup motor = new ProductGroup("m", "Motor");
    private final Contract c = new Contract("1",false,"one", "1111",
            germany, motor, Collections.EMPTY_LIST);

    @Test
    public void findContractsForZentralruf() throws Exception {
        assertThat(manager).isNotNull();
        assertThat(repo).isNotNull();
        persist(c);
        manager.flush();
        val liste = repo.findContractsForZentralruf(10);
        assertThat(liste).isNotEmpty();
        System.out.println(liste);
    }

    private void persist(Contract c) {
        manager.persist(c);
        manager.persist(c.getCountry());
        manager.persist(c.getProductGroup());
    }
}