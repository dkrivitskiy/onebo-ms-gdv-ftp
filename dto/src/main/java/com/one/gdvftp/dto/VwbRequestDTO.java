package com.one.gdvftp.dto;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.stream.IntStream;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import lombok.val;


//@RequiredArgsConstructor
@ToString
@Getter
@Builder
public class VwbRequestDTO extends DTO {

  /** Vierstellige Nummer des VU
   *  ONE's insurance number 9496 */
  final private int vuNr;

  /** Nummer der VU-Geschäftsstelle
   *  ONE's insurance number 001 */
  final private int vuGstNr;

  /** Versicherungsscheinnummer
   *  SF: contractNumber */
  final private String vsNr;

  /** Fahrzeugidentifizierungsnummer
   *  SF: VIN */
  final private String fin;

  /** Versicherungsbeginn
   *  SF: Initial valid Date From */
  final private LocalDate versichBeginn;


  static final int SIZE = 57; // 88

  public String toRecord() {
    val rec = A( 2,"10") // Satzart 10
      //+A(18, "") // Verbandsvorgangsnummer TODO
      +modulo11(   // N8
      N( 4, getVuNr())
        +N( 3, getVuGstNr()))
      +A(20, getVsNr())
      +N( 2, 1) // Anfragegrund (01 = Versichererwechsel)
      +A(17, getFin())
      +N( 8, date(getVersichBeginn()))
      +A(SIZE-2-8-20-2-17-8, "")  // filler spaces
      ;
    checkAscii(rec);
    checkLength(rec, SIZE);
    return rec;
  }

  // Adds a check digit to the end of a string.
  // The string must contain 7 digits.
  private String modulo11(String s) {
    val factors = Arrays.asList(0,6,3,1,7,2,4);

    if(s==null || s.length()!=factors.size())
      throw new RuntimeException("String \""+s+"\n must have size "+factors.size());

    val sum = IntStream.range(0,s.length()).map(i -> {
      val digit = s.charAt(i)-'0';
      val factor = factors.get(i);
      return digit*factor;
    }).sum();

    val check = (sum%11)%10;
    return s+check;
  }


  public static String filename(int vuNr, int vuGstNr, LocalDate creationDate, int deliveryNumber) {
    val n =
       A( 3, "dat")
      +A( 1, ".")
      +N( 4, vuNr)
      +N( 3, vuGstNr)
      +A( 1, ".")
      +A( 3, "kvb")     // Sachgebiet
      +A( 1, ".")
      +N( 4, year(creationDate))
      +N( 3, deliveryNumber)
      ;
    checkAscii(n);
    checkLength(n, 23);
    return n;
  }

  public static String header(int vuNr, int vuGstNr) {
    val h =
       A( 12, "KONTROLLE BV")
      +A( 4, "8333")    // Ziel-VU
      +A( 3, "KVB")     // Ziel-Sachgebiet
      +A( 1, "T")       // T or space
      +N( 4, vuNr)      // Absender-VU
      +N( 3, vuGstNr)   // Absender-GS
      +A(SIZE-12-4-3-1-4-3, "")  // filler spaces
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
      +A( 8, isoDate(creationDate))
      +N( 4, deliveryNumber) // documentation says: type A
      +N( 8, recordCount)    // documentation says: type A
      +A( 8, isoDate(previousDeliveryDate))
      +N( 4, previousDeliveryNumber) // documentation says: type A
      +A(SIZE-12-8-4-8-8-4, "")  // filler spaces
      ;
    checkAscii(f);
    checkLength(f, SIZE);
    return f;
  }
}
