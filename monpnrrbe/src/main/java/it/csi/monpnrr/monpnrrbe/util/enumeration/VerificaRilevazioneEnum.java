/*
* SPDX-FileCopyrightText: (C) Copyright 2025 Regione Piemonte
*
* SPDX-License-Identifier: EUPL-1.2 
*/
package it.csi.monpnrr.monpnrrbe.util.enumeration;

public enum VerificaRilevazioneEnum {
	OK("OK"),
	KO("KO");
	
	private final String code;
	
	private VerificaRilevazioneEnum(String code) {
		this.code = code;
	}

	public String getCode() {
		return code;
	}
}
