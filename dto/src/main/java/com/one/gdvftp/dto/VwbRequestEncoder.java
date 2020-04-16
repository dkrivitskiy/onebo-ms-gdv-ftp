package com.one.gdvftp.dto;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.stream.IntStream;
import lombok.val;

public class VwbRequestEncoder extends Encoder<VwbRequestDTO> {

  static final int SIZE = 255;

  VwbRequestEncoder() {
    super(StandardCharsets.ISO_8859_1);
  }

  @Override
  public String encode(VwbRequestDTO d) {
    val rec = A( 2,"10") // Satzart 10
        +N( 4, d.getVuNr())
        +A( 8, isoDate(d.getDatum()))
        +N( 6, d.getLaufendeNummer())
        +modulo11(   // N8
           N( 4, d.getVuNr())
          +N( 3, d.getVuGstNr()))
        +A(20, d.getVsNr())
        +N( 2, 1) // Anfragegrund (01 = Versichererwechsel)
        +A(17, d.getFin())
        +N( 8, date(d.getVersichBeginn()))
        +Z(d.getAnrede())
        +A( 30+25, d.getNachName())
        +A(20, d.getVorName())
        +A(30, d.getStraße())
        +A(3, d.getLdKz())
        +A(6, d.getPlz())
        +A(25, d.getOrt())
        +modulo11(   // N8
           N( 4, d.getVorVuNr())
          +N( 3, d.getVorVuGstNr()))
        +A(20, d.getVorVsNr())
        +A(10, d.getVorAkz())
        +Z(d.getBescheinigung())
        +N(1, d.getErinnerung())
        ;
    checkCharset(rec);
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

  @Override
  public String filename(int vuNr, int vuGstNr, LocalDate creationDate, int deliveryNumber) {
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
    checkCharset(n);
    checkLength(n, 23);
    return n;
  }

  @Override
  public String header(int vuNr, int vuGstNr) {
    val h =
        A( 12, "KONTROLLE BV")
            +A( 4, "8333")    // Ziel-VU
            +A( 3, "KVB")     // Ziel-Sachgebiet
            +A( 1, "T")       // T or space
            +N( 4, vuNr)      // Absender-VU
            +N( 3, vuGstNr)   // Absender-GS
            +A(SIZE-12-4-3-1-4-3, "")  // filler spaces
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
            +A( 8, isoDate(creationDate))
            +N( 4, deliveryNumber) // documentation says: type A
            +N( 8, recordCount)    // documentation says: type A
            +A( 8, isoDate(previousDeliveryDate))
            +N( 4, previousDeliveryNumber) // documentation says: type A
            +A(SIZE-12-8-4-8-8-4, "")  // filler spaces
        ;
    checkCharset(f);
    checkLength(f, SIZE);
    return f;
  }
}
