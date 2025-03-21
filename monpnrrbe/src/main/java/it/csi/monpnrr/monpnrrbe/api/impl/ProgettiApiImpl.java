/*
* SPDX-FileCopyrightText: (C) Copyright 2025 Regione Piemonte
*
* SPDX-License-Identifier: EUPL-1.2 
*/
package it.csi.monpnrr.monpnrrbe.api.impl;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.csi.monpnrr.monpnrrbe.api.ProgettiApi;
import it.csi.monpnrr.monpnrrbe.api.dto.ChecklistProgettoList;
import it.csi.monpnrr.monpnrrbe.api.dto.ChecklistRispostaList;
import it.csi.monpnrr.monpnrrbe.api.dto.Errore;
import it.csi.monpnrr.monpnrrbe.api.dto.Progetto;
import it.csi.monpnrr.monpnrrbe.api.dto.ProgettoDettaglio;
import it.csi.monpnrr.monpnrrbe.api.dto.VerificaRispostaList;
import it.csi.monpnrr.monpnrrbe.api.impl.base.RESTBaseService;
import it.csi.monpnrr.monpnrrbe.exception.AziendaSanitariaNonAmmessaException;
import it.csi.monpnrr.monpnrrbe.exception.CambioStatoNonPossibileException;
import it.csi.monpnrr.monpnrrbe.exception.CsvException;
import it.csi.monpnrr.monpnrrbe.exception.DatabaseException;
import it.csi.monpnrr.monpnrrbe.exception.EsitoNonApplicabilePerAvanzamentoException;
import it.csi.monpnrr.monpnrrbe.exception.EsitoNotFoundException;
import it.csi.monpnrr.monpnrrbe.exception.PdfException;
import it.csi.monpnrr.monpnrrbe.exception.PdfNonDisponibileException;
import it.csi.monpnrr.monpnrrbe.exception.ProfiloNonAmmessoException;
import it.csi.monpnrr.monpnrrbe.exception.RESTException;
import it.csi.monpnrr.monpnrrbe.integration.csv.CsvService;
import it.csi.monpnrr.monpnrrbe.integration.dao.DecodificheDao;
import it.csi.monpnrr.monpnrrbe.integration.dao.ProgettiDao;
import it.csi.monpnrr.monpnrrbe.integration.dao.dto.CambiStatoDto;
import it.csi.monpnrr.monpnrrbe.integration.dao.dto.ChecklistProgettoDto;
import it.csi.monpnrr.monpnrrbe.integration.dao.dto.FiltersDto;
import it.csi.monpnrr.monpnrrbe.integration.dao.dto.ProceduraCigDto;
import it.csi.monpnrr.monpnrrbe.integration.pdf.PdfService;
import it.csi.monpnrr.monpnrrbe.util.Constants;
import it.csi.monpnrr.monpnrrbe.util.Util;
import it.csi.monpnrr.monpnrrbe.util.enumeration.ApiMethodEnum;
import it.csi.monpnrr.monpnrrbe.util.enumeration.HeaderEnum;
import it.csi.monpnrr.monpnrrbe.util.enumeration.ProfiloEnum;
import it.csi.monpnrr.monpnrrbe.util.log.HttpHeaderParam;
import it.csi.monpnrr.monpnrrbe.util.log.LogSupport;
import it.csi.monpnrr.monpnrrbe.util.log.LogSupportBuilder;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;

@Service
public class ProgettiApiImpl extends RESTBaseService implements ProgettiApi {

	@Autowired
	ProgettiDao progettiDao;
	
	@Autowired
	DecodificheDao decodificheDao;
	
	@Autowired
	PdfService pdfService;
	
	@Autowired
	CsvService csvService;
	
