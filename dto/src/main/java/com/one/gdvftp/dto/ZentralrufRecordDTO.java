package com.one.gdvftp.dto;

import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
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
public class ZentralrufRecordDTO {

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
  @NonNull final private LocalDate zulassung;


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
      + A( 2, getDeckungsArt())
      + N( 1, serviceDeckung(isSchutzbrief()))
      + N( 8, 0) // Servicedeckung TODO: implement
      + N( 4, getHsn())
      + A( 3, getTsn())
      + N( 4, year(getZulassung()))
      ;
    checkAscii(rec);
    checkLength(rec, SIZE);
    return rec;
  }

  public static String filename(int vuNr, int vuGstNr, LocalDate creationDate, int deliveryNumber) {
    val h =
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
    checkAscii(h);
    checkLength(h, 23);
    return h;
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
    val h =
          A( 12, "KONTROLLE BN")
        + A( 4, "8333") // Ziel-VU
        + A( 8, isoDate(creationDate))
        + N( 4, deliveryNumber) // documentation says: type A
        + N( 8, recordCount)    // documentation says: type A
        + A( 8, isoDate(previousDeliveryDate))
        + N( 4, previousDeliveryNumber) // documentation says: type A
        + A( 3, "")     // Information zum Sachgebiet
        + A(SIZE-12-4-8-4-8-8-4-3, "")  // filler spaces
        ;
    checkAscii(h);
    checkLength(h, SIZE);
    return h;
  }

  // CharsetEncoder is not threadsafe!
  private static final CharsetEncoder encoder = Charset.forName("US-ASCII").newEncoder();

  private static synchronized boolean isASCII(String s) { // synchronized because encoder is not threadsafe
    return encoder.canEncode(s);
  }

  /**
   * Returns an alphanumeric value as a String of the specified size
   * filled with spaces on the right side.
   * Checks for pure ASCII.
   */
  private static String A(int size, String s) {
    if(s==null) return repeat(' ', size);
    checkAscii(s);
    val len = checkLength(s, size);
    if(len==size) return s;
    // len<size
      return s+repeat(' ', size-len);
  }

  private static int checkLength(String s, int size) {
    val len = s.length();
    if(len>size) throw new RuntimeException("String is longer than "+size+" characters ("+len+"): \""+s+"\"");
    return len;
  }

  private static void checkAscii(String s) {
    if(!isASCII(s)) throw new RuntimeException("String contains non ASCII characters: \""+s+"\"");
  }

  /**
   * Returns a numeric value as a String of the specified size
   * filled with zeros on the left side.
   */
  private static String N(int size, Number n) {
    if(n==null) return repeat('0', size);
    val v = n.longValue();
    if(v<0) throw new RuntimeException("Number is negative: \""+n+"\"");
    val s = ""+v;
    val len = s.length();
    if(len>size) throw new RuntimeException("String is longer than "+size+" characters: \""+s+"\"");
    if(len==size) return s;
    // len<l
    return repeat('0', size-len)+s;
  }

  private static String repeat(char c, int l) {
    val chars = new char[l];
    Arrays.fill(chars, c);
    return String.valueOf(chars);
  }

  // is threadsafe
  private static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("ddMMyyyy");
  /**
   * returns a date as an int with format ddMMyyyy
   */
  private static int date(LocalDate date) {
    if(date==null) return 0;
    val s = date.format(dateFormatter);
    val result = Integer.valueOf(s);
    return result;
  }

  // is threadsafe
  private static final DateTimeFormatter isoDateFormatter = DateTimeFormatter.ofPattern("yyyyMMdd");
  /**
   * returns a date as a String with format yyyyMMdd
   */
  private static String isoDate(LocalDate date) {
    if(date==null) return "";
    val result = date.format(isoDateFormatter);
    return result;
  }

  // is threadsafe
  private static final DateTimeFormatter yearFormatter = DateTimeFormatter.ofPattern("yyyy");
  /**
   * returns the year
   */
  private static int year(LocalDate date) {
    val s = date.format(yearFormatter);
    val result = Integer.valueOf(s);
    return result;
  }

  private static int serviceDeckung(boolean schutzbrief) {
    return schutzbrief ? 1 : 0;
  }

}
