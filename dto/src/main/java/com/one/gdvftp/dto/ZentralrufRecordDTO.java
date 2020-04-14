package com.one.gdvftp.dto;

import java.time.LocalDate;
import java.util.Map;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.val;

@RequiredArgsConstructor
@ToString
@Getter
@Builder
public class ZentralrufRecordDTO extends DTO {

  static final int SIZE = 88;

  /** Vierstellige Nummer des VU
   *  ONE's insurance number 9496 */
  final private int vuNr;

  /** Vertrag
   *  Contract Number */
  @NonNull final private String vertr;

  /** Amtliches Kennzeichen
   *  licence plate (without Umlauts) */
  @NonNull final private String faKz;

  /** Neben-Wagniskennziffer für Festlegung der Art des Kennzeichens
   *  not applicable */
  final private int wagN=0;

  /** Beginn der Haftpflicht- bzw. Kasko-VS
   *  Initial Valid From Date (DDMMYYYY) */
  @NonNull final private LocalDate favDatAb;

  /** Stornodatum bzw. leer
   *  Valid To (DDMMYYYY) */
  final private LocalDate favDatBis;

  /** Nummer der VU-Geschäftsstelle
   *  ONE's insurance number 001 */
  final private int vuGstNr;

  /** Nummer des/der zuständigen VU-Agenten / VU-Außenstelle / Sachbearbeiters
   *  not applicable */
  final private int agent=0;

  /**
   * KH/VK/TK Art der Deckung
   *
   * vehicle-liability      -> 01 = KH
   * fully-comprehensive    -> 02 = VK (Vollkasko)
   * partial-comprehensive  -> 03 = TK (Teilkasko)
   */
  @NonNull final private String deckungsArt;

  /**
   * Art der Schutzbrief-Deckung
   *
   * false  ->  0 = keine Deckung
   * true   ->  1 = fahrzeugbezogener Schutzbrief
   *            2 = Pannenhilfe
   */
  final private boolean schutzbrief;

  @NonNull final private Map<String, Integer> sb;

  /**
   * Herstellernummer
   */
  final private int hsn;

  /**
   * Typschlüsselnummer
   */
  @NonNull final private String tsn;

  /**
   * Jahr der Erstzulassung
   */
  final private LocalDate zulassung;


  public String toRecord() {
    val rec =
        N( 4, getVuNr())
      + A(20, getVertr())
      + A(12, getFaKz())
      + N( 3, getWagN())
      + N( 8, date(getFavDatAb()))
      + N( 8, date(getFavDatBis()))
      + N( 3, getVuGstNr())
      + N( 8, getAgent())
      + N( 2, deckungsart(getDeckungsArt()))
      + N( 1, serviceDeckung(isSchutzbrief()))
      + N( 4, getSb().get("TK"))
      + N( 4, getSb().get("VK"))
      + N( 4, getHsn())
      + A( 3, getTsn())
      + N( 4, year(getZulassung()))
      ;
    checkAscii(rec);
    checkLength(rec, SIZE);
    return rec;
  }

  public static String filename(int vuNr, int vuGstNr, LocalDate creationDate, int deliveryNumber) {
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
    checkAscii(n);
    checkLength(n, 23);
    return n;
  }

  public static String header(int vuNr, int vuGstNr) {
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
    checkAscii(h);
    checkLength(h, SIZE);
    return h;
  }

  public static String footer(
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
    checkAscii(f);
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