	@Override
	public Response progettiProfiloGet(String shibIdentitaCodiceFiscale, String xRequestId, String xCodiceServizio,
			String xForwardedFor, String profilo, String cup, String checklist, String stato, String modificaRupDa,
			String modificaRupA, String aziendaSanitaria, String rua, String rup, Boolean verificatoLiv2,
			String modificaVerifica2Da, String modificaVerifica2A, Boolean verificatoLiv3, String modificaVerifica3Da,
			String modificaVerifica3A, Boolean modificatoRupDopoVerifica, String modificaRupDopoVerificaDa,
			String modificaRupDopoVerificaA, String orderBy, Boolean descending, BigDecimal pageNumber,
			BigDecimal rowPerPage, SecurityContext securityContext, HttpHeaders httpHeaders,
			HttpServletRequest httpRequest) {
		String metodo = new Object() {}.getClass().getEnclosingMethod().getName();
		Errore error = new Errore();
		HttpHeaderParam headerParam = new HttpHeaderParam(shibIdentitaCodiceFiscale, xRequestId, xForwardedFor, xCodiceServizio);
		LogSupport ls = LogSupportBuilder
				.init(metodo, headerParam, httpRequest.getRequestURI(), ApiMethodEnum.GET)
				.queryParam(httpRequest.getQueryString())
				.isLoggableParam(true)
				.build();
		logAudit(ls);
		
		try {
			checkHeaders(shibIdentitaCodiceFiscale, xRequestId, xForwardedFor, xCodiceServizio);
			FiltersDto filters = new FiltersDto(cup, checklist, stato, modificaRupDa, 
					modificaRupA, aziendaSanitaria, rua, rup, verificatoLiv2, modificaVerifica2Da, modificaVerifica2A, 
					verificatoLiv3, modificaVerifica3Da, modificaVerifica3A, 
					modificatoRupDopoVerifica, modificaRupDopoVerificaDa, modificaRupDopoVerificaA, 
					orderBy, descending, pageNumber, rowPerPage);
			
			ProfiloEnum profiloEnum = checkProfilo(profilo);
			checkAziendaSanitaria(profilo, filters.aziendaSanitaria());
			
			List<Progetto> result = progettiDao.getProgetti(shibIdentitaCodiceFiscale, profiloEnum, filters);
			ls.setResponse(result);
			insertLogAuditSuccess(ls);
			return Response.ok(result).header(HeaderEnum.ROWS_NUMBER.getCode(), getTotalCount(result)).build();
		} catch (RESTException e) {
			error = handleRESTException(metodo, e);
		} catch (IllegalArgumentException ex) {
			RESTException e = new RESTException(ex);
			error = handleRESTException(metodo, e);
		} catch (DatabaseException e) {
			error = handleDatabaseException(metodo, e);
		} catch (Exception e) {
			error = handleException(metodo, e);
		}
		
		ls.setResponse(error);
		insertLogAuditError(ls,error.getStatus());
		return generateResponseError(error);
	}

	@Override
	public Response progettiProgettoCupChecklistProfiloGet(String shibIdentitaCodiceFiscale, String xRequestId,
			String xCodiceServizio, String xForwardedFor, String progettoCup, String profilo,
			SecurityContext securityContext, HttpHeaders httpHeaders, HttpServletRequest httpRequest) {
		String metodo = new Object() {}.getClass().getEnclosingMethod().getName();
		Errore error = new Errore();
		HttpHeaderParam headerParam = new HttpHeaderParam(shibIdentitaCodiceFiscale, xRequestId, xForwardedFor, xCodiceServizio);
		LogSupport ls = LogSupportBuilder
				.init(metodo,headerParam, httpRequest.getRequestURI(), ApiMethodEnum.GET)
				.isLoggableParam(true)
				.build();
		logAudit(ls);
		
		try {
			checkHeaders(shibIdentitaCodiceFiscale, xRequestId, xForwardedFor, xCodiceServizio);
			ProfiloEnum profiloEnum = checkProfilo(profilo);
			String livello = getLivelloFromProfilo(profiloEnum);
			ChecklistProgettoList result = progettiDao.getChecklistProgetto(progettoCup, livello);
			ls.setResponse(result);
			insertLogAuditSuccess(ls);
			return Response.ok(result).build();
		} catch (RESTException e) {
			error = handleRESTException(metodo, e);
		} catch (DatabaseException e) {
			error = handleDatabaseException(metodo, e);
		} catch (Exception e) {
			error = handleException(metodo, e);
		}
		
		ls.setResponse(error);
		insertLogAuditError(ls,error.getStatus());
		return generateResponseError(error);
	}

