package com.one.gdvftp.dto;

import static java.util.stream.Collectors.toMap;
import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.util.AbstractMap.SimpleEntry;
import java.util.Collections;
import java.util.stream.Stream;
import lombok.val;
import org.junit.Test;

public class ZentralrufRecordDtoTest {

  @Test
  public void testRecordVK() {
    val date = LocalDate.of(2021,12,30);
    val deductible = Stream.of(new SimpleEntry<>("KH", 0), new SimpleEntry<>("TK", 2345), new SimpleEntry<>("VK", 6789))
        .collect(toMap(SimpleEntry::getKey, SimpleEntry::getValue));

    val dto = new ZentralrufRecordDTO(
        123,"vertr1234567890","fakz12345678", date.minusDays(10), date, 12,
        "VK", true, deductible , 4321, "tsn", date.minusDays(91));

    val rec = dto.toRecord();
    assertThat(rec).isEqualTo(
        "0123"+"vertr1234567890     "+"fakz12345678"+"000"+"20122021"+"30122021"+"012"+
        "00000000"+"VK"+"1"+"2345"+"6789"+"4321"+"tsn"+"2021");
  }

  @Test
  public void testRecordTK() {
    val date = LocalDate.of(2021,12,30);
    val deductible = Stream.of(new SimpleEntry<>("KH", 0), new SimpleEntry<>("TK", 2345))
        .collect(toMap(SimpleEntry::getKey, SimpleEntry::getValue));

    val dto = new ZentralrufRecordDTO(
        123,"vertr1234567890","fakz12345678", date.minusDays(10), date, 12,
        "TK", true, deductible , 4321, "tsn", date.minusDays(91));

    val rec = dto.toRecord();
    assertThat(rec).isEqualTo(
        "0123"+"vertr1234567890     "+"fakz12345678"+"000"+"20122021"+"30122021"+"012"+
            "00000000"+"TK"+"1"+"2345"+"0000"+"4321"+"tsn"+"2021");
  }

  @Test
  public void testRecordKH() {
    val date = LocalDate.of(2021,12,30);

    val dto = new ZentralrufRecordDTO(
        123,"vertr1234567890","fakz12345678", date.minusDays(10), date, 12,
        "KH", true, Collections.EMPTY_MAP, 4321, "tsn", date.minusDays(91));

    val rec = dto.toRecord();
    assertThat(rec).isEqualTo(
        "0123"+"vertr1234567890     "+"fakz12345678"+"000"+"20122021"+"30122021"+"012"+
            "00000000"+"KH"+"1"+"0000"+"0000"+"4321"+"tsn"+"2021");
  }

}
