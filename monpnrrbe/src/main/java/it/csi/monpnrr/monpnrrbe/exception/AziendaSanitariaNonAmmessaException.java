/*
* SPDX-FileCopyrightText: (C) Copyright 2025 Regione Piemonte
*
* SPDX-License-Identifier: EUPL-1.2 
*/
package it.csi.monpnrr.monpnrrbe.exception;

public class AziendaSanitariaNonAmmessaException extends Exception {

	private static final long serialVersionUID = -8763582816142055237L;

	public AziendaSanitariaNonAmmessaException() {
		super();
	}

	public AziendaSanitariaNonAmmessaException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public AziendaSanitariaNonAmmessaException(String message, Throwable cause) {
		super(message, cause);
	}

	public AziendaSanitariaNonAmmessaException(String message) {
		super(message);
	}

	public AziendaSanitariaNonAmmessaException(Throwable cause) {
		super(cause);
	}

}
