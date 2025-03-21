/*
* SPDX-FileCopyrightText: (C) Copyright 2025 Regione Piemonte
*
* SPDX-License-Identifier: EUPL-1.2 
*/
package it.csi.monpnrr.monpnrrbe.exception;

public class RESTException extends RuntimeException {
	private static final long serialVersionUID = -3536744145734865602L;
	
	public RESTException() {
		super();
	}

	public RESTException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public RESTException(String message, Throwable cause) {
		super(message, cause);
	}

	public RESTException(String message) {
		super(message);
	}

	public RESTException(Throwable cause) {
		super(cause);
	}
}
