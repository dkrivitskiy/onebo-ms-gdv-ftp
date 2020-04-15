package com.one.gdvftp.service.impl;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.mapping;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;
import static java.util.stream.Collectors.toSet;

import com.one.gdvftp.dto.ZentralrufRecordDTO;
import com.one.gdvftp.dto.ZentralrufRecordEncoder;
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
import lombok.extern.log4j.Log4j2;
import lombok.val;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;


@Log4j2
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

  Clock clock = Clock.system(ZoneId.of("CET"));   // This clock can be replaced for tests.


  @Override
  public int writeZentralrufRecords(String foldername, int limit) {
    val today = LocalDate.now(clock);
    // val count = repo.countContractsForZentralruf(today);
    val contracts = repo.findContractsForZentralruf(today, limit);
    val writtenCount = writeZentralrufRecords(today, foldername, contracts);
    return writtenCount;
  }

  public int writeZentralrufRecords(LocalDate today, String foldername, List<Contract> contracts) {
    int writtenCount = 0;
    int errorCount = 0;
    val encoder = new ZentralrufRecordEncoder();

    LocalDate previousDeliveryDate = today.minusDays(1);  // TODO: real implementation
    Integer previousDeliveryNumber = null;
    String prev = previousDeliveryNumber(foldername);
    int deliveryNumber = 1;
    if(!prev.isEmpty()) {
      int prevYear = Integer.valueOf(prev.substring(0,4));
      previousDeliveryNumber = Integer.valueOf(prev.substring(4,7));
      deliveryNumber = previousDeliveryNumber + 1;
      // TODO: start with deliveryNumber=1 if the year changed
    }
    val filename = encoder.filename(insuranceNumber, insuranceBranch, today, deliveryNumber);

    try {
      val file = File.createTempFile(filename, ".tmp");

      // Writing to tempfile
      try (val out = new FileWriter(file)) {
        val header = encoder.header(insuranceNumber, insuranceBranch);
        out.write(header); out.write("\n");

        for (Contract c : contracts) {
          try {
            val dto = zentralrufRecordDTO(c);
            val record = encoder.encode(dto);
            out.write(record); out.write("\n");
            writtenCount++;
          } catch (ContractException e) {
              errorCount++;
              log.error(e.getClass().getName() + ": " + e.getMessage());  // don't write a stacktrace to the log
          } catch (Exception e) {
            errorCount++;
            log.error(e);
          }
        }
        log.info("error count: "+errorCount);

        val footer = encoder.footer(
            today, deliveryNumber, writtenCount,
            previousDeliveryDate, previousDeliveryNumber);
        out.write(footer); out.write("\n");
      }

      // Reading from tempfile
      try {
        transfer.upload(foldername+filename, file);
        log.info("records written: "+writtenCount);
        return writtenCount;
      } finally {
        file.delete();
      }
    } catch (IOException e) {
        throw new RuntimeException(e);
    }
  }

  public ZentralrufRecordDTO zentralrufRecordDTO(Contract contract) {
    val details = details(contract);
    val activeDetail = activeDetail(details(contract));
    val parameters = parameters(activeDetail);
    val productTypes = productTypes(parameters, contract);
    val record = ZentralrufRecordDTO.builder()
        .deckungsArt(deckungsArt(productTypes))
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
        .zulassung(
            isSwitch(productTypes) ? null   // no value for switch contracts
            : zulassung(parameters, contract)
        )
        .build();
    return record;
  }

  private String deckungsArt(List<ContractDetailParameter> productTypes) {
    // TODO: improve
    val list = productTypes.stream()
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

  private static List<ContractDetailParameter> parameters(String name, List<ContractDetailParameter> params, boolean required, Contract contract) {
    val list = params.stream().
        filter(p -> name.equals(p.getParameter().getName())).collect(Collectors.toList());
    if(required && list.isEmpty())
      throw new ContractException("Contract does not have parameter "+name+".", contract);
    return list;
  }

  private static String parameter(String name, List<ContractDetailParameter> params, boolean required, Contract contract) {

    val list = parameters(name, params, required, contract);
    val set = list.stream().map(p -> p.getValueToShow()).collect(toSet());

    if(set.size()>1)
      throw new ContractException("Contract has more than 1 parameter "+name+".", contract);

    if(set.size()==0) {
      if (required) {
        throw new ContractException("Contract has no parameter " + name + ".",
            contract);   // must not happen
      } else {
        return null;
      }
    }

    return set.iterator().next();
  }

  private static String normalizedLicensePlate(List<ContractDetailParameter> params, Contract contract) {
    val plate = parameter("NormalizedLicensePlate", params, true, contract);
    val result = plate.replaceAll("_", " ");
    if(result.contains("Ä") || result.contains("Ö") || result.contains("Ü"))
      throw new ContractException("Normalized license plate contains umlaut \""+result+"\".", contract);
    return result;
  }

  private static List<ContractDetailParameter> productTypes(List<ContractDetailParameter> params, Contract contract) {
    val result = parameters("productType", params, true, contract);
    return result;
  }

  private static Boolean assistance(List<ContractDetailParameter> params, Contract contract) {
    val string = parameter("Assistance", params, false, contract);
    if(string==null) return false;
    return Boolean.valueOf(string);
  }

  // calculate the deductibles (for KH, TK, VK)
  private static Map<String, Integer> deductibles(List<ContractDetailParameter> params, Contract contract) {
    val deductibles = parameters("deductible", params, true, contract);
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
    val hsn = parameter("vehicleHSN", params, true, contract);
    if(hsn==null) throw new ContractException("HSN must not be null.", contract);
    return Short.valueOf(hsn);
  }

  private static String tsn(List<ContractDetailParameter> params, Contract contract) {
    return parameter("vehicleTSN", params, true, contract);
  }

  private static LocalDate zulassung(List<ContractDetailParameter> params, Contract contract) {
    val string = parameter("firstRegistrationDateInsured", params, true, contract);
    if(string==null) throw new ContractException("firstRegistrationDateInsured is null.", contract);
    try {
      val milliseconds = Long.valueOf(string);
      val localDateTime = LocalDateTime.ofInstant(
          Instant.ofEpochMilli(milliseconds),
          ZoneId.of("CET"));  // TODO: ensure that this is the correct timezone
      val result = localDateTime.toLocalDate();
      return result;
    } catch(NumberFormatException e) {
      throw new ContractException("Can not parse firstRegistrationDateInsured: "+string+".", contract);
    }
  }

  // The productTypes name must contain "Switch".
  private boolean isSwitch(List<ContractDetailParameter> productTypes) {
    val isSwitch = productTypes.stream().map((p -> p.getProductParameter().getName()))
        .allMatch(name -> name.contains("Switch"));
    return isSwitch;
  }


  /**
   * Returns the largest delivery number (7 digit including year) from the folder in S3.
   */
  private String previousDeliveryNumber(String foldername) {

    // filename contains two 7 digit numbers:
    //   1) VuNr + VuGstNr
    //   2) year + delivery number
    // Example: dat.9496001.aza.2020001
    val pattern = "^\\D+\\d{7}\\D+\\d{7}$";

    val filename = transfer.listFolder(foldername).stream()
        .map(path -> path.split("/"))           // split the path into parts
        .filter(parts -> parts.length == 2)           // path must have 2 parts (foldername+filename)
        .map(parts -> parts[1])                       // take the filename
        .filter(name -> name.matches(pattern))        // filter for correct format
        .map(name -> name.substring(name.length()-7)) // take the last 7 chars
        .max(String::compareTo);                      // get the largest number (as String)

    return filename.orElse("");
  }

}
