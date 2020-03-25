package com.one.gdvftp.repository;

import com.one.gdvftp.entity.Contract;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.time.LocalDate;
import java.util.List;

@Repository
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ContractRepositoryImpl implements ContractRepositoryExtension {

    private final @NonNull EntityManager em;

    @Override
    @SuppressWarnings("UnnecessaryLocalVariable")
    public List<Contract> findContractsForZentralruf(LocalDate after, int limit) {
        val builder = em.getCriteriaBuilder();
        val query = builder.createQuery(Contract.class);

        val contract = query.from(Contract.class);

        query.where(
            builder.isFalse(contract.get("deleted")),
            builder.equal(contract.get("country").get("isoCountryCode"), "DE"),
            builder.equal(contract.get("productGroup").get("name"), "Motor"),
            builder.greaterThan(contract.get("validTo"), after)
        );

        val result = em.createQuery(query).setMaxResults(limit).getResultList();

        return result;
    }
}
