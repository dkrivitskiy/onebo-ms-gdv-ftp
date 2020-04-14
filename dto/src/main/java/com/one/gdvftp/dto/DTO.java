package com.one.gdvftp.dto;

import java.nio.charset.Charset;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import lombok.val;

public class DTO {

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
   * Checks for pure ASCII.
   */
  protected static String A(int size, String s) {
    if(s==null) return repeat(' ', size);
    checkAscii(s);
    val len = checkLength(s, size);
    if(len==size) return s;
    // len<size
    return s+repeat(' ', size-len);
  }

  protected static String repeat(char c, int l) {
    val chars = new char[l];
    Arrays.fill(chars, c);
    return String.valueOf(chars);
  }

  protected static synchronized boolean isASCII(String s) { // synchronized because encoder is not threadsafe
    // CharsetEncoder is not threadsafe!
    val encoder = Charset.forName("US-ASCII").newEncoder();
    return encoder.canEncode(s);
  }

  protected static void checkAscii(String s) {
    if(!isASCII(s)) throw new RuntimeException("String contains non ASCII characters: \""+s+"\"");
  }

  protected static int checkLength(String s, int size) {
    val len = s.length();
    if(len>size) throw new RuntimeException("String is longer than "+size+" characters ("+len+"): \""+s+"\"");
    return len;
  }

  // is threadsafe
  protected static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("ddMMyyyy");
  /**
   * returns a date as an int with format ddMMyyyy
   */
  protected static int date(LocalDate date) {
    if(date==null) return 0;
    val s = date.format(dateFormatter);
    val result = Integer.valueOf(s);
    return result;
  }

  // is threadsafe
  protected static final DateTimeFormatter isoDateFormatter = DateTimeFormatter.ofPattern("yyyyMMdd");
  /**
   * returns a date as a String with format yyyyMMdd
   */
  protected static String isoDate(LocalDate date) {
    if(date==null) return "";
    val result = date.format(isoDateFormatter);
    return result;
  }

  // is threadsafe
  protected static final DateTimeFormatter yearFormatter = DateTimeFormatter.ofPattern("yyyy");
  /**
   * returns the year
   */
  protected static int year(LocalDate date) {
    if(date==null) return 0;
    val s = date.format(yearFormatter);
    val result = Integer.valueOf(s);
    return result;
  }


}
