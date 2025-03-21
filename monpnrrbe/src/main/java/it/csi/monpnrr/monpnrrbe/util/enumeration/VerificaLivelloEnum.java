/*
* SPDX-FileCopyrightText: (C) Copyright 2025 Regione Piemonte
*
* SPDX-License-Identifier: EUPL-1.2 
*/
package it.csi.monpnrr.monpnrrbe.util.enumeration;

public enum VerificaLivelloEnum {
	VERIFICA_LIVELLO_2("VERIFICA_LIVELLO_2"),
	VERIFICA_LIVELLO_3("VERIFICA_LIVELLO_3");
	
	private final String code;
	
	private VerificaLivelloEnum(String code) {
		this.code = code;
	}

	public String getCode() {
		return code;
	}
}
