/*
* SPDX-FileCopyrightText: (C) Copyright 2025 Regione Piemonte
*
* SPDX-License-Identifier: EUPL-1.2 
*/
package it.csi.monpnrr.monpnrrbe.util.enumeration;

public enum ProfiloEnum {
	RUP("RUP-CUP-checklist"),
	RUA("RUA-AS-checklist"),
	SUPER_USER("RP-checklist"),
	AUTOCONTROLLO_2("RP-verifica-liv2"),
	AUTOCONTROLLO_3("RP-verifica-liv3");
	
	private final String code;
	
	private ProfiloEnum(String code) {
		this.code = code;
	}

	public String getCode() {
		return code;
	}
	
	public static ProfiloEnum fromString(String value) {
        for (ProfiloEnum profile : ProfiloEnum.values()) {
            if (profile.code.equalsIgnoreCase(value)) {
                return profile;
            }
        }
        return null;
    }
	
	public static boolean isRp(ProfiloEnum profilo) {
		return switch (profilo) {
		    case RUP, RUA -> false;
		    case SUPER_USER, AUTOCONTROLLO_2, AUTOCONTROLLO_3 -> true;
		    default -> false;
		};
	}
}