	@Override
	@Transactional(rollbackFor = Throwable.class)
	public Response progettiProgettoCupChecklistProfiloPut(ChecklistRispostaList body, String shibIdentitaCodiceFiscale,
			String xRequestId, String xCodiceServizio, String xForwardedFor, String progettoCup, String profilo,
			SecurityContext securityContext, HttpHeaders httpHeaders, HttpServletRequest httpRequest) {
		String metodo = new Object() {}.getClass().getEnclosingMethod().getName();
		Errore error = new Errore();
		HttpHeaderParam headerParam = new HttpHeaderParam(shibIdentitaCodiceFiscale, xRequestId, xForwardedFor, xCodiceServizio);
		LogSupport ls = LogSupportBuilder
				.init(metodo,headerParam, httpRequest.getRequestURI(), ApiMethodEnum.PUT)
				.payloadRequest(body)
				.isLoggableParam(true)
				.build();
		logAudit(ls);
		
		try {
			checkHeaders(shibIdentitaCodiceFiscale, xRequestId, xForwardedFor, xCodiceServizio);
			checkBodyASR(body);
			String statoAttuale = progettiDao.getStatoProgetto(progettoCup);
			List<CambiStatoDto> cambiStatoDto = decodificheDao.getCambiStatoDto();
			checkCambioStato(statoAttuale, body.getStato(), cambiStatoDto);
			
			progettiDao.saveRisposte(progettoCup, shibIdentitaCodiceFiscale, statoAttuale, body);
			
			ls.setResponse(true);
			insertLogAuditSuccess(ls);
			return Response.ok().build();
		} catch (CambioStatoNonPossibileException e) {
			error = handleCambioStatoNonPossibileException(metodo, e);
		} catch (EsitoNotFoundException e) {
			error = handleEsitoNotFoundException(metodo, e);
		} catch (EsitoNonApplicabilePerAvanzamentoException e) {
			error = handleEsitoNonApplicabilePerAvanzamentoException(metodo, e);
		} catch (RESTException e) {
			error = handleRESTException(metodo, e);
		} catch (DatabaseException e) {
			error = handleDatabaseException(metodo, e);
		} catch (Exception e) {
			error = handleException(metodo, e);
		}
		
		ls.setResponse(error);
		insertLogAuditError(ls,error.getStatus());
		return generateResponseError(error);
	}

	@Override
	public Response progettiProgettoCupDettaglioProfiloGet(String shibIdentitaCodiceFiscale, String xRequestId,
			String xCodiceServizio, String xForwardedFor, String progettoCup, String profilo,
			SecurityContext securityContext, HttpHeaders httpHeaders, HttpServletRequest httpRequest) {
		String metodo = new Object() {}.getClass().getEnclosingMethod().getName();
		Errore error = new Errore();
		HttpHeaderParam headerParam = new HttpHeaderParam(shibIdentitaCodiceFiscale, xRequestId, xForwardedFor, xCodiceServizio);
		LogSupport ls = LogSupportBuilder
				.init(metodo,headerParam, httpRequest.getRequestURI(), ApiMethodEnum.GET)
				.isLoggableParam(true)
				.build();
		logAudit(ls);
		
		try {
			checkHeaders(shibIdentitaCodiceFiscale, xRequestId, xForwardedFor, xCodiceServizio);
			checkProfilo(profilo);
			ProgettoDettaglio result = progettiDao.getProgettoDettaglio(progettoCup);
			ls.setResponse(result);
			insertLogAuditSuccess(ls);
			return Response.ok(result).build();
		} catch (RESTException e) {
			error = handleRESTException(metodo, e);
		} catch (DatabaseException e) {
			error = handleDatabaseException(metodo, e);
		} catch (Exception e) {
			error = handleException(metodo, e);
		}
		
		ls.setResponse(error);
		insertLogAuditError(ls,error.getStatus());
		return generateResponseError(error);
	}

