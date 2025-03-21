/*
* SPDX-FileCopyrightText: (C) Copyright 2025 Regione Piemonte
*
* SPDX-License-Identifier: EUPL-1.2 
*/
package it.csi.monpnrr.monpnrrbe.exception;

public class ProfiloNonAmmessoException extends Exception {

	private static final long serialVersionUID = -8763582816142055237L;

	public ProfiloNonAmmessoException() {
		super();
	}

	public ProfiloNonAmmessoException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public ProfiloNonAmmessoException(String message, Throwable cause) {
		super(message, cause);
	}

	public ProfiloNonAmmessoException(String message) {
		super(message);
	}

	public ProfiloNonAmmessoException(Throwable cause) {
		super(cause);
	}

}
