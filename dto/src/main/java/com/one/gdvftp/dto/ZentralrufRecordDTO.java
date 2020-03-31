package com.one.gdvftp.dto;

import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.time.LocalDate;
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
  @NonNull final private Short vuNr;

  /** Nummer der VU-Geschäftsstelle
   *  ONE's insurance number 001 */
  @NonNull final private Short vuGstNr;

  /** Vertrag
   *  Contract Number */
  @NonNull final private String vertr;

  /** Amtliches Kennzeichen
   *  licence plate (without Umlauts) */
  @NonNull final private String faKz;

  /** Neben-Wagniskennziffer für Festlegung der Art des Kennzeichens
   *  not applicable */
  @NonNull final private Short wagN=0;

  /** Nummer des/der zuständigen VU-Agenten / VU-Außenstelle / Sachbearbeiters
   *  not applicable */
  @NonNull final private Short agent=0;

  /** Beginn der Haftpflicht- bzw. Kasko-VS
   *  Initial Valid From Date (DDMMYYYY) */
  @NonNull final private LocalDate favDatAb;

  /** Stornodatum bzw. leer
   *  Valid To (DDMMYYYY) */
  final private LocalDate favDatBis;

  /**
   * KH/VK/TK Art der Deckung
   *
   * vehicle-liability      -> 01 = KH
   * fully-comprehensive    -> 02 = VK (Vollkasko)
   * partial-comprehensive  -> 03 = TK (Teilkasko)
   */
  @NonNull final private List<?> khVkTk;

  /**
   * Art der Schutzbrief-Deckung
   *
   * false  ->  0 = keine Deckung
   * true   ->  1 = fahrzeugbezogener Schutzbrief
   *            2 = Pannenhilfe
   */
  @NonNull final private Boolean schutzbrief;

  @NonNull final private List tkSb;

  /**
   * Herstellernummer
   */
  @NonNull final private Short hsn;

  /**
   * Typschlüsselnummer
   */
  @NonNull final private String tsn;

  /**
   * Jahr der Erstzulassung
   */
  @NonNull final private LocalDate zulassung;


  private final CharsetEncoder encoder = Charset.forName("US-ASCII").newEncoder();

  private boolean isASCII(String s) {
    return encoder.canEncode(s);
  }

  public String toRecord() {
    val rec =
      N(4, getVuNr())+
      A(20, getVertr())+
      A(12, getFaKz())+
      N(3, getWagN());
    return rec;
  }

  /**
   * Returns an alphanumeric value as a String of the specified size
   * filled with spaces on the right side.
   * Checks for pure ASCII.
   */
  private String A(int size, String s) {
    if(s==null) return repeat(' ', size);
    if(!isASCII(s)) throw new RuntimeException("String contains non ASCII characters: \""+s+"\"");
    val len = s.length();
    if(len>size) throw new RuntimeException("String is longer than "+size+" characters: \""+s+"\"");
    if(len==size) return s;
    // len<size
      return s+repeat(' ', size-len);
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
}
