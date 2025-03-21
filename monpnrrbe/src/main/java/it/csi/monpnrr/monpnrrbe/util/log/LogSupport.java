/*
* SPDX-FileCopyrightText: (C) Copyright 2025 Regione Piemonte
*
* SPDX-License-Identifier: EUPL-1.2 
*/
package it.csi.monpnrr.monpnrrbe.util.log;

import it.csi.monpnrr.monpnrrbe.util.enumeration.ApiMethodEnum;

public class LogSupport {
	private String methodName=null;
	private HttpHeaderParam httpHeaderParam = null;
	private String apiUrl = null;
	private ApiMethodEnum apiEnum = null;
	private Object request = null;
	private Object response= null;
	private String queryParam = null;
	private boolean isCSIAuditLog = false;
	private boolean isLoggablePayload = false;
	private boolean isLoggableParam = false;

	public HttpHeaderParam getHttpHeaderParam() {
		return httpHeaderParam;
	}

	public void setHttpHeaderParam(HttpHeaderParam httpHeaderParam) {
		this.httpHeaderParam = httpHeaderParam;
	}

	public String getApiUrl() {
		return apiUrl;
	}

	public void setApiUrl(String apiUrl) {
		this.apiUrl = apiUrl;
	}

	public ApiMethodEnum getApiEnum() {
		return apiEnum;
	}

	public void setApiEnum(ApiMethodEnum apiEnum) {
		this.apiEnum = apiEnum;
	}

	public String getQueryParam() {
		return queryParam;
	}

	public void setQueryParam(String queryParam) {
		this.queryParam = queryParam;
	}

	public boolean isCSIAuditLog() {
		return isCSIAuditLog;
	}

	public void setCSIAuditLog(boolean isCSIAuditLog) {
		this.isCSIAuditLog = isCSIAuditLog;
	}

	public boolean isLoggableParam() {
		return isLoggableParam;
	}

	public void setLoggableParam(boolean isLoggableParam) {
		this.isLoggableParam = isLoggableParam;
	}


	public boolean isLoggablePayload() {
		return isLoggablePayload;
	}

	public void setLoggablePayload(boolean isLoggablePayload) {
		this.isLoggablePayload = isLoggablePayload;
	}

	public String getMethodName() {
		return methodName;
	}

	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}

	public Object getRequest() {
		return request;
	}

	public void setRequest(Object request) {
		this.request = request;
	}

	public Object getResponse() {
		return response;
	}

	public void setResponse(Object response) {
		this.response = response;
	}

	@Override
	public String toString() {
		return "LogSupport [methodName=" + methodName + ", httpHeaderParam=" + httpHeaderParam + ", apiUrl=" + apiUrl
				+ ", apiEnum=" + apiEnum + ", request=" + request + ", response=" + response + ", queryParam="
				+ queryParam + ", isCSIAuditLog=" + isCSIAuditLog + ", isLoggablePayload=" + isLoggablePayload
				+ ", isLoggableParam=" + isLoggableParam + "]";
	}

	public LogSupport() {
		super();
	}
}
