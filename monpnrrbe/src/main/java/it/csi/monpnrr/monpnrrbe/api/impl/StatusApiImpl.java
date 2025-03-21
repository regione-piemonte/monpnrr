/*
* SPDX-FileCopyrightText: (C) Copyright 2025 Regione Piemonte
*
* SPDX-License-Identifier: EUPL-1.2 
*/
package it.csi.monpnrr.monpnrrbe.api.impl;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import it.csi.monpnrr.monpnrrbe.api.StatusApi;
import it.csi.monpnrr.monpnrrbe.api.impl.base.RESTBaseService;
import it.csi.monpnrr.monpnrrbe.exception.DatabaseException;
import it.csi.monpnrr.monpnrrbe.integration.dao.DecodificheDao;
import it.csi.monpnrr.monpnrrbe.util.enumeration.ApiMethodEnum;
import it.csi.monpnrr.monpnrrbe.util.log.HttpHeaderParam;
import it.csi.monpnrr.monpnrrbe.util.log.LogSupport;
import it.csi.monpnrr.monpnrrbe.util.log.LogSupportBuilder;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;

@Service
public class StatusApiImpl extends RESTBaseService implements StatusApi {

	@Autowired
	DecodificheDao decodificheDao;
	
	@Override
	public Response statusGet(String shibIdentitaCodiceFiscale, String xRequestId, String xCodiceServizio, String xForwardedFor,
			SecurityContext securityContext, HttpHeaders httpHeaders,
			HttpServletRequest httpRequest) {
		String metodo = new Object() {}.getClass().getEnclosingMethod().getName();
		HttpHeaderParam headerParam = new HttpHeaderParam(shibIdentitaCodiceFiscale, xRequestId, xForwardedFor, xCodiceServizio);
		LogSupport ls = LogSupportBuilder
				.init(metodo,headerParam, httpRequest.getRequestURI(), ApiMethodEnum.GET)
				.isLoggableParam(true)
				.build();
		logAudit(ls);
		
		try {
            decodificheDao.getRisposte();
        } catch (DatabaseException e) {
            logError(metodo, "invoked at "+ LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME) + " with error", e);
            return Response.serverError().entity("Status KO").build();
        }
		
		logInfo(metodo,"invoked at "+ LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME));
		return Response.ok().entity("Status OK").build();
	}
}