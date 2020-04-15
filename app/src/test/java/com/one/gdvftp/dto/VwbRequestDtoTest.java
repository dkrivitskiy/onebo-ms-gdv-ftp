package com.one.gdvftp.dto;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import lombok.val;
import org.junit.Test;

public class VwbRequestDtoTest {

  @Test
  public void testFilename() {
    val today = LocalDate.of(2020,1,31);
    val name = VwbRequestDTO.filename(1234, 567, today, 890);
    assertThat(name).isEqualTo("dat."+"1234"+"567"+".kvb."+"2020"+"890");
  }

  @Test
  public void testHeader() {
    val header = VwbRequestDTO.header(1234, 567);
    assertThat(header.length()).isEqualTo(VwbRequestDTO.SIZE);
    assertThat(header).isEqualTo("KONTROLLE BV"+"8333"+"KVB"+"T"+"1234"+"567"
        +" ");
  }

  @Test
  public void testFooter() {
    val today = LocalDate.of(2020,1,31);
    val footer = VwbRequestDTO.footer(today, 2, 999, today.minusDays(1), 1);
    assertThat(footer.length()).isEqualTo(VwbRequestDTO.SIZE);
    assertThat(footer).isEqualTo("KONTROLLE BN"+"20200131"+"0002"+"00000999"+"20200130"+"0001"
        +" ");
  }

  @Test
  public void testRecord() {
    val date = LocalDate.of(2020,1,31);

    val dto = new VwbRequestDTO (8333, 1, "vsnr-abcdefghijklmno", "fin-abcdefghijklm", date, '1');

    val rec = dto.toRecord();
    assertThat(rec).isEqualTo(
        "10"+"8333"+"001"+"1"+"vsnr-abcdefghijklmno"+"01"+"fin-abcdefghijklm"+"31012020"+"1"
            +"");
  }
}
