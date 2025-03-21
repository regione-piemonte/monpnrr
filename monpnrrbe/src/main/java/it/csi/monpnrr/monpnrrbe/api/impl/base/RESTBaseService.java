/*
* SPDX-FileCopyrightText: (C) Copyright 2025 Regione Piemonte
*
* SPDX-License-Identifier: EUPL-1.2 
*/
package it.csi.monpnrr.monpnrrbe.api.impl.base;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import io.micrometer.common.util.StringUtils;
import it.csi.monpnrr.monpnrrbe.api.dto.ChecklistRisposta;
import it.csi.monpnrr.monpnrrbe.api.dto.ChecklistRispostaList;
import it.csi.monpnrr.monpnrrbe.api.dto.Dettaglio;
import it.csi.monpnrr.monpnrrbe.api.dto.Errore;
import it.csi.monpnrr.monpnrrbe.api.dto.ProceduraRisposteCig;
import it.csi.monpnrr.monpnrrbe.api.dto.Progetto;
import it.csi.monpnrr.monpnrrbe.api.dto.VerificaRisposta;
import it.csi.monpnrr.monpnrrbe.api.dto.VerificaRispostaCig;
import it.csi.monpnrr.monpnrrbe.api.dto.VerificaRispostaList;
import it.csi.monpnrr.monpnrrbe.exception.AziendaSanitariaNonAmmessaException;
import it.csi.monpnrr.monpnrrbe.exception.CambioStatoNonPossibileException;
import it.csi.monpnrr.monpnrrbe.exception.ConfiguratoreGeneralException;
import it.csi.monpnrr.monpnrrbe.exception.ConfiguratoreIPException;
import it.csi.monpnrr.monpnrrbe.exception.CsvException;
import it.csi.monpnrr.monpnrrbe.exception.DatabaseException;
import it.csi.monpnrr.monpnrrbe.exception.EsitoNonApplicabilePerAvanzamentoException;
import it.csi.monpnrr.monpnrrbe.exception.EsitoNotFoundException;
import it.csi.monpnrr.monpnrrbe.exception.PdfException;
import it.csi.monpnrr.monpnrrbe.exception.PdfNonDisponibileException;
import it.csi.monpnrr.monpnrrbe.exception.ProfiloNonAmmessoException;
import it.csi.monpnrr.monpnrrbe.exception.RESTException;
import it.csi.monpnrr.monpnrrbe.integration.dao.dto.CambiStatoDto;
import it.csi.monpnrr.monpnrrbe.util.Util;
import it.csi.monpnrr.monpnrrbe.util.enumeration.BodyRispostaEnum;
import it.csi.monpnrr.monpnrrbe.util.enumeration.CodeErrorEnum;
import it.csi.monpnrr.monpnrrbe.util.enumeration.HeaderEnum;
import it.csi.monpnrr.monpnrrbe.util.enumeration.PathQueryParamEnum;
import it.csi.monpnrr.monpnrrbe.util.enumeration.ProfiloEnum;
import it.csi.monpnrr.monpnrrbe.util.enumeration.RispostaEnum;
import it.csi.monpnrr.monpnrrbe.util.enumeration.StatoRilevazioneEnum;
import it.csi.monpnrr.monpnrrbe.util.enumeration.VerificaLivelloEnum;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Service
public class RESTBaseService extends AuditableApiServiceImpl {
	
	@Value("${IrideIdAdapterFilter.devmode:false}")
	private boolean devmode;
	
	protected void checkToken(String token) throws RESTException {
		checkNotNullString(token, HeaderEnum.TOKEN.getCode());
	}
	
	protected String checkHeadersForLogin(String shibIdentitaCodiceFiscale, String xRequestId, String xCodiceServizio,
			String xForwardedFor, HttpServletRequest httpRequest) throws RESTException {
		shibIdentitaCodiceFiscale = checkCf(shibIdentitaCodiceFiscale, httpRequest);
		checkNotNullString(xRequestId, HeaderEnum.X_REQUEST_ID.getCode());
		checkNotNullString(xCodiceServizio, HeaderEnum.X_CODICE_SERVIZIO.getCode());
		checkNotNullString(xForwardedFor, HeaderEnum.X_FORWARDED_FOR.getCode());
		
		return shibIdentitaCodiceFiscale;
	}
	
