package com.one.gdvftp.dto;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import lombok.val;


public class ZentralrufRecordEncoder extends Encoder<ZentralrufRecordDTO> {

  static final int SIZE = 88;

  public ZentralrufRecordEncoder() {
    super(StandardCharsets.US_ASCII);
  }

  @Override
  public String encode(ZentralrufRecordDTO d) {
    val rec =
        N( 4, d.getVuNr())
            + A(20, d.getVertr())
            + A(12, d.getFaKz())
            + N( 3, d.getWagN())
            + N( 8, date(d.getFavDatAb()))
            + N( 8, date(d.getFavDatBis()))
            + N( 3, d.getVuGstNr())
            + N( 8, d.getAgent())
            + N( 2, deckungsart(d.getDeckungsArt()))
            + N( 1, serviceDeckung(d.isSchutzbrief()))
            + N( 4, d.getSb().get("TK"))
            + N( 4, d.getSb().get("VK"))
            + N( 4, d.getHsn())
            + A( 3, d.getTsn())
            + N( 4, year(d.getZulassung()))
        ;
    checkCharset(rec);
    checkLength(rec, SIZE);
    return rec;
  }

  @Override
  public String filename(int vuNr, int vuGstNr, LocalDate creationDate, int deliveryNumber) {
    val n =
        A( 3, "dat")
            + A( 1, ".")
            + N( 4, vuNr)
            + N( 3, vuGstNr)
            + A( 1, ".")
            + A( 3, "aza")     // Sachgebiet
            + A( 1, ".")
            + N( 4, year(creationDate))
            + N( 3, deliveryNumber)
        ;
    checkCharset(n);
    checkLength(n, 23);
    return n;
  }

  @Override
  public String header(int vuNr, int vuGstNr) {
    val h =
        A( 12, "KONTROLLE BV")
            + A( 4, "8333") // Ziel-VU
            + A( 3, "AZA")  // Ziel-Sachgebiet
            + A( 1, " ")    // K or H or space
            + N( 4, vuNr)      // Absender-VU; documentation says: type A
            + N( 3, vuGstNr)   // Absender-GS; documentation says: type A
            + A( 3, "")     // Information zum Sachgebiet
            + A(SIZE-12-4-3-1-4-3-3, "")  // filler spaces
        ;
    checkCharset(h);
    checkLength(h, SIZE);
    return h;
  }

  @Override
  public String footer(
      LocalDate creationDate, int deliveryNumber, int recordCount,
      LocalDate previousDeliveryDate, Integer previousDeliveryNumber
  ) {
    val f =
        A( 12, "KONTROLLE BN")
            + A( 8, isoDate(creationDate))
            + N( 4, deliveryNumber) // documentation says: type A
            + N( 8, recordCount)    // documentation says: type A
            + A( 8, isoDate(previousDeliveryDate))
            + N( 4, previousDeliveryNumber) // documentation says: type A
            + A( 3, "")     // Information zum Sachgebiet
            + A(SIZE-12-8-4-8-8-4-3, "")  // filler spaces
        ;
    checkCharset(f);
    checkLength(f, SIZE);
    return f;
  }

  private static int serviceDeckung(boolean schutzbrief) {
    return schutzbrief ? 1 : 0;
  }

  private static int deckungsart(String art) {
    return "VK".equals(art) ? 3
        : "TK".equals(art) ? 2
            : 1; // "KH"
  }
}
