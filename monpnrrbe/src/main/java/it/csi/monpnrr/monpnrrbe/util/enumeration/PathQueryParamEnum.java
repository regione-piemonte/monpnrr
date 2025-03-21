/*
* SPDX-FileCopyrightText: (C) Copyright 2025 Regione Piemonte
*
* SPDX-License-Identifier: EUPL-1.2 
*/
package it.csi.monpnrr.monpnrrbe.util.enumeration;

public enum PathQueryParamEnum {
	PROFILO("profilo"),
	AZIENDA_SANITARIA("aziendaSanitaria"),
	DATE("Date Modifica o Verifica");
	
	private final String code;

	PathQueryParamEnum(String code) {
		this.code = code;
	}

	public String getCode() {
		return code;
	}
}
