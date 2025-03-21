/*
* SPDX-FileCopyrightText: (C) Copyright 2025 Regione Piemonte
*
* SPDX-License-Identifier: EUPL-1.2 
*/
package it.csi.monpnrr.monpnrrbe.util.enumeration;

public enum StatoRilevazioneEnum {
	DA_COMPILARE("DA_COMPILARE"),
	BOZZA("BOZZA"),
	COMPILATA("COMPILATA"),
	COMPLETATA("COMPLETATA");
	
	private final String code;
	
	private StatoRilevazioneEnum(String code) {
		this.code = code;
	}

	public String getCode() {
		return code;
	}
}
