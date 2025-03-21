/*
* SPDX-FileCopyrightText: (C) Copyright 2025 Regione Piemonte
*
* SPDX-License-Identifier: EUPL-1.2 
*/
package it.csi.monpnrr.monpnrrbe.util.enumeration;

public enum OrderQueryEnum {
	CUP("pdp.id_cup"),
	DESCRIZIONE("pdm.misura_desc"),
	ASL("pda.asl_azienda_desc"),
	RUA("rua"),
	RUP("rup"),
	CHECKLIST("pdc.checklist_desc"),
	STATO("pdsr.stato_rilevazione_desc"),
	ULTIMAMODIFICA("ultima_modifica_data"),
	AUTOCONTROLLO("ultima_verifica_data"),
	MODIFICARECENTE("modifica_recente");
	
	private final String code;
	
	private OrderQueryEnum(String code) {
		this.code = code;
	}

	public String getCode() {
		return code;
	}
	
	public static OrderQueryEnum fromString(String value) {
        for (OrderQueryEnum profile : OrderQueryEnum.values()) {
            if (profile.code.equalsIgnoreCase(value)) {
                return profile;
            }
        }
        return null;
    }
	
	public static OrderQueryEnum fromName(String key) {
        for (OrderQueryEnum profile : OrderQueryEnum.class.getEnumConstants()) {
            if (profile.name().equalsIgnoreCase(key)) {
                return profile;
            }
        }
        return null;
    }
}
