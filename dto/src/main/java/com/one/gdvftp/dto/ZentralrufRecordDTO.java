package com.one.gdvftp.dto;

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

  /** Vertrag
   *  Contract Number */
  @NonNull final private String vertr;

  /** Amtliches Kennzeichen
   *  licence plate (without Umlauts) */
  @NonNull final private String faKz;

  /** Neben-Wagniskennziffer für Festlegung der Art des Kennzeichens
   *  not applicable */
  @NonNull final private Short wagN=0;

//  /** Beginn der Haftpflicht- bzw. Kasko-VS
//   *  Initial Valid From Date (DDMMYYYY) */
//  @NonNull final private Integer favDatAb;

  /** Stornodatum bzw. leer
   *  Valid To (DDMMYYYY) */
  @NonNull final private Integer favDatBis=0;

}
