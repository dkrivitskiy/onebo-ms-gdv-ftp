package com.one.gdvftp.repository;

import com.one.gdvftp.entity.Contract;

import java.time.LocalDate;
import java.util.List;

public interface ContractRepositoryExtension {

    Long countContractsForZentralruf(LocalDate today);
    List<Contract> findContractsForZentralruf(LocalDate today, int limit);
    List<Contract> findContractsForVwbRequest(int limit);
}
