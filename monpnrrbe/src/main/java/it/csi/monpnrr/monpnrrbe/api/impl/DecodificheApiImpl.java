/*
* SPDX-FileCopyrightText: (C) Copyright 2025 Regione Piemonte
*
* SPDX-License-Identifier: EUPL-1.2 
*/
package it.csi.monpnrr.monpnrrbe.api.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import it.csi.monpnrr.monpnrrbe.api.DecodificheApi;
import it.csi.monpnrr.monpnrrbe.api.dto.CambiStato;
import it.csi.monpnrr.monpnrrbe.api.dto.DateModifica;
import it.csi.monpnrr.monpnrrbe.api.dto.Decodifica;
import it.csi.monpnrr.monpnrrbe.api.dto.Errore;
import it.csi.monpnrr.monpnrrbe.api.impl.base.RESTBaseService;
import it.csi.monpnrr.monpnrrbe.exception.DatabaseException;
import it.csi.monpnrr.monpnrrbe.exception.RESTException;
import it.csi.monpnrr.monpnrrbe.integration.dao.DecodificheDao;
import it.csi.monpnrr.monpnrrbe.util.enumeration.ApiMethodEnum;
import it.csi.monpnrr.monpnrrbe.util.enumeration.ProfiloEnum;
import it.csi.monpnrr.monpnrrbe.util.log.HttpHeaderParam;
import it.csi.monpnrr.monpnrrbe.util.log.LogSupport;
import it.csi.monpnrr.monpnrrbe.util.log.LogSupportBuilder;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;

@Service
public class DecodificheApiImpl extends RESTBaseService implements DecodificheApi {

	@Autowired
	DecodificheDao decodificheDao;

	@Override
	public Response decodificheProcedureProfiloGet(String shibIdentitaCodiceFiscale, String xRequestId,
			String xCodiceServizio, String xForwardedFor, String profilo, SecurityContext securityContext,
			HttpHeaders httpHeaders, HttpServletRequest httpRequest) {
		String metodo = new Object() {}.getClass().getEnclosingMethod().getName();
		Errore error = new Errore();
		HttpHeaderParam headerParam = new HttpHeaderParam(shibIdentitaCodiceFiscale, xRequestId, xForwardedFor, xCodiceServizio);
		LogSupport ls = LogSupportBuilder.init(metodo, headerParam, httpRequest.getRequestURI(), ApiMethodEnum.GET)
				.isLoggableParam(true)
				.build();
		logAudit(ls);

		try {
			checkHeaders(shibIdentitaCodiceFiscale, xRequestId, xForwardedFor, xCodiceServizio);
			List<Decodifica> result = decodificheDao.getProcedure();
			ls.setResponse(result);
			insertLogAuditSuccess(ls);
			return Response.ok(result).build();

		} catch (DatabaseException e) {
			error = handleDatabaseException(metodo, e);
		} catch (RESTException e) {
			error = handleRESTException(metodo, e);
		} catch (Exception e) {
			error = handleException(metodo, e);
		}
		
		ls.setResponse(error);
		insertLogAuditError(ls, error.getStatus());
		return generateResponseError(error);
	}

	@Override
	public Response decodificheRisposteProfiloGet(String shibIdentitaCodiceFiscale, String xRequestId,
			String xCodiceServizio, String xForwardedFor, String profilo, SecurityContext securityContext,
			HttpHeaders httpHeaders, HttpServletRequest httpRequest) {
		String metodo = new Object() {}.getClass().getEnclosingMethod().getName();
		Errore error = new Errore();
		HttpHeaderParam headerParam = new HttpHeaderParam(shibIdentitaCodiceFiscale, xRequestId, xForwardedFor, xCodiceServizio);
		LogSupport ls = LogSupportBuilder.init(metodo, headerParam, httpRequest.getRequestURI(), ApiMethodEnum.GET)
				.isLoggableParam(true)
				.build();
		logAudit(ls);

		try {
			checkHeaders(shibIdentitaCodiceFiscale, xRequestId, xForwardedFor, xCodiceServizio);
			List<Decodifica> result = decodificheDao.getRisposte();
			ls.setResponse(result);
			insertLogAuditSuccess(ls);
			return Response.ok(result).build();

		} catch (DatabaseException e) {
			error = handleDatabaseException(metodo, e);
		} catch (RESTException e) {
			error = handleRESTException(metodo, e);
		} catch (Exception e) {
			error = handleException(metodo, e);
		}
		
		ls.setResponse(error);
		insertLogAuditError(ls, error.getStatus());
		return generateResponseError(error);
	}

