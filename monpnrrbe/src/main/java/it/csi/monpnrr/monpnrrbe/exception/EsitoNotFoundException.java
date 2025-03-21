/*
* SPDX-FileCopyrightText: (C) Copyright 2025 Regione Piemonte
*
* SPDX-License-Identifier: EUPL-1.2 
*/
package it.csi.monpnrr.monpnrrbe.exception;

public class EsitoNotFoundException extends Exception {

	private static final long serialVersionUID = -8763582816142055237L;

	public EsitoNotFoundException() {
		super();
	}

	public EsitoNotFoundException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public EsitoNotFoundException(String message, Throwable cause) {
		super(message, cause);
	}

	public EsitoNotFoundException(String message) {
		super(message);
	}

	public EsitoNotFoundException(Throwable cause) {
		super(cause);
	}

}
