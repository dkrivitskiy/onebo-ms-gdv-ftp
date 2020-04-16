package com.one.gdvftp.dto;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.stream.IntStream;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;
import lombok.val;


//@RequiredArgsConstructor
@ToString
@Getter
@Builder
public class VwbRequestDTO {

  /** Vierstellige Nummer des VU
   *  ONE's insurance number 9496 */
  @NonNull final private Integer vuNr;

  /** Nummer der VU-Geschäftsstelle
   *  ONE's insurance number 001 */
  @NonNull final private Integer vuGstNr;

  /** Versicherungsscheinnummer
   *  SF: contractNumber */
  @NonNull final private String vsNr;

  /** Fahrzeugidentifizierungsnummer
   *  SF: VIN */
  @NonNull final private String fin;

  /** Versicherungsbeginn
   *  SF: Initial valid Date From */
  final private LocalDate versichBeginn;

  /** Versicherungsnehmer Anredeschlüssel (0 or 1)
   * SF: sex */
  final private Character anrede;

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

  /** Vierstellige Nummer des Vorversicherers
   *   */
  final private int vorVuNr;

  /** Nummer der Geschäftsstelle des Vorversicherers
   *   */
  final private int vorVuGstNr;

  /** Versicherungsscheinnummer beim Vorversicherer
   *   */
  final private String vorVsNr;

  /** AKZ beim Vorversicherer
   *   */
  final private String vorAkz;

  /** Bescheinigung gem. Par. 5 PflVersG wurde vorgelegt
   */
  final private Character bescheinigung;

  /** n-te Erinnerung
   * */
  final private Integer erinnerung;

}
