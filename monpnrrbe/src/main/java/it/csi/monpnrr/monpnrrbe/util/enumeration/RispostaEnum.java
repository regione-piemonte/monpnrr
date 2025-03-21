/*
* SPDX-FileCopyrightText: (C) Copyright 2025 Regione Piemonte
*
* SPDX-License-Identifier: EUPL-1.2 
*/
package it.csi.monpnrr.monpnrrbe.util.enumeration;

public enum RispostaEnum {
	SI("SI"),
	NO("NO"),
	NON_APPLICABILE("NON_APPLICABILE"),
	NON_APPLICABILE_PER_AVANZAMENTO("NON_APPLICABILE_PER_AVANZAMENTO");
	
	private final String code;
	
	private RispostaEnum(String code) {
		this.code = code;
	}

	public String getCode() {
		return code;
	}
}