	protected void checkHeaders(String shibIdentitaCodiceFiscale, String xRequestId, String xCodiceServizio,
			String xForwardedFor) throws RESTException {
		checkNotNullString(shibIdentitaCodiceFiscale, HeaderEnum.SHIB_IDENTITA_CODICEFISCALE.getCode());
		checkNotNullString(xRequestId, HeaderEnum.X_REQUEST_ID.getCode());
		checkNotNullString(xCodiceServizio, HeaderEnum.X_CODICE_SERVIZIO.getCode());
		checkNotNullString(xForwardedFor, HeaderEnum.X_FORWARDED_FOR.getCode());
	}
	
	protected String checkCf(String shibIdentitaCodiceFiscale, HttpServletRequest httpRequest) {
		String shib = shibIdentitaCodiceFiscale;
		if (StringUtils.isBlank(shib)) {
			shib = Util.getIrideToken(httpRequest, devmode);
		}
		
		checkNotNullString(shib, HeaderEnum.SHIB_IDENTITA_CODICEFISCALE.getCode());
		return shib;
	}
	
	protected ProfiloEnum checkProfilo(String profilo) throws RESTException {
		checkNotNullString(profilo, PathQueryParamEnum.PROFILO.getCode());
		return checkValidProfilo(profilo);
	}
	
	protected ProfiloEnum checkProfiloVerifica(String profilo) throws RESTException, ProfiloNonAmmessoException {
		checkNotNullString(profilo, PathQueryParamEnum.PROFILO.getCode());
		return checkValidProfiloVerifica(profilo);
	}

	protected ProfiloEnum checkValidProfilo(String profilo) throws RESTException {
		ProfiloEnum profiloEnum = ProfiloEnum.fromString(profilo);
		checkNotNull(profiloEnum, PathQueryParamEnum.PROFILO.getCode());
		return profiloEnum;
	}
	
	protected ProfiloEnum checkValidProfiloVerifica(String profilo) throws RESTException, ProfiloNonAmmessoException {
		ProfiloEnum profiloEnum = ProfiloEnum.fromString(profilo);
		checkNotNull(profiloEnum, PathQueryParamEnum.PROFILO.getCode());
		if(!profiloEnum.equals(ProfiloEnum.AUTOCONTROLLO_2) && 
				!profiloEnum.equals(ProfiloEnum.AUTOCONTROLLO_3)) {
			throw new ProfiloNonAmmessoException();
		}
			
		return profiloEnum;
	}
	
	protected String getLivelloFromProfilo(ProfiloEnum profiloEnum) {
		if(profiloEnum.equals(ProfiloEnum.AUTOCONTROLLO_2)) {
			return VerificaLivelloEnum.VERIFICA_LIVELLO_2.getCode();
		} else if(profiloEnum.equals(ProfiloEnum.AUTOCONTROLLO_3)) {
			return VerificaLivelloEnum.VERIFICA_LIVELLO_3.getCode();
		}
		
		return Strings.EMPTY;
	}
	
	protected void checkAziendaSanitaria(String profilo, String aziendaSanitaria) throws RESTException {
		if(profilo.equalsIgnoreCase(ProfiloEnum.RUA.getCode()) || profilo.equalsIgnoreCase(ProfiloEnum.RUP.getCode())) {
			checkNotNullString(aziendaSanitaria, PathQueryParamEnum.AZIENDA_SANITARIA.getCode());
		}
	}
	
	protected void checkAziendaSanitariaProgetto(Boolean isAziendaSanitaria, String profilo) throws AziendaSanitariaNonAmmessaException {
		if(profilo.equalsIgnoreCase(ProfiloEnum.RUA.getCode()) || profilo.equalsIgnoreCase(ProfiloEnum.RUP.getCode())) {
			if(!isAziendaSanitaria) {
				isNotAziendaSanitariaProgetto(isAziendaSanitaria, PathQueryParamEnum.AZIENDA_SANITARIA.getCode());
			}
		}
	}

