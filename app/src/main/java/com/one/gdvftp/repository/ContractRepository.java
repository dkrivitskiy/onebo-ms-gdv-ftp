package com.one.gdvftp.repository;

import com.one.gdvftp.entity.Contract;
import lombok.val;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ContractRepository extends JpaRepository<Contract, String> {

  Page<Contract> findByCountryIsoCountryCodeAndProductGroupNameAndDeleted(String country, String group, Boolean deleted, Pageable pageable);

  default List<Contract> findContractsForZentralruf(int limit) {
    val result = findByCountryIsoCountryCodeAndProductGroupNameAndDeleted(
            "DE", "Motor", false, PageRequest.of(0, limit)).getContent();
    return result;
  }
}
