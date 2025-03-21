/*
* SPDX-FileCopyrightText: (C) Copyright 2025 Regione Piemonte
*
* SPDX-License-Identifier: EUPL-1.2 
*/
package it.csi.monpnrr.monpnrrbe.exception;

public class ConfiguratoreGeneralException extends RuntimeException {
	private static final long serialVersionUID = -3536744145734865602L;
	
	private Integer status;
	private String code;
	
	public ConfiguratoreGeneralException() {
		super();
	}

	public ConfiguratoreGeneralException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public ConfiguratoreGeneralException(String message, Throwable cause) {
		super(message, cause);
	}

	public ConfiguratoreGeneralException(String message) {
		super(message);
	}
	
	public ConfiguratoreGeneralException(String message, Integer status, String code) {
		super(message);
		this.status = status;
		this.code = code;
	}

	public ConfiguratoreGeneralException(Throwable cause) {
		super(cause);
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}
}
