package com.one.gdvftp.repository;

import com.one.gdvftp.entity.Contract;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Root;
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
    public List<Contract> findContractsForVwbRequest(int limit) {
        val builder = em.getCriteriaBuilder();
        val query = builder.createQuery(Contract.class);
        val contract = query.from(Contract.class);
        val details = contract.joinList("details", JoinType.LEFT);
        val parameters = details.joinList("parameters", JoinType.LEFT);

        query.where(
            builder.isFalse(contract.get("deleted"))
           ,builder.equal(contract.get("statusOne"), "Active")
           ,builder.equal(contract.get("country").get("isoCountryCode"), "DE")
           ,builder.equal(contract.get("productGroup").get("name"), "Motor")
           ,builder.isFalse(details.get("deleted"))
           ,builder.equal(details.get("status"), "ACTIVE")
           ,builder.equal(parameters.get("productParameter").get("apiKey"), "car.product-type")
           ,builder.like(parameters.get("productParameter").get("name"), "%Switch%")
            // TODO: Portfolio Owner is not equal to Check24
            // TODO: VWB Versichererwechsel Status is equal to Not Requested
        );

        val result = em.createQuery(query).setMaxResults(limit).getResultList();

        return result;
    }

    @Override
    @SuppressWarnings("UnnecessaryLocalVariable")
    public List<Contract> findContractsForZentralruf(LocalDate today, int limit) {

        val builder = em.getCriteriaBuilder();
        val query = builder.createQuery(Contract.class);
        val contract = query.from(Contract.class);

        whereContractsForZentralruf(contract, query, builder, today);
        val result = em.createQuery(query).setMaxResults(limit).getResultList();

        return result;
    }

    @Override
    @SuppressWarnings("UnnecessaryLocalVariable")
    public Long countContractsForZentralruf(LocalDate today) {

        val builder = em.getCriteriaBuilder();
        val query = builder.createQuery(Long.class);
        val contract = query.from(Contract.class);
        val count = query.select(builder.count(contract));

        whereContractsForZentralruf
            (contract, query, builder, today);
        val result = em.createQuery(query).getSingleResult();

        return result;
    }

    private <T> void whereContractsForZentralruf(Root<Contract> contract, CriteriaQuery<T> query, CriteriaBuilder builder, LocalDate today) {

        val details = contract.joinList("details", JoinType.LEFT);
        query.where(
            builder.isFalse(contract.get("deleted")),
            builder.equal(contract.get("statusOne"), "Active"),
            builder.equal(contract.get("country").get("isoCountryCode"), "DE"),
            builder.equal(contract.get("productGroup").get("name"), "Motor"),
            builder.isFalse(details.get("deleted")),
            builder.equal(details.get("status"), "ACTIVE"),
            builder.or(
                builder.isNull(contract.get("validTo")),
                builder.greaterThan(contract.get("validTo"), today)
            )
        );
    }
}
