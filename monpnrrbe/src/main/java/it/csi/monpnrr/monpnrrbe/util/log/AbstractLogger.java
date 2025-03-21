/*
* SPDX-FileCopyrightText: (C) Copyright 2025 Regione Piemonte
*
* SPDX-License-Identifier: EUPL-1.2 
*/
package it.csi.monpnrr.monpnrrbe.util.log;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.spi.LoggingEventBuilder;

public class AbstractLogger {
	private final Logger log = LoggerFactory.getLogger(getClass());

	protected void logInfo(String methodName, String message, Object arg) {
		info().log(buildMessage(methodName, message), arg);
	}

	protected void logInfo(String methodName, String message) {
		info().log(buildMessage(methodName, message));
	}

	protected void logDebug(String methodName, String message, Object arg) {
		debug().log(buildMessage(methodName, message), arg);
	}

	protected void logDebug(String methodName, String message) {
		debug().log(buildMessage(methodName, message));
	}

	protected void logWarn(String methodName, String message, Object arg) {
		warn().log(buildMessage(methodName, message), arg);
	}

	protected void logWarn(String methodName, String message) {
		warn().log(buildMessage(methodName, message));
	}

	protected void logError(String methodName, String message, Throwable t, Object arg) {
		error().setCause(t).log(buildMessage(methodName, message), arg);
	}

	protected void logError(String methodName, String message, Throwable t) {
		error().setCause(t).log(buildMessage(methodName, message));
	}

	protected void logError(String methodName, String message) {
		error().log(buildMessage(methodName, message));
	}
	
	private LoggingEventBuilder info() {
		return log.atInfo();
	}

	private LoggingEventBuilder debug() {
		return log.atDebug();
	}

	private LoggingEventBuilder warn() {
		return log.atWarn();
	}
	
	private LoggingEventBuilder error() {
		return log.atError();
	}

	private String buildMessage(String methodName, String message) {
		return "["+methodName+"] : " + message;
	}
}
