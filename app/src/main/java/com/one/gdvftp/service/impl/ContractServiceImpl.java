package com.one.gdvftp.service.impl;

import com.one.gdvftp.dto.ZentralrufRecordDTO;
import com.one.gdvftp.entity.Contract;
import com.one.gdvftp.entity.ContractDetail;
import com.one.gdvftp.entity.ContractDetailParameter;
import com.one.gdvftp.entity.Display;
import com.one.gdvftp.repository.ContractRepository;
import com.one.gdvftp.service.ContractException;
import com.one.gdvftp.service.ContractService;
import java.util.List;
import java.util.stream.Collectors;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@SuppressWarnings("UnnecessaryLocalVariable")
public class ContractServiceImpl implements ContractService {

  @Value("${message.insurance.insurance-number}")  // TODO: can this be a constructor parameter?
  private Short insuranceNumber;


  private final @NonNull ContractRepository repo;


  @Override
  public ZentralrufRecordDTO zentralrufRecordDTO(Contract contract) throws ContractException {
    val parameters = parameters(detail(contract));
    return ZentralrufRecordDTO.builder()
        .vuNr(insuranceNumber)
        .vertr(contract.getSymassid())
        .faKz(licensePlate(parameters))
        .build();
  }

  List<Contract> findContractsForZentralruf(int limit) {
    val result = repo.findByCountryIsoCountryCodeAndProductGroupNameAndDeleted(
        "DE", "Motor", false, PageRequest.of(0, limit)).getContent();
    return result;
  }

  // The detail must not be deleted.
  // Returns the parameters which are not deleted.
  private static List<ContractDetailParameter> parameters(ContractDetail detail) {
    if(detail.getDeleted()) throw new ContractException("ContractDetail is deleted.", detail.toString());
    val result = detail.getParameters().stream().filter(p -> !p.getDeleted()).collect(Collectors.toList());
    return result;
  }

  // The contract must not be deleted and
  // must have exactly one detail which is not deleted.
  private static ContractDetail detail(Contract contract) throws ContractException {
    if(contract.getDeleted()) throw new ContractException("Contract is deleted.", contract.toString());
    val details = contract.getDetails().stream().
        filter(d -> !d.getDeleted()).collect(Collectors.toList());
    if(details.isEmpty()) throw new ContractException("Contract has not undeleted details.", contract.toString());
    if(details.size()>1) throw new ContractException("Contract has more than 1 undeleted detail.", contract.toString());
    val result = details.get(0);
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

  private static String licensePlate(List<ContractDetailParameter> params) {
    return parameter("licensePlate", params);
  }

}
