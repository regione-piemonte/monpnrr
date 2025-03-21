/*
* SPDX-FileCopyrightText: (C) Copyright 2025 Regione Piemonte
*
* SPDX-License-Identifier: EUPL-1.2 
*/
package it.csi.monpnrr.monpnrrbe.exception;

public class CambioStatoNonPossibileException extends Exception {

	private static final long serialVersionUID = -8763582816142055237L;

	public CambioStatoNonPossibileException() {
		super();
	}

	public CambioStatoNonPossibileException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public CambioStatoNonPossibileException(String message, Throwable cause) {
		super(message, cause);
	}

	public CambioStatoNonPossibileException(String message) {
		super(message);
	}

	public CambioStatoNonPossibileException(Throwable cause) {
		super(cause);
	}

}