	@Override
	public Response decodificheCambiStatoProfiloGet(String shibIdentitaCodiceFiscale, String xRequestId,
			String xCodiceServizio, String xForwardedFor, String profilo, SecurityContext securityContext,
			HttpHeaders httpHeaders, HttpServletRequest httpRequest) {
		String metodo = new Object() {}.getClass().getEnclosingMethod().getName();
		Errore error = new Errore();
		HttpHeaderParam headerParam = new HttpHeaderParam(shibIdentitaCodiceFiscale, xRequestId, xForwardedFor, xCodiceServizio);
		LogSupport ls = LogSupportBuilder.init(metodo, headerParam, httpRequest.getRequestURI(), ApiMethodEnum.GET)
				.isLoggableParam(true)
				.build();
		logAudit(ls);

		try {
			checkHeaders(shibIdentitaCodiceFiscale, xRequestId, xForwardedFor, xCodiceServizio);
			List<CambiStato> result = decodificheDao.getCambiStato();
			ls.setResponse(result);
			insertLogAuditSuccess(ls);
			return Response.ok(result).build();

		} catch (DatabaseException e) {
			error = handleDatabaseException(metodo, e);
		} catch (RESTException e) {
			error = handleRESTException(metodo, e);
		} catch (Exception e) {
			error = handleException(metodo, e);
		}
		
		ls.setResponse(error);
		insertLogAuditError(ls, error.getStatus());
		return generateResponseError(error);
	}

	@Override
	public Response decodificheAziendaSanitariaProfiloGet(String shibIdentitaCodiceFiscale, String xRequestId,
			String xCodiceServizio, String xForwardedFor, String profilo, SecurityContext securityContext,
			HttpHeaders httpHeaders, HttpServletRequest httpRequest) {
		String metodo = new Object() {}.getClass().getEnclosingMethod().getName();
		Errore error = new Errore();
		HttpHeaderParam headerParam = new HttpHeaderParam(shibIdentitaCodiceFiscale, xRequestId, xForwardedFor, xCodiceServizio);
		LogSupport ls = LogSupportBuilder.init(metodo, headerParam, httpRequest.getRequestURI(), ApiMethodEnum.GET)
				.isLoggableParam(true)
				.build();
		logAudit(ls);

		try {
			checkHeaders(shibIdentitaCodiceFiscale, xRequestId, xForwardedFor, xCodiceServizio);
			List<Decodifica> result = decodificheDao.getAziendeSanitarie();
			ls.setResponse(result);
			insertLogAuditSuccess(ls);
			return Response.ok(result).build();

		} catch (DatabaseException e) {
			error = handleDatabaseException(metodo, e);
		} catch (RESTException e) {
			error = handleRESTException(metodo, e);
		} catch (Exception e) {
			error = handleException(metodo, e);
		}
		
		ls.setResponse(error);
		insertLogAuditError(ls, error.getStatus());
		return generateResponseError(error);
	}

	@Override
	public Response decodificheChecklistProfiloGet(String shibIdentitaCodiceFiscale, String xRequestId,
			String xCodiceServizio, String xForwardedFor, String profilo, SecurityContext securityContext,
			HttpHeaders httpHeaders, HttpServletRequest httpRequest) {
		String metodo = new Object() {}.getClass().getEnclosingMethod().getName();
		Errore error = new Errore();
		HttpHeaderParam headerParam = new HttpHeaderParam(shibIdentitaCodiceFiscale, xRequestId, xForwardedFor, xCodiceServizio);
		LogSupport ls = LogSupportBuilder.init(metodo, headerParam, httpRequest.getRequestURI(), ApiMethodEnum.GET)
				.isLoggableParam(true)
				.build();
		logAudit(ls);

		try {
			checkHeaders(shibIdentitaCodiceFiscale, xRequestId, xForwardedFor, xCodiceServizio);
			List<Decodifica> result = decodificheDao.getChecklist();
			ls.setResponse(result);
			insertLogAuditSuccess(ls);
			return Response.ok(result).build();

		} catch (DatabaseException e) {
			error = handleDatabaseException(metodo, e);
		} catch (RESTException e) {
			error = handleRESTException(metodo, e);
		} catch (Exception e) {
			error = handleException(metodo, e);
		}
		
		ls.setResponse(error);
		insertLogAuditError(ls, error.getStatus());
		return generateResponseError(error);
	}