	protected void checkEsito(String esito, String stato) throws EsitoNotFoundException, EsitoNonApplicabilePerAvanzamentoException {
		if(stato.equalsIgnoreCase(StatoRilevazioneEnum.COMPILATA.getCode())) {
			checkNotNullEsito(esito, BodyRispostaEnum.ESITO.getCode());
		} else if(stato.equalsIgnoreCase(StatoRilevazioneEnum.COMPLETATA.getCode())) {
			checkNonApplicabilePerAvanzamentoEsito(esito, BodyRispostaEnum.ESITO.getCode());
		}
	}
	
	protected void checkNoteAsr(String esito, String stato, String noteAsr) {
		if(!stato.equalsIgnoreCase(StatoRilevazioneEnum.DA_COMPILARE.getCode())
				 && !stato.equalsIgnoreCase(StatoRilevazioneEnum.BOZZA.getCode())) {
			if(esito != null 
					&& (esito.equalsIgnoreCase(RispostaEnum.NO.getCode()) 
					|| esito.equalsIgnoreCase(RispostaEnum.NON_APPLICABILE.getCode()))) {
				checkNotNullString(noteAsr, BodyRispostaEnum.NOTE_ASR.getCode());
			}
		}
	}
	
	protected void checkNoteRp(Boolean esito, String noteRp) {
		if(esito) {
			checkNotNullString(noteRp, BodyRispostaEnum.NOTE_RP.getCode());
		}
	}
	
	protected void checkCondition(boolean isOk, RESTException re) {
		if (!isOk) {
			throw re;
		}
	}
	
	protected void checkConditionEsito(boolean isOk, EsitoNotFoundException e) throws EsitoNotFoundException {
		if (!isOk) {
			throw e;
		}
	}
	
	protected void checkConditionEsitoNonApplicabile(boolean isOk, EsitoNonApplicabilePerAvanzamentoException e) throws EsitoNonApplicabilePerAvanzamentoException {
		if (!isOk) {
			throw e;
		}
	}
	
	protected void checkAziendaSanitariaNonAmmessa(boolean isOk, AziendaSanitariaNonAmmessaException e) throws AziendaSanitariaNonAmmessaException {
		if (!isOk) {
			throw e;
		}
	}
	
	protected void checkNotNull(Object obj, String message) {
		checkNotNull(obj, buildRESTException(message));
	}
	
	protected int getTotalCount(List<Progetto> progetti) {
		int totalCount = 0;
		
		if(progetti != null && progetti.size() != 0 && progetti.get(0) != null) {
			totalCount = progetti.get(0).getTotalCount().intValue();
		}
		
		return totalCount;
	}
	
	private RESTException buildRESTException(String message) {
		return new RESTException(String.format(CodeErrorEnum.ERR03.getMessage(),message));
	}
	
	protected void checkNotNullEsito(String esito, String message) throws EsitoNotFoundException {
		checkNotNullEsito(esito, buildEsitoNotFoundException(message));
	}

	private EsitoNotFoundException buildEsitoNotFoundException(String message) {
		return new EsitoNotFoundException(String.format(CodeErrorEnum.ERR09.getMessage(),message));
	}
	
	private AziendaSanitariaNonAmmessaException buildAziendaSanitariaNonAmmessaException(String message) {
		return new AziendaSanitariaNonAmmessaException(String.format(CodeErrorEnum.ERR09.getMessage(),message));
	}
	
	protected void checkNotNullEsito(Object obj, EsitoNotFoundException re) throws EsitoNotFoundException {
		checkConditionEsito(obj != null, re);
	}
	
	protected void checkNonApplicabilePerAvanzamentoEsito(String esito, String message) throws EsitoNonApplicabilePerAvanzamentoException {
		checkNonApplicabilePerAvanzamentoEsito(esito, buildEsitoNonApplicabilePerAvanzamentoException(message));
	}

