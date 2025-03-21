/*
* SPDX-FileCopyrightText: (C) Copyright 2025 Regione Piemonte
*
* SPDX-License-Identifier: EUPL-1.2 
*/
package it.csi.monpnrr.monpnrrbe.exception;

public class PdfNonDisponibileException extends Exception {

	private static final long serialVersionUID = -8763582816142055237L;

	public PdfNonDisponibileException() {
		super();
	}

	public PdfNonDisponibileException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public PdfNonDisponibileException(String message, Throwable cause) {
		super(message, cause);
	}

	public PdfNonDisponibileException(String message) {
		super(message);
	}

	public PdfNonDisponibileException(Throwable cause) {
		super(cause);
	}

}
