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

  /** Datum (Verbands-Vorgangsnummer)
   */
  @NonNull final private LocalDate datum;

  /** laufende Nummer (Verbands-Vorgangsnummer)
   */
  @NonNull final private Integer laufendeNummer;

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
  @NonNull final private LocalDate versichBeginn;

  /** Versicherungsnehmer Anredeschlüssel (0 or 1)
   * SF: sex */
  @NonNull final private Character anrede;

  /** Versicherungsnehmer Namenszeile 1 + 2
   * SF: Last Name */
  @NonNull final private String nachName;

  /** Versicherungsnehmer Namenszeile 3
   * SF: First Name */
  @NonNull final private String vorName;

  /** Versicherungsnehmer Straße
   */
  @NonNull final private String straße;

  /** Versicherungsnehmer LdKz
   */
  @NonNull final private String ldKz;

  /** Versicherungsnehmer PLZ
   */
  @NonNull final private String plz;

  /** Versicherungsnehmer Ort
   */
  @NonNull final private String ort;

  /** Vorversichererungsunternehmen
   *  SF: parameter previousInsurer */
  @NonNull final private String vorVu;

  /** Versicherungsscheinnummer beim Vorversicherer
   *   */
  @NonNull final private String vorVsNr;

  /** AKZ beim Vorversicherer
   *   */
  @NonNull final private String vorAkz;

  /** Bescheinigung gem. Par. 5 PflVersG wurde vorgelegt
   */
  @NonNull final private Character bescheinigung;

  /** n-te Erinnerung
   * */
  @NonNull final private Integer erinnerung;

}