	private EsitoNonApplicabilePerAvanzamentoException buildEsitoNonApplicabilePerAvanzamentoException(String message) {
		return new EsitoNonApplicabilePerAvanzamentoException(String.format(CodeErrorEnum.ERR10.getMessage(),message));
	}
	
	protected void checkNonApplicabilePerAvanzamentoEsito(Object obj, EsitoNonApplicabilePerAvanzamentoException re) throws EsitoNonApplicabilePerAvanzamentoException {
		checkConditionEsitoNonApplicabile(obj != null, re);
	}
	
	protected void isNotAziendaSanitariaProgetto(boolean esito, String message) throws AziendaSanitariaNonAmmessaException {
		checkAziendaSanitariaNonAmmessa(esito, buildAziendaSanitariaNonAmmessaException(message));
	}
	
	protected void checkNotNull(Object obj, RESTException re) {
		checkCondition(obj != null, re);
	}
	
	protected void checkNotNullString(String string, String message) {
		checkNotNullString(string, buildRESTException(message));
	}
	
	protected void checkNotNullString(String string, RESTException re) {
		checkCondition(StringUtils.isNotEmpty(string), re);
	}

	protected void checkCondition(boolean isOk, String message) {
		checkCondition(isOk, buildRESTException(message));
	}
	
	protected void checkList(List<?> list, String message) {
		checkList(list, buildRESTException(message));
	}
	
	protected void checkListCig(List<?> list, String stato, String message) {
		if(stato.equalsIgnoreCase(StatoRilevazioneEnum.COMPILATA.getCode())
				 || stato.equalsIgnoreCase(StatoRilevazioneEnum.COMPLETATA.getCode())) {
			checkList(list, buildRESTException(message));
		}
	}
	
	protected void checkList(List<?> obj, RESTException re) {
		checkCondition(obj != null, re);
		checkCondition(obj.size()!= 0, re);
	}
	
	public void checkBodyASR(ChecklistRispostaList body) throws DatabaseException, EsitoNotFoundException, EsitoNonApplicabilePerAvanzamentoException {
		checkNotNull(body, BodyRispostaEnum.CHECKLIST_RISPOSTE.getCode());
		checkNotNullString(body.getStato(), BodyRispostaEnum.STATO.getCode());
		checkList(body.getCup(), BodyRispostaEnum.CUP.getCode()); 
		checkListCig(body.getCig(), body.getStato(), BodyRispostaEnum.CIG.getCode());
		
		body.getCup().forEach(p -> 
			p.setEsito(Util.cleanString(p.getEsito())));
		body.getCup().forEach(p -> 
			p.setNoteAsr(Util.cleanString(p.getNoteAsr())));
		body.getCig().forEach(p -> 
			p.getRisposte().forEach(r ->
				r.setEsito(Util.cleanString(r.getEsito()))));
		body.getCig().forEach(p -> 
			p.getRisposte().forEach(r ->
				r.setNoteAsr(Util.cleanString(r.getNoteAsr()))));
		
		for(ChecklistRisposta risposta : body.getCup()) {
			checkNotNull(risposta.getRilevazioneDomandaId(), BodyRispostaEnum.RILEVAZIONE_DOMANDA_ID.getCode());
			checkEsito(risposta.getEsito(), body.getStato());
			checkNoteAsr(risposta.getEsito(), body.getStato(), risposta.getNoteAsr());
		}
		
		for(ProceduraRisposteCig rispostaCig : body.getCig()) {
			checkCig(rispostaCig, body.getStato());
			checkList(rispostaCig.getRisposte(), BodyRispostaEnum.RISPOSTE.getCode()); 
			for(ChecklistRisposta risposta : rispostaCig.getRisposte()) {
				checkNotNull(risposta.getRilevazioneDomandaId(), BodyRispostaEnum.RILEVAZIONE_DOMANDA_ID.getCode());
				checkEsito(risposta.getEsito(), body.getStato());
				checkNoteAsr(risposta.getEsito(), body.getStato(), risposta.getNoteAsr());
			}
		}
	}
	
