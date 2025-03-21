/*
* SPDX-FileCopyrightText: (C) Copyright 2025 Regione Piemonte
*
* SPDX-License-Identifier: EUPL-1.2 
*/
package it.csi.monpnrr.monpnrrbe.util.enumeration;

public enum HeaderEnum {
	SHIB_IDENTITA_CODICEFISCALE("Shib-Identita-CodiceFiscale"),
	X_FORWARDED_FOR("X-Forwarded-For"),
	X_REQUEST_ID("X-Request-Id"),
	X_CODICE_SERVIZIO("X-Codice-Servizio"),
	TOKEN("token"),
	AUTHORIZATION("Authorization"),
	ROWS_NUMBER("Rows-Number");
	
	private final String code;

	HeaderEnum(String code) {
		this.code = code;
	}

	public String getCode() {
		return code;
	}
}
