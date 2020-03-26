package com.one.gdvftp.service.impl;

import com.one.gdvftp.dto.ZentralrufRecordDTO;
import com.one.gdvftp.entity.Contract;
import com.one.gdvftp.entity.ContractDetail;
import com.one.gdvftp.entity.ContractDetailParameter;
import com.one.gdvftp.entity.Display;
import com.one.gdvftp.repository.ContractRepository;
import com.one.gdvftp.service.ContractException;
import com.one.gdvftp.service.ContractService;
import java.time.LocalDateTime;
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
    val display = contract.toString();  // TODO: maybe implement Contract.display()
    val details = details(contract);
    val parameters = parameters(details);
    return ZentralrufRecordDTO.builder()
        .vuNr(insuranceNumber)
        .vuGstNr(insuranceBranch)
        .vertr(contract.getSymassid())
        .faKz(normalizedLicensePlate(parameters))
        .favDatAb(initialValidFrom(details, display))
        .favDatBis(contract.getValidTo())
        .khVkTk(productType(parameters))
        .schutzbrief(assistance(parameters))
        .build();
  }


  private static LocalDateTime initialValidFrom(List<ContractDetail> details, String display) {
    val result = details.stream().map(ContractDetail::getValidFrom).min(LocalDateTime::compareTo);
    return result.orElseThrow(()->new ContractException("ValidFrom is missing", display));
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
    if(contract.getDeleted()) throw new ContractException("Contract is deleted.", contract.toString());
    val result = contract.getDetails().stream().
        filter(d -> !d.getDeleted()).collect(Collectors.toList());
    return result;
  }

  private static String parameter(String name, List<ContractDetailParameter> params) {
    val list = params.stream().
        filter(p -> name.equals(p.getParameter().getName())).collect(Collectors.toList());
    if(list.isEmpty()) throw new ContractException("Contract does not have parameter "+name, display(params)); // TODO: toString() for List
    if(list.size()>1) throw new ContractException("Contract has more than 1 parameter "+name, display(params)); // TODO: toString() for List
    val result = list.get(0).getValueToShow();
    return result;
  }

  private static <T extends Display> Display display(List<T> params) {
    return () -> {
      val displays = params.stream().map(Display::display).collect(Collectors.toList());
      val s = String.join(", ", displays);
      return "["+s+"]";
    };
  }

  private static String normalizedLicensePlate(List<ContractDetailParameter> params) {
    return parameter("NormalizedLicensePlate", params);
  }

  private static String productType(List<ContractDetailParameter> params) {
    return parameter("productType", params);
  }

  private static Boolean assistance(List<ContractDetailParameter> params) {
    return Boolean.valueOf(parameter("Assistance", params));
  }

}
