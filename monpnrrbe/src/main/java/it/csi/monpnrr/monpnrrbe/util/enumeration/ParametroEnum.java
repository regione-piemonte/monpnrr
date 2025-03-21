/*
* SPDX-FileCopyrightText: (C) Copyright 2025 Regione Piemonte
*
* SPDX-License-Identifier: EUPL-1.2 
*/
package it.csi.monpnrr.monpnrrbe.util.enumeration;

public enum ParametroEnum {
	ULTIMA_MODIFICA_RUP("ULTIMA_MODIFICA_RUP"),
	ULTIMA_MODIFICA_RUP_DOPO_VERIFICA("ULTIMA_MODIFICA_RUP_DOPO_VERIFICA"),
	ULTIMA_VERIFICA_LIV2("ULTIMA_VERIFICA_LIV2"),
	ULTIMA_VERIFICA_LIV3("ULTIMA_VERIFICA_LIV3");
	
	private final String code;
	
	private ParametroEnum(String code) {
		this.code = code;
	}

	public String getCode() {
		return code;
	}
	
	public static ParametroEnum fromString(String value) {
        for (ParametroEnum profile : ParametroEnum.values()) {
            if (profile.code.equalsIgnoreCase(value)) {
                return profile;
            }
        }
        return null;
    }
}
