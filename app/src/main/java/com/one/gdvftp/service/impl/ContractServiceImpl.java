package com.one.gdvftp.service.impl;

import com.one.gdvftp.dto.ZentralrufRecordDTO;
import com.one.gdvftp.entity.Contract;
import com.one.gdvftp.entity.ContractDetail;
import com.one.gdvftp.entity.ContractDetailParameter;
import com.one.gdvftp.repository.ContractRepository;
import com.one.gdvftp.service.ContractException;
import com.one.gdvftp.service.ContractService;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.stream.Collectors;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;


@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@SuppressWarnings("UnnecessaryLocalVariable")
public class ContractServiceImpl implements ContractService {

  @Value("${message.insurance.insurance-number}")  // TODO: can this be a constructor parameter?
  private Short insuranceNumber;

  @Value("${message.insurance.insurance-branch}")  // TODO: can this be a constructor parameter?
  private Short insuranceBranch;

  private final @NonNull EntityManager em;

  private final @NonNull ContractRepository repo;


  @Override
  public ZentralrufRecordDTO zentralrufRecordDTO(Contract contract) throws ContractException {
    val details = details(contract);
    val parameters = parameters(details);
    return ZentralrufRecordDTO.builder()
        .vuNr(insuranceNumber)
        .vuGstNr(insuranceBranch)
        .vertr(contract.getSymassid())
        .faKz(normalizedLicensePlate(parameters, contract))
        .favDatAb(initialValidFrom(details, contract))
        .favDatBis(contract.getValidTo())
        .khVkTk(productType(parameters, contract))
        .schutzbrief(assistance(parameters, contract))
        .tkSb(deductibles(parameters, contract))
        .hsn(hsn(parameters, contract))
        .tsn(tsn(parameters, contract))
        .zulassung(zulassung(parameters, contract))
        .build();
  }


  private static LocalDateTime initialValidFrom(List<ContractDetail> details, Contract contract) {
    val result = details.stream().map(ContractDetail::getValidFrom).min(LocalDateTime::compareTo);
    return result.orElseThrow(()->new ContractException("ValidFrom is missing", contract));
  }

  // Returns the parameters which are not deleted.
  private static List<ContractDetailParameter> parameters(List<ContractDetail> details) {
    val result = details.stream().flatMap(
        d -> d.getParameters().stream().filter(p -> !p.getDeleted())
    ).collect(Collectors.toList());
    return result;
  }

  // The contract must not be deleted.
  // Return all ContractDetails which are not deleted.
  private static List<ContractDetail> details(Contract contract) throws ContractException {
    if(contract.getDeleted()) throw new ContractException("Contract is deleted.", contract);
    val result = contract.getDetails().stream().
        filter(d -> !d.getDeleted()).collect(Collectors.toList());
    return result;
  }

  private static List<ContractDetailParameter> parameters(String name, List<ContractDetailParameter> params, Contract contract) {
    val list = params.stream().
        filter(p -> name.equals(p.getParameter().getName())).collect(Collectors.toList());
    if(list.isEmpty())
      throw new ContractException("Contract does not have parameter "+name, contract);
    return list;
  }

  private static String parameter(String name, List<ContractDetailParameter> params, Contract contract) {
    val list = parameters(name, params, contract);
    if(list.size()>1)
      throw new ContractException("Contract has more than 1 parameter "+name, contract);
    val result = list.get(0).getValueToShow();
    return result;
  }

  private static String normalizedLicensePlate(List<ContractDetailParameter> params, Contract contract) {
    return parameter("NormalizedLicensePlate", params, contract);
  }

  private static List<ContractDetailParameter> productType(List<ContractDetailParameter> params, Contract contract) {
    val result = parameters("productType", params, contract);
    return result;
  }

  private static Boolean assistance(List<ContractDetailParameter> params, Contract contract) {
    return Boolean.valueOf(parameter("Assistance", params, contract));
  }

  private static List<ContractDetailParameter> deductibles(List<ContractDetailParameter> params, Contract contract) {
    return parameters("deductible", params, contract);
  }

  private static Short hsn(List<ContractDetailParameter> params, Contract contract) {
    return Short.valueOf(parameter("vehicleHSN", params, contract));
  }

  private static String tsn(List<ContractDetailParameter> params, Contract contract) {
    return parameter("vehicleTSN", params, contract);
  }

  private static LocalDate zulassung(List<ContractDetailParameter> params, Contract contract) {
    val milliseconds = Long.valueOf(parameter("firstRegistrationDateInsured", params, contract));
    val localDateTime = LocalDateTime.ofInstant(
        Instant.ofEpochMilli(milliseconds), ZoneId.of("CET"));  // TODO: ensure that this is the correct timezone
    val result = localDateTime.toLocalDate();
    return result;
  }
}
