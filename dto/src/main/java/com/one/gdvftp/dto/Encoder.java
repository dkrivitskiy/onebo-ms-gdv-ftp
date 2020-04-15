package com.one.gdvftp.dto;

import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import lombok.NonNull;
import lombok.val;


abstract public class Encoder<DTO> {

  abstract public String encode(DTO d);
  abstract public String filename(int vuNr, int vuGstNr, LocalDate creationDate, int deliveryNumber);
  abstract public String header(int vuNr, int vuGstNr);
  abstract public String footer(
      LocalDate creationDate, int deliveryNumber, int recordCount,
      LocalDate previousDeliveryDate, Integer previousDeliveryNumber
  );

  // Be aware, that CharsetEncoder is not threadsafe!
  @NonNull private final CharsetEncoder encoder;

  @NonNull private final Charset charset;

  Encoder(Charset charset) {
    this.charset = charset;
    encoder = charset.newEncoder();
  }

  /**
   * Returns a numeric value as a String of the specified size
   * filled with zeros on the left side.
   */
  protected static String N(int size, Number n) {
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

  /**
   * Returns an alphanumeric value as a String of the specified size
   * filled with spaces on the right side.
   * Checks for character set.
   */
  protected String A(int size, String s) {
    if(s==null) return repeat(' ', size);
    checkCharset(s);
    val len = checkLength(s, size);
    if(len==size) return s;
    // len<size
    return s+repeat(' ', size-len);
  }

  private static String repeat(char c, int l) {
    val chars = new char[l];
    Arrays.fill(chars, c);
    return String.valueOf(chars);
  }

  protected synchronized void checkCharset(String s) { // synchronized because encoder is not threadsafe
    if(!encoder.canEncode(s)) throw new RuntimeException("String contains non "+charset.name()+" characters: \""+s+"\"");
  }

  protected static int checkLength(String s, int size) {
    val len = s.length();
    if(len>size) throw new RuntimeException("String is longer than "+size+" characters ("+len+"): \""+s+"\"");
    return len;
  }

  // is threadsafe
  private static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("ddMMyyyy");
  /**
   * returns a date as an int with format ddMMyyyy
   */
  protected static int date(LocalDate date) {
    if(date==null) return 0;
    val s = date.format(dateFormatter);
    val result = Integer.valueOf(s).intValue();
    return result;
  }

  // is threadsafe
  private static final DateTimeFormatter isoDateFormatter = DateTimeFormatter.ofPattern("yyyyMMdd");
  /**
   * returns a date as a String with format yyyyMMdd
   */
  protected static String isoDate(LocalDate date) {
    if(date==null) return "";
    val result = date.format(isoDateFormatter);
    return result;
  }

  // is threadsafe
  private static final DateTimeFormatter yearFormatter = DateTimeFormatter.ofPattern("yyyy");
  /**
   * returns the year
   */
  protected static int year(LocalDate date) {
    if(date==null) return 0;
    val s = date.format(yearFormatter);
    val result = Integer.valueOf(s).intValue();
    return result;
  }
}