	public void checkBodyRP(VerificaRispostaList body) throws DatabaseException {
		checkNotNull(body, BodyRispostaEnum.CHECKLIST_RISPOSTE_VERIFICHE.getCode());
		checkList(body.getCup(), BodyRispostaEnum.CUP.getCode()); 
		
		body.getCup().forEach(p -> 
			p.setNoteRp(Util.cleanString(p.getNoteRp())));
		body.getCig().forEach(p -> 
			p.getRisposte().forEach(r ->
				r.setNoteRp(Util.cleanString(r.getNoteRp()))));
	
		for(VerificaRisposta risposta : body.getCup()) {
			checkNotNull(risposta.getRilevazioneDomandaId(), BodyRispostaEnum.RILEVAZIONE_DOMANDA_ID.getCode());
			if(risposta.isEsito() != null) {
				checkNoteRp(risposta.isEsito(), risposta.getNoteRp());
			}
		}
		
		for(VerificaRispostaCig rispostaCig : body.getCig()) {
			checkList(rispostaCig.getRisposte(), BodyRispostaEnum.RISPOSTE.getCode());
			for(VerificaRisposta risposta : rispostaCig.getRisposte()) {
				checkNotNull(risposta.getRilevazioneDomandaId(), BodyRispostaEnum.RILEVAZIONE_DOMANDA_ID.getCode());
				if(risposta.isEsito() != null) {
					checkNoteRp(risposta.isEsito(), risposta.getNoteRp());
				}
			}
		}
	}
	
	private void checkCig(ProceduraRisposteCig rispostaCig, String stato) {
		if(stato.equalsIgnoreCase(StatoRilevazioneEnum.COMPILATA.getCode()) 
				|| stato.equalsIgnoreCase(StatoRilevazioneEnum.COMPLETATA.getCode())) {
			checkNotNull(rispostaCig.getDescrizioneProceduraCig(), BodyRispostaEnum.DESCRIZIONE_PROCEDURA_CIG.getCode());
			checkNotNull(rispostaCig.getNumeroCig(), BodyRispostaEnum.NUMERO_CIG.getCode());
			checkNotNull(rispostaCig.getProgressivo(), BodyRispostaEnum.PROGRESSIVO.getCode());
		}
	}
	
	public void checkStatus(String stato) throws PdfNonDisponibileException {
		checkNotNullString(stato, BodyRispostaEnum.STATO.getCode());
		if(stato.equalsIgnoreCase(StatoRilevazioneEnum.DA_COMPILARE.getCode())) {
			throw new PdfNonDisponibileException();
		}
	}
	
	public void checkCambioStato(String statoAttuale, String statoFuturo, List<CambiStatoDto> cambiStatoDto) throws CambioStatoNonPossibileException {
		String metodo = new Object() {}.getClass().getEnclosingMethod().getName();
		logInfo(metodo, String.format("Cambio stato da %s a %s", statoAttuale, statoFuturo));
		
		Optional<CambiStatoDto> cambioStato = cambiStatoDto.stream()
				.filter(s -> s.statoFinaleCod() != null && 
					s.statoFinaleCod().equalsIgnoreCase(statoFuturo) && 
					s.statoInizialeCod() != null &&
					s.statoInizialeCod().equalsIgnoreCase(statoAttuale))
				.findFirst();
		
		if(cambioStato.isEmpty()) {
			throw new CambioStatoNonPossibileException();
		}
	}
	
	/** Exceptions **/
	
	protected Errore handleDatabaseException(String metodo, DatabaseException e) {
		String messageError = "Errore riguardante database: ";
		return handleError(metodo, CodeErrorEnum.ERR01, HttpStatus.INTERNAL_SERVER_ERROR, e, messageError);
	}
		
	protected Errore handleException(String metodo, Exception e) {
		String messageError = "Errore generico: ";
		return handleError(metodo, CodeErrorEnum.ERR01, HttpStatus.INTERNAL_SERVER_ERROR, e, messageError);
	}
	
