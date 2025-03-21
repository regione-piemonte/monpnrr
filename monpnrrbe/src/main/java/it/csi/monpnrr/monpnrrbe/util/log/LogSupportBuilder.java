/*
* SPDX-FileCopyrightText: (C) Copyright 2025 Regione Piemonte
*
* SPDX-License-Identifier: EUPL-1.2 
*/
package it.csi.monpnrr.monpnrrbe.util.log;

import it.csi.monpnrr.monpnrrbe.util.enumeration.ApiMethodEnum;

public class LogSupportBuilder {
	
	private LogSupport logSupport;
	
	private LogSupportBuilder() {
		this.logSupport = new LogSupport();
	}
	
	public static LogSupportBuilder init(String methodName, HttpHeaderParam param, String apiUrl, ApiMethodEnum apiEnum) {
		return new LogSupportBuilder().methodName(methodName).httpHeaderParam(param).apiUrl(apiUrl).verboApiEnum(apiEnum);
	}
	
	public LogSupportBuilder methodName(String methodName) {
		this.logSupport.setMethodName(methodName);
		return this;
	}
	
	public LogSupportBuilder httpHeaderParam(HttpHeaderParam param) {
		this.logSupport.setHttpHeaderParam(param);
		return this;
	}
	
	public LogSupportBuilder apiUrl(String apiUrl)  {
		this.logSupport.setApiUrl(apiUrl);
		return this;
	}
	
	public LogSupportBuilder verboApiEnum(ApiMethodEnum apiEnum) {
		this.logSupport.setApiEnum(apiEnum);
		return this;
	}
	
	public LogSupportBuilder payloadRequest(Object payload) {
		this.logSupport.setRequest(payload);
		return this;
	}
	
	public LogSupportBuilder payloadResponse(Object payload) {
		this.logSupport.setResponse(payload);
		return this;
	}
	
	public LogSupportBuilder queryParam(String queryParam) {
		this.logSupport.setQueryParam(queryParam);
		return this;
	}
	
	public LogSupportBuilder isCsiAuditLog(boolean isCSIAuditLog) {
		this.logSupport.setCSIAuditLog(isCSIAuditLog);
		return this;
	}
	public LogSupportBuilder isLoggableParam(boolean isLoggableParam) {
		this.logSupport.setLoggableParam(isLoggableParam);
		return this;
	}
	
	public LogSupportBuilder isLoggablePayload(boolean isLoggablePayload) {
		this.logSupport.setLoggablePayload(isLoggablePayload);
		return this;
	}
	
	public LogSupport build() {
		return this.logSupport;
	}
}