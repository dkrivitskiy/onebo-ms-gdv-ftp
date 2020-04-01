package com.one.gdvftp.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.one.gdvftp.dto.ZentralrufRecordDTO;
import java.time.LocalDate;
import java.util.Collections;
import lombok.val;
import org.junit.Test;

public class ZentralrufRecordDtoTest {

  @Test
  public void testRecord() {
    val date = LocalDate.of(2021,12,30);
    val dto = new ZentralrufRecordDTO(
        123,"vertr1234567890","fakz12345678", date.minusDays(10), date, 12,
        3, true, Collections.emptyList(), 4321, "tsn", date.minusDays(91));
    val rec = dto.toRecord();
    assertThat(rec).isEqualTo(
        "0123"+"vertr1234567890     "+"fakz12345678"+"000"+"20122021"+"30122021"+"012"+
        "00000000"+"03"+"1"+"00000000"+"4321"+"tsn"+"2021");
  }

}