	protected Errore handleRESTException(String metodo, RESTException e) {
		return handleError(metodo,CodeErrorEnum.ERR03,HttpStatus.BAD_REQUEST,e,e.getMessage());
	}
	
	protected Errore handleCambioStatoNonPossibileException(String metodo, CambioStatoNonPossibileException e) {
		return handleError(metodo,CodeErrorEnum.ERR04,HttpStatus.BAD_REQUEST,e,e.getMessage());
	}
	
	protected Errore handleEsitoNotFoundException(String metodo, EsitoNotFoundException e) {
		return handleError(metodo,CodeErrorEnum.ERR04,HttpStatus.BAD_REQUEST,e,e.getMessage());
	}
	
	protected Errore handleEsitoNonApplicabilePerAvanzamentoException(String metodo, EsitoNonApplicabilePerAvanzamentoException e) {
		return handleError(metodo,CodeErrorEnum.ERR04,HttpStatus.BAD_REQUEST,e,e.getMessage());
	}
	
	protected Errore handlePdfNonDisponibileException(String metodo, PdfNonDisponibileException e) {
		return handleError(metodo,CodeErrorEnum.ERR05,HttpStatus.BAD_REQUEST,e,e.getMessage());
	}
	
	protected Errore handlePdfException(String metodo, PdfException e) {
		return handleError(metodo,CodeErrorEnum.ERR06,HttpStatus.INTERNAL_SERVER_ERROR,e,e.getMessage());
	}
	
	protected Errore handleCsvException(String metodo, CsvException e) {
		return handleError(metodo,CodeErrorEnum.ERR11,HttpStatus.INTERNAL_SERVER_ERROR,e,e.getMessage());
	}
	
	protected Errore handleAziendaSanitariaNonAmmessaException(String metodo, AziendaSanitariaNonAmmessaException e) {
		return handleError(metodo,CodeErrorEnum.ERR13,HttpStatus.BAD_REQUEST,e,e.getMessage());
	}
	
	protected Errore handleConfiguratoreIPException(String metodo, ConfiguratoreIPException e) {
		String messageError = "Errore riguardante configuratore: ";
		return handleError(metodo, CodeErrorEnum.ERR07, HttpStatus.BAD_REQUEST, e, messageError);
	}
	
	protected Errore handleConfiguratoreGeneralException(String metodo, ConfiguratoreGeneralException e) {
		String messageError = "Errore riguardante configuratore: ";
		return handleError(metodo, CodeErrorEnum.ERR08, HttpStatus.BAD_REQUEST, e, messageError);
	}
	
	protected Errore handleProfiloNonAmmessoException(String metodo, ProfiloNonAmmessoException e) {
		return handleError(metodo,CodeErrorEnum.ERR12,HttpStatus.BAD_REQUEST,e,e.getMessage());
	}

	private Dettaglio getCodeMessage(CodeErrorEnum code, String errorMessage) {
		Dettaglio dettaglio = new Dettaglio();
		dettaglio.setChiave(code.getCode());
		dettaglio.setValore(code.isExceptionMessageVisible()? code.getMessage():errorMessage);
		return dettaglio;
	}
	
	private Errore handleError(String metodo, CodeErrorEnum codErrore, HttpStatus status, Exception e,
			String messageError) {
		List<Dettaglio> listerrorservice = new ArrayList<Dettaglio>();
		logError(metodo, messageError, e);
		listerrorservice.add(getCodeMessage(codErrore, e.getMessage()));
		return buildErrore(status, listerrorservice	);
	}

	public Response generateResponseError(Errore errore) {
		return Response.status(errore.getStatus() != null ? errore.getStatus() : HttpStatus.INTERNAL_SERVER_ERROR.value())
				.entity(errore).type(MediaType.APPLICATION_JSON).build();
	}
	
	private Errore buildErrore(HttpStatus status, List<Dettaglio> listerrorservice) {
		Errore errore = new Errore();
		errore.setStatus(status.value());
		errore.setCode(status.getReasonPhrase());
		errore.setTitle(status.getReasonPhrase());
		errore.setDetail(listerrorservice);
		return errore;
	}
}
