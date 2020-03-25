package com.one.gdvftp.repository;

import com.one.gdvftp.entity.Contract;
import com.one.gdvftp.entity.ContractDetail;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.criteria.Join;
import java.time.LocalDateTime;
import java.util.List;

@Repository
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ContractRepositoryImpl implements ContractRepositoryExtension {

    private final @NonNull EntityManager em;

    @Override
    public List<Contract> findContractsForZentralruf(LocalDateTime after, int limit) {
        val builder = em.getCriteriaBuilder();
        val query = builder.createQuery(Contract.class);

        val contract = query.from(Contract.class);
        val details = contract.join("details");

        query.where(
            builder.isFalse(contract.get("deleted")),
            builder.equal(contract.get("country").get("isoCountryCode"), "DE"),
            builder.equal(contract.get("productGroup").get("name"), "Motor"),
            builder.isFalse(details.get("deleted")),
            builder.greaterThan(details.get("validTo"), after)
        );

        val result = em.createQuery(query).setMaxResults(limit).getResultList();

        return result;
    }
}
