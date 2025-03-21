/*
* SPDX-FileCopyrightText: (C) Copyright 2025 Regione Piemonte
*
* SPDX-License-Identifier: EUPL-1.2 
*/
package it.csi.monpnrr.monpnrrbe.util.enumeration;

public enum ApiMethodEnum {
	GET("GET"),
	POST("POST"),
	PUT("PUT"),
	DELETE("DELETE"),
	PATCH("PATCH");
	
	private final String code;
	
	private ApiMethodEnum(String code) {
		this.code = code;
	}

	public String getCode() {
		return code;
	}
}
