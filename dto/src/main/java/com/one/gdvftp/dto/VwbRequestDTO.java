package com.one.gdvftp.dto;

import java.time.LocalDate;
import lombok.val;

public class VwbRequestDTO extends DTO {

  static final int SIZE = 45; // 88

  public static String header(int vuNr, int vuGstNr) {
    val h =
        A( 12, "KONTROLLE BV")
      + A( 4, "8333") // Ziel-VU
      + A( 3, "KVB")  // Ziel-Sachgebiet
      + A( 1, "T")    // T or space
      + N( 4, vuNr)      // Absender-VU
      + N( 3, vuGstNr)   // Absender-GS
      + A(SIZE-12-4-3-1-4-3, "")  // filler spaces
      ;
    checkAscii(h);
    checkLength(h, SIZE);
    return h;
  }

  public static String footer(
      LocalDate creationDate, int deliveryNumber, int recordCount,
      LocalDate previousDeliveryDate, Integer previousDeliveryNumber
  ) {
    val h =
        A( 12, "KONTROLLE BN")
            + A( 8, isoDate(creationDate))
            + N( 4, deliveryNumber) // documentation says: type A
            + N( 8, recordCount)    // documentation says: type A
            + A( 8, isoDate(previousDeliveryDate))
            + N( 4, previousDeliveryNumber) // documentation says: type A
            + A(SIZE-12-8-4-8-8-4, "")  // filler spaces
        ;
    checkAscii(h);
    checkLength(h, SIZE);
    return h;
  }

}
