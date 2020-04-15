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
public class VwbRequestDTO {

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

  /** Versicherungsnehmer Anredeschlüssel (0 or 1)
   * SF: sex */
  final private char anrede;

  /** Versicherungsnehmer Namenszeile 1 + 2
   * SF: Last Name */
  final private String nachName;

  /** Versicherungsnehmer Namenszeile 3
   * SF: First Name */
  final private String vorName;

  /** Versicherungsnehmer Straße
   */
  final private String straße;

  /** Versicherungsnehmer LdKz
   */
  final private String ldKz;

  /** Versicherungsnehmer PLZ
   */
  final private String plz;

  /** Versicherungsnehmer Ort
   */
  final private String ort;

}
