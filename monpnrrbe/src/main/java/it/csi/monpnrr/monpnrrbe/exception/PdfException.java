/*
* SPDX-FileCopyrightText: (C) Copyright 2025 Regione Piemonte
*
* SPDX-License-Identifier: EUPL-1.2 
*/
package it.csi.monpnrr.monpnrrbe.exception;

public class PdfException extends Exception {

	private static final long serialVersionUID = -8763582816142055237L;

	public PdfException() {
		super();
	}

	public PdfException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public PdfException(String message, Throwable cause) {
		super(message, cause);
	}

	public PdfException(String message) {
		super(message);
	}

	public PdfException(Throwable cause) {
		super(cause);
	}

}
