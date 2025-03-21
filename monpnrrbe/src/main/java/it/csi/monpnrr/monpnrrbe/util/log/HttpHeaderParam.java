/*
* SPDX-FileCopyrightText: (C) Copyright 2025 Regione Piemonte
*
* SPDX-License-Identifier: EUPL-1.2 
*/
package it.csi.monpnrr.monpnrrbe.util.log;

public record HttpHeaderParam (
	 String shibIdentitaCodiceFiscale,
	 String xRequestId,
	 String xForwardedFor,
	 String xCodiceServizio,
	 String token) {
	
	public HttpHeaderParam(String shibIdentitaCodiceFiscale,
			 String xRequestId,
			 String xForwardedFor,
			 String xCodiceServizio) {
        this(shibIdentitaCodiceFiscale, xRequestId, xForwardedFor, xCodiceServizio, null);
    }
	
}
	
