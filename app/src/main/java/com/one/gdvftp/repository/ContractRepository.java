package com.one.gdvftp.repository;

import com.one.gdvftp.entity.Contract;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ContractRepository extends JpaRepository<Contract, String> {

  // used for Zentralruf
  Page<Contract> findByCountryIsoCountryCodeAndProductGroupNameAndDeleted(String country, String group, Boolean deleted, Pageable pageable);

}
