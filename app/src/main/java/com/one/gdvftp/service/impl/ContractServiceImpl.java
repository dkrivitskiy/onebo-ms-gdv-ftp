package com.one.gdvftp.service.impl;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.mapping;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

import com.one.gdvftp.dto.ZentralrufRecordDTO;
import com.one.gdvftp.entity.Contract;
import com.one.gdvftp.entity.ContractDetail;
import com.one.gdvftp.entity.ContractDetailParameter;
import com.one.gdvftp.repository.ContractRepository;
import com.one.gdvftp.service.ContractException;
import com.one.gdvftp.service.ContractService;
import com.one.gdvftp.service.FileTransfer;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@SuppressWarnings("UnnecessaryLocalVariable")
public class ContractServiceImpl implements ContractService {

  @Value("${message.insurance.insurance-number}")  // TODO: can this be a constructor parameter?
  private Short insuranceNumber;

  @Value("${message.insurance.insurance-branch}")  // TODO: can this be a constructor parameter?
  private Short insuranceBranch;

  private final @NonNull ContractRepository repo;

  private final @NonNull FileTransfer transfer;

  Clock clock = Clock.system(ZoneId.of("CET")); // can be changed for tests


  @Override
  public int writeZentralrufRecords(String foldername, List<Contract> contracts) {
    int writtenCount = 0;
    int errorCount = 0;
    LocalDate previousDeliveryDate = null;  // TODO: implement
    Integer previousDeliveryNumber = null;  // TODO: implement
    int deliveryNumber = previousDeliveryNumber==null ? 1 : previousDeliveryNumber+1;
    val now = LocalDate.now(clock);
    val filename = ZentralrufRecordDTO.filename(insuranceNumber, insuranceBranch, now, deliveryNumber);
    try {
      val file = File.createTempFile(filename, ".tmp");

      // Writing to tempfile
      try (val out = new FileWriter(file)) {
        val header = ZentralrufRecordDTO.header(insuranceNumber, insuranceBranch);
        out.write(header); out.write("\n");

        for (Contract c : contracts) {
          try {
            val dto = zentralrufRecordDTO(c);
            val record = dto.toRecord();
            out.write(record); out.write("\n");
            writtenCount++;
          } catch (ContractException e) {
            errorCount++;
            System.err.println(e.getMessage()); // TODO: logging
          } catch (Exception e) {
            errorCount++;
            e.printStackTrace(); // TODO: logging
          }
        }
        System.out.println("error count: "+errorCount); // TODO: logging

        val footer = ZentralrufRecordDTO.footer(
            now, deliveryNumber, writtenCount,
            previousDeliveryDate, previousDeliveryNumber);
        out.write(footer); out.write("\n");
      } catch (Throwable e) {
        e.printStackTrace();  // TODO: logging
      }

      // Reading from tempfile
      try {
        transfer.upload(foldername+filename, file);
        System.out.println("records written: "+writtenCount); // TODO: logging
        return writtenCount;
      } finally {
        file.delete();
      }
    } catch (IOException e) {
        throw new RuntimeException(e);
    }
  }

  @Override
  public ZentralrufRecordDTO zentralrufRecordDTO(Contract contract) {
    val details = details(contract);
    val activeDetail = activeDetail(details(contract));
    val parameters = parameters(activeDetail);
    val record = ZentralrufRecordDTO.builder()
        .deckungsArt(deckungsArt(productType(parameters, contract)))
        .vuNr(insuranceNumber)
        .vuGstNr(insuranceBranch)
        .vertr(contract.getSymassid())
        .faKz(normalizedLicensePlate(parameters, contract))
        .favDatAb(initialValidFrom(details, contract))
        .favDatBis(contract.getValidTo())
        .schutzbrief(assistance(parameters, contract))
        .sb(deductibles(parameters, contract))
        .hsn(hsn(parameters, contract))
        .tsn(tsn(parameters, contract))
        .zulassung(zulassung(parameters, contract))
        .build();
    return record;
  }

