package com.one.gdvftp.service;

import com.one.gdvftp.dto.ZentralrufRecordDTO;
import com.one.gdvftp.entity.Contract;
import java.util.List;

public interface ContractService {

  ZentralrufRecordDTO zentralrufRecordDTO(Contract contract);

  int writeZentralrufRecords(String foldername, List<Contract> contracts);

}
