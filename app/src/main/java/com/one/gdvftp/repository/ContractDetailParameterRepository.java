package com.one.gdvftp.repository;

import com.one.gdvftp.entity.ContractDetailParameter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ContractDetailParameterRepository extends JpaRepository<ContractDetailParameter, String> {

}