  private String deckungsArt(List<ContractDetailParameter> productType) {
    // TODO: improve
    val list = productType.stream()
        .map(cdp -> cdp.getProductParameter().getBindingFieldToSubmit()).collect(Collectors.toList());
    String art = list.stream().map(s -> s.substring(0,2)).collect(Collectors.joining());
    if(art.contains("KH"))
      if(art.contains("TK"))
        if(art.contains("VK"))
          return("VK");
        else
          return("TK");
      else
        return("KH");
    else
      return art;
  }


  /** get the minimum getValidFrom of the details */
  private static LocalDate initialValidFrom(List<ContractDetail> details, Contract contract) {
    val dateTime = details.stream().map(ContractDetail::getValidFrom).min(LocalDateTime::compareTo);
    val date = dateTime.orElseThrow(()->new ContractException("ValidFrom is missing", contract)).toLocalDate();
    return date;
  }

  // Returns the parameters which are not deleted.
  private static List<ContractDetailParameter> parameters(ContractDetail detail) {
    val result = detail.getParameters().stream().filter(p -> !p.getDeleted()).collect(Collectors.toList());
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

  // There must be one active detail.
  private static ContractDetail activeDetail(List<ContractDetail> details) {
    val contract = details.get(0).getContract(); // details must not be empty nor null
    val size = details.size();
    val list = details.stream().
        filter(d -> "ACTIVE".equals(d.getStatus())).collect(Collectors.toList());
    if(list.isEmpty())
      throw new ContractException("Contract does not have active details.", contract);
    if(list.size()>1)
      throw new ContractException("Contract has more than 1 active detail.", contract);
    return list.get(0);
  }

  private static List<ContractDetailParameter> parameters(String name, List<ContractDetailParameter> params, Contract contract) {
    val list = params.stream().
        filter(p -> name.equals(p.getParameter().getName())).collect(Collectors.toList());
    if(list.isEmpty())
      throw new ContractException("Contract does not have parameter "+name+".", contract);
    return list;
  }

  private static String parameter(String name, List<ContractDetailParameter> params, Contract contract) {
    val list = parameters(name, params, contract);
// TODO: fix this problem for zulassung
//    if(list.size()>1)
//      throw new ContractException("Contract has more than 1 parameter "+name, contract);
    val result = list.get(0).getValueToShow();
    return result;
  }

  private static String normalizedLicensePlate(List<ContractDetailParameter> params, Contract contract) {
    val plate = parameter("NormalizedLicensePlate", params, contract);
    val result = plate.replaceAll("_", " ");
    return result;
  }

  private static List<ContractDetailParameter> productType(List<ContractDetailParameter> params, Contract contract) {
    val result = parameters("productType", params, contract);
    return result;
  }

  private static Boolean assistance(List<ContractDetailParameter> params, Contract contract) {
    return Boolean.valueOf(parameter("Assistance", params, contract));
  }

  // calculate the deductibles (for KH, TK, VK)
  private static Map<String, Integer> deductibles(List<ContractDetailParameter> params, Contract contract) {
    val deductibles = parameters("deductible", params, contract);
    val size = deductibles.size();
    val map = deductibles.stream().collect(
        groupingBy(p -> StringUtils.left(p.getProductParameter().getBindingFieldToSubmit(), 2),
        mapping(p -> p.getValueToShow(), toList())
    ));
    val display = StringUtils.join(map);
    // sanity check
    map.values().forEach(list -> {
      if(list.isEmpty()) throw new ContractException("Missing deductible: "+display, contract);
      if(list.size()>1)  throw new ContractException("Multiple deductibles: "+display, contract);
    });
    val result = map.entrySet().stream().collect(
        toMap(Entry::getKey, e -> Integer.valueOf(e.getValue().get(0))));
    return result;
  }

  private static Short hsn(List<ContractDetailParameter> params, Contract contract) {
    val hsn = parameter("vehicleHSN", params, contract);
    if(hsn==null) throw new ContractException("HSN must not be null.", contract);
    return Short.valueOf(hsn);
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
