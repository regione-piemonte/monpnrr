/*
* SPDX-FileCopyrightText: (C) Copyright 2025 Regione Piemonte
*
* SPDX-License-Identifier: EUPL-1.2 
*/
package it.csi.monpnrr.monpnrrbe.util.enumeration;

public enum MacrosezioneEnum {
	CUP("CUP"),
	CIG("CIG");
	
	private final String code;
	
	private MacrosezioneEnum(String code) {
		this.code = code;
	}

	public String getCode() {
		return code;
	}
}
