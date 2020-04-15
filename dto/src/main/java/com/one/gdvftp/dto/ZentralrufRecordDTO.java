package com.one.gdvftp.dto;

import java.time.LocalDate;
import java.util.Map;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

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
  @NonNull final private String deckungsArt;

  /**
   * Art der Schutzbrief-Deckung
   *
   * false  ->  0 = keine Deckung
   * true   ->  1 = fahrzeugbezogener Schutzbrief
   *            2 = Pannenhilfe
   */
  final private boolean schutzbrief;

  @NonNull final private Map<String, Integer> sb;

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
  final private LocalDate zulassung;

}
