package com.one.gdvftp.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NonNull;
import lombok.ToString;

@AllArgsConstructor
@ToString
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
  @NonNull final private LocalDateTime favDatAb;

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
  @NonNull final private String khVkTk;

  /**
   * Art der Schutzbrief-Deckung
   *
   * false  ->  0 = keine Deckung
   * true   ->  1 = fahrzeugbezogener Schutzbrief
   *            2 = Pannenhilfe
   */
  @NonNull final private Boolean schutzbrief;

  @NonNull final private String tkSb;
}
