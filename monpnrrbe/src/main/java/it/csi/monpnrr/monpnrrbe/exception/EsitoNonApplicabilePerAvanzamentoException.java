/*
* SPDX-FileCopyrightText: (C) Copyright 2025 Regione Piemonte
*
* SPDX-License-Identifier: EUPL-1.2 
*/
package it.csi.monpnrr.monpnrrbe.exception;

public class EsitoNonApplicabilePerAvanzamentoException extends Exception {

	private static final long serialVersionUID = -8763582816142055237L;

	public EsitoNonApplicabilePerAvanzamentoException() {
		super();
	}

	public EsitoNonApplicabilePerAvanzamentoException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public EsitoNonApplicabilePerAvanzamentoException(String message, Throwable cause) {
		super(message, cause);
	}

	public EsitoNonApplicabilePerAvanzamentoException(String message) {
		super(message);
	}

	public EsitoNonApplicabilePerAvanzamentoException(Throwable cause) {
		super(cause);
	}

}
