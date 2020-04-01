package com.one.gdvftp.dto;

import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
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
  final private int deckungsArt;

  /**
   * Art der Schutzbrief-Deckung
   *
   * false  ->  0 = keine Deckung
   * true   ->  1 = fahrzeugbezogener Schutzbrief
   *            2 = Pannenhilfe
   */
  final private boolean schutzbrief;

  @NonNull final private List tkSb;

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


  @ToString.Exclude
  private final CharsetEncoder encoder = Charset.forName("US-ASCII").newEncoder();

  private boolean isASCII(String s) {
    return encoder.canEncode(s);
  }

  public String toRecord() {
    val rec =
      N( 4, getVuNr())+
      A(20, getVertr())+
      A(12, getFaKz())+
      N( 3, getWagN())+
      N( 8, date(getFavDatAb()))+
      N( 8, date(getFavDatAb()))+
      N( 3, getVuGstNr())+
      N( 8, getAgent())+
      N( 2, getDeckungsArt())+
      N( 1, serviceDeckung(isSchutzbrief()))+
      N( 8, 0)+ // Servicedeckung TODO: implement
      N( 4, getHsn())+
      A( 3, getTsn())+
      N( 4, year(getZulassung()));
    checkAscii(rec);
    checkLength(rec, 88);
    return rec;
  }

  /**
   * Returns an alphanumeric value as a String of the specified size
   * filled with spaces on the right side.
   * Checks for pure ASCII.
   */
  private String A(int size, String s) {
    if(s==null) return repeat(' ', size);
    checkAscii(s);
    val len = checkLength(s, size);
    if(len==size) return s;
    // len<size
      return s+repeat(' ', size-len);
  }

  private int checkLength(String s, int size) {
    val len = s.length();
    if(len>size) throw new RuntimeException("String is longer than "+size+" characters ("+len+"): \""+s+"\"");
    return len;
  }

  private void checkAscii(String s) {
    if(!isASCII(s)) throw new RuntimeException("String contains non ASCII characters: \""+s+"\"");
  }

  /**
   * Returns a numeric value as a String of the specified size
   * filled with zeros on the left side.
   */
  private String N(int size, Number n) {
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

  private String repeat(char c, int l) {
    val chars = new char[l];
    Arrays.fill(chars, c);
    return String.valueOf(chars);
  }

  @ToString.Exclude
  private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("ddMMyyyy");
  /**
   * returns a date as an int with format ddMMyyyy
   */
  private int date(LocalDate date) {
    val s = date.format(dateFormatter);
    val result = Integer.valueOf(s);
    return result;
  }

  @ToString.Exclude
  private final DateTimeFormatter yearFormatter = DateTimeFormatter.ofPattern("yyyy");
  /**
   * returns the year
   */
  private int year(LocalDate date) {
    val s = date.format(yearFormatter);
    val result = Integer.valueOf(s);
    return result;
  }

  private int serviceDeckung(boolean schutzbrief) {
    return schutzbrief ? 1 : 0;
  }


}
