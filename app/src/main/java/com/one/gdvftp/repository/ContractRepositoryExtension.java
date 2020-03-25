package com.one.gdvftp.repository;

import com.one.gdvftp.entity.Contract;

import java.time.LocalDate;
import java.util.List;

public interface ContractRepositoryExtension {
    List<Contract> findContractsForZentralruf(LocalDate after, int limit);
}