	@Override
	public Response progettiProgettoCupPdfProfiloGet(String shibIdentitaCodiceFiscale, String xRequestId,
			String xCodiceServizio, String xForwardedFor, String progettoCup, String profilo, String aziendaSanitaria,
			SecurityContext securityContext, HttpHeaders httpHeaders, HttpServletRequest httpRequest) {
		String metodo = new Object() {}.getClass().getEnclosingMethod().getName();
		Errore error = new Errore();
		HttpHeaderParam headerParam = new HttpHeaderParam(shibIdentitaCodiceFiscale, xRequestId, xForwardedFor, xCodiceServizio);
		LogSupport ls = LogSupportBuilder
				.init(metodo, headerParam, httpRequest.getRequestURI(), ApiMethodEnum.GET)
				.queryParam(httpRequest.getQueryString())
				.isLoggableParam(true)
				.build();
		logAudit(ls);
		
		try {
			checkHeaders(shibIdentitaCodiceFiscale, xRequestId, xForwardedFor, xCodiceServizio);
			String statoAttuale = progettiDao.getStatoProgetto(progettoCup);
			checkStatus(statoAttuale);
			aziendaSanitaria = FiltersDto.convertToNullIfStringNull(Util.cleanString(aziendaSanitaria));
			checkAziendaSanitaria(profilo, aziendaSanitaria);
			
			Boolean isAziendaCorretta = progettiDao.checkAziendaSanitariaProgetto(progettoCup, aziendaSanitaria);
			checkAziendaSanitariaProgetto(isAziendaCorretta, profilo);
			
			ProfiloEnum profiloEnum = checkProfilo(profilo);
			String livello = getLivelloFromProfilo(profiloEnum);
			
			List<ChecklistProgettoDto> checklistProgettiDto = progettiDao.getChecklistProgettoDto(progettoCup, livello);
			Integer rilevazioneId = progettiDao.getRilevazioneByCup(progettoCup);
			List<ProceduraCigDto> procedureCigDto = progettiDao.getProcedureCigDto(rilevazioneId);
			ProgettoDettaglio progettoDettaglio = progettiDao.getProgettoDettaglio(progettoCup);
			byte[] result = pdfService.createPdfChecklist(checklistProgettiDto, procedureCigDto, progettoDettaglio, statoAttuale, profiloEnum);
			ls.setResponse(result);
			insertLogAuditSuccess(ls);
			
			Date date = new Date();
			String dateString = Util.fromDateToString(date, Constants.FORMATTER_FILE);
			String nomeFile = Constants.PDF_FILE_NAME + progettoCup + "_" + dateString + Constants.PDF_EXTENSION;

			return Response.ok().entity(result)
					.header(HttpHeaders.CONTENT_DISPOSITION, nomeFile)
					.build();
		} catch (PdfException e) {
			error = handlePdfException(metodo, e);
		} catch (PdfNonDisponibileException e) {
			error = handlePdfNonDisponibileException(metodo, e);
		} catch (AziendaSanitariaNonAmmessaException e) {
			error = handleAziendaSanitariaNonAmmessaException(metodo, e);
		} catch (RESTException e) {
			error = handleRESTException(metodo, e);
		} catch (DatabaseException e) {
			error = handleDatabaseException(metodo, e);
		} catch (Exception e) {
			error = handleException(metodo, e);
		}
		
		ls.setResponse(error);
		insertLogAuditError(ls,error.getStatus());
		return generateResponseError(error);
	}