	@Override
	public Response decodificheRuaProfiloGet(String shibIdentitaCodiceFiscale, String xRequestId,
			String xCodiceServizio, String xForwardedFor, String profilo, SecurityContext securityContext,
			HttpHeaders httpHeaders, HttpServletRequest httpRequest) {
		String metodo = new Object() {}.getClass().getEnclosingMethod().getName();
		Errore error = new Errore();
		HttpHeaderParam headerParam = new HttpHeaderParam(shibIdentitaCodiceFiscale, xRequestId, xForwardedFor, xCodiceServizio);
		LogSupport ls = LogSupportBuilder.init(metodo, headerParam, httpRequest.getRequestURI(), ApiMethodEnum.GET)
				.isLoggableParam(true)
				.build();
		logAudit(ls);

		try {
			checkHeaders(shibIdentitaCodiceFiscale, xRequestId, xForwardedFor, xCodiceServizio);
			List<Decodifica> result = decodificheDao.getUtente(ProfiloEnum.RUA.name());
			ls.setResponse(result);
			insertLogAuditSuccess(ls);
			return Response.ok(result).build();

		} catch (DatabaseException e) {
			error = handleDatabaseException(metodo, e);
		} catch (RESTException e) {
			error = handleRESTException(metodo, e);
		} catch (Exception e) {
			error = handleException(metodo, e);
		}
		
		ls.setResponse(error);
		insertLogAuditError(ls, error.getStatus());
		return generateResponseError(error);
	}

	@Override
	public Response decodificheRupProfiloGet(String shibIdentitaCodiceFiscale, String xRequestId,
			String xCodiceServizio, String xForwardedFor, String profilo, SecurityContext securityContext,
			HttpHeaders httpHeaders, HttpServletRequest httpRequest) {
		String metodo = new Object() {}.getClass().getEnclosingMethod().getName();
		Errore error = new Errore();
		HttpHeaderParam headerParam = new HttpHeaderParam(shibIdentitaCodiceFiscale, xRequestId, xForwardedFor, xCodiceServizio);
		LogSupport ls = LogSupportBuilder.init(metodo, headerParam, httpRequest.getRequestURI(), ApiMethodEnum.GET)
				.isLoggableParam(true)
				.build();
		logAudit(ls);

		try {
			checkHeaders(shibIdentitaCodiceFiscale, xRequestId, xForwardedFor, xCodiceServizio);
			List<Decodifica> result = decodificheDao.getUtente(ProfiloEnum.RUP.name());
			ls.setResponse(result);
			insertLogAuditSuccess(ls);
			return Response.ok(result).build();

		} catch (DatabaseException e) {
			error = handleDatabaseException(metodo, e);
		} catch (RESTException e) {
			error = handleRESTException(metodo, e);
		} catch (Exception e) {
			error = handleException(metodo, e);
		}
		
		ls.setResponse(error);
		insertLogAuditError(ls, error.getStatus());
		return generateResponseError(error);
	}

	@Override
	public Response decodificheDateModificaProfiloGet(String shibIdentitaCodiceFiscale, String xRequestId,
			String xCodiceServizio, String xForwardedFor, String profilo, SecurityContext securityContext,
			HttpHeaders httpHeaders, HttpServletRequest httpRequest) {
		String metodo = new Object() {}.getClass().getEnclosingMethod().getName();
		Errore error = new Errore();
		HttpHeaderParam headerParam = new HttpHeaderParam(shibIdentitaCodiceFiscale, xRequestId, xForwardedFor, xCodiceServizio);
		LogSupport ls = LogSupportBuilder.init(metodo, headerParam, httpRequest.getRequestURI(), ApiMethodEnum.GET)
				.isLoggableParam(true)
				.build();
		logAudit(ls);

		try {
			checkHeaders(shibIdentitaCodiceFiscale, xRequestId, xForwardedFor, xCodiceServizio);
			ProfiloEnum profiloEnum = checkProfilo(profilo);
			DateModifica result = decodificheDao.getDateModifica(profiloEnum);
			ls.setResponse(result);
			insertLogAuditSuccess(ls);
			return Response.ok(result).build();

		} catch (DatabaseException e) {
			error = handleDatabaseException(metodo, e);
		} catch (RESTException e) {
			error = handleRESTException(metodo, e);
		} catch (Exception e) {
			error = handleException(metodo, e);
		}
		
		ls.setResponse(error);
		insertLogAuditError(ls, error.getStatus());
		return generateResponseError(error);
	}
}