package com.one.gdvftp.repository;

import com.one.gdvftp.entity.Contract;

import java.time.LocalDateTime;
import java.util.List;

public interface ContractRepositoryExtension {
    List<Contract> findContractsForZentralruf(LocalDateTime after, int limit);
}