	@Override
	public Response progettiCsvProfiloGet(String shibIdentitaCodiceFiscale, String xRequestId, String xCodiceServizio,
			String xForwardedFor, String profilo, String cup, String checklist, String stato, String modificaRupDa,
			String modificaRupA, String aziendaSanitaria, String rua, String rup, Boolean verificatoLiv2,
			String modificaVerifica2Da, String modificaVerifica2A, Boolean verificatoLiv3, String modificaVerifica3Da,
			String modificaVerifica3A, Boolean modificatoRupDopoVerifica, String modificaRupDopoVerificaDa,
			String modificaRupDopoVerificaA, String orderBy, Boolean descending, SecurityContext securityContext,
			HttpHeaders httpHeaders, HttpServletRequest httpRequest) {
		String metodo = new Object() {}.getClass().getEnclosingMethod().getName();
		Errore error = new Errore();
		HttpHeaderParam headerParam = new HttpHeaderParam(shibIdentitaCodiceFiscale, xRequestId, xForwardedFor, xCodiceServizio);
		LogSupport ls = LogSupportBuilder
				.init(metodo,headerParam, httpRequest.getRequestURI(), ApiMethodEnum.GET)
				.queryParam(httpRequest.getQueryString())
				.isLoggableParam(true)
				.build();
		logAudit(ls);
		
		try {
			checkHeaders(shibIdentitaCodiceFiscale, xRequestId, xForwardedFor, xCodiceServizio);
			FiltersDto filters = new FiltersDto(cup, checklist, stato, modificaRupDa, 
					modificaRupA, aziendaSanitaria, rua, rup, verificatoLiv2, modificaVerifica2Da, modificaVerifica2A, 
					verificatoLiv3, modificaVerifica3Da, modificaVerifica3A, 
					modificatoRupDopoVerifica, modificaRupDopoVerificaDa, modificaRupDopoVerificaA, 
					orderBy, descending, null, null);
			
			ProfiloEnum profiloEnum = checkProfilo(profilo);
			checkAziendaSanitaria(profilo, filters.aziendaSanitaria());
			
			List<Progetto> progetti = progettiDao.getProgetti(shibIdentitaCodiceFiscale, profiloEnum, filters);
			
			byte[] result = csvService.createCsvProgetti(progetti, profiloEnum).getBytes(StandardCharsets.ISO_8859_1);
			ls.setResponse(result);
			insertLogAuditSuccess(ls);
			
			Date date = new Date();
			String dateString = Util.fromDateToString(date, Constants.FORMATTER_FILE);
			String nomeFile = Constants.CSV_FILE_NAME + dateString + Constants.CSV_EXTENSION;
			
			return Response.ok().entity(result)
					.header(HttpHeaders.CONTENT_DISPOSITION, nomeFile)
					.build();
		} catch (CsvException e) {
			error = handleCsvException(metodo, e);
		} catch (RESTException e) {
			error = handleRESTException(metodo, e);
		} catch (DatabaseException e) {
			error = handleDatabaseException(metodo, e);
		} catch (Exception e) {
			error = handleException(metodo, e);
		}
		
		ls.setResponse(error);
		insertLogAuditError(ls,error.getStatus());
		return generateResponseError(error);
	}

	@Override
	@Transactional(rollbackFor = Throwable.class)
	public Response progettiProgettoCupChecklistVerificaProfiloPut(VerificaRispostaList body,
			String shibIdentitaCodiceFiscale, String xRequestId, String xCodiceServizio, String xForwardedFor,
			String progettoCup, String profilo, SecurityContext securityContext, HttpHeaders httpHeaders,
			HttpServletRequest httpRequest) {
		String metodo = new Object() {}.getClass().getEnclosingMethod().getName();
		Errore error = new Errore();
		HttpHeaderParam headerParam = new HttpHeaderParam(shibIdentitaCodiceFiscale, xRequestId, xForwardedFor, xCodiceServizio);
		LogSupport ls = LogSupportBuilder
				.init(metodo,headerParam, httpRequest.getRequestURI(), ApiMethodEnum.PUT)
				.payloadRequest(body)
				.isLoggableParam(true)
				.build();
		logAudit(ls);
		
		try {
			checkHeaders(shibIdentitaCodiceFiscale, xRequestId, xForwardedFor, xCodiceServizio);
			ProfiloEnum profiloEnum = checkProfiloVerifica(profilo);
			checkBodyRP(body);

			String livello = getLivelloFromProfilo(profiloEnum);
			progettiDao.saveVerifiche(progettoCup, shibIdentitaCodiceFiscale, body, livello);
			
			ls.setResponse(true);
			insertLogAuditSuccess(ls);
			return Response.ok().build();
		} catch (ProfiloNonAmmessoException e) {
			error = handleProfiloNonAmmessoException(metodo, e);
		} catch (RESTException e) {
			error = handleRESTException(metodo, e);
		} catch (DatabaseException e) {
			error = handleDatabaseException(metodo, e);
		} catch (Exception e) {
			error = handleException(metodo, e);
		}
		
		ls.setResponse(error);
		insertLogAuditError(ls,error.getStatus());
		return generateResponseError(error);
	}
}