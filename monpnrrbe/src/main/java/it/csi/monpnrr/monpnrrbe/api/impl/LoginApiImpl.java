/*
* SPDX-FileCopyrightText: (C) Copyright 2025 Regione Piemonte
*
* SPDX-License-Identifier: EUPL-1.2 
*/
package it.csi.monpnrr.monpnrrbe.api.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import it.csi.monpnrr.monpnrrbe.api.LoginApi;
import it.csi.monpnrr.monpnrrbe.api.dto.Azione;
import it.csi.monpnrr.monpnrrbe.api.dto.CollocazioneAzienda;
import it.csi.monpnrr.monpnrrbe.api.dto.Errore;
import it.csi.monpnrr.monpnrrbe.api.dto.Persona;
import it.csi.monpnrr.monpnrrbe.api.dto.Profilo;
import it.csi.monpnrr.monpnrrbe.api.dto.Utente;
import it.csi.monpnrr.monpnrrbe.api.impl.base.RESTBaseService;
import it.csi.monpnrr.monpnrrbe.exception.ConfiguratoreGeneralException;
import it.csi.monpnrr.monpnrrbe.exception.ConfiguratoreIPException;
import it.csi.monpnrr.monpnrrbe.exception.DatabaseException;
import it.csi.monpnrr.monpnrrbe.exception.RESTException;
import it.csi.monpnrr.monpnrrbe.integration.configuratore.ConfiguratoreService;
import it.csi.monpnrr.monpnrrbe.integration.configuratore.dto.Collocazione;
import it.csi.monpnrr.monpnrrbe.integration.configuratore.dto.Funzionalita;
import it.csi.monpnrr.monpnrrbe.integration.configuratore.dto.Richiedente;
import it.csi.monpnrr.monpnrrbe.integration.configuratore.dto.TokenInformazione;
import it.csi.monpnrr.monpnrrbe.util.enumeration.ApiMethodEnum;
import it.csi.monpnrr.monpnrrbe.util.log.HttpHeaderParam;
import it.csi.monpnrr.monpnrrbe.util.log.LogSupport;
import it.csi.monpnrr.monpnrrbe.util.log.LogSupportBuilder;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;

@Service
public class LoginApiImpl extends RESTBaseService implements LoginApi {

	@Autowired
	ConfiguratoreService configuratoreService;
	
	@Override
	public Response login(String shibIdentitaCodiceFiscale, String xRequestId, String xCodiceServizio,
			String xForwardedFor, @NotNull String token, SecurityContext securityContext, HttpHeaders httpHeaders,
			HttpServletRequest httpRequest) {
		String metodo = new Object() {}.getClass().getEnclosingMethod().getName();
		Errore error = new Errore();
		LogSupport ls = new LogSupport();
		
		try {
			shibIdentitaCodiceFiscale = checkHeadersForLogin(shibIdentitaCodiceFiscale, xRequestId, xForwardedFor, xCodiceServizio, httpRequest);
			HttpHeaderParam headerParam = new HttpHeaderParam(shibIdentitaCodiceFiscale, xRequestId, xForwardedFor, xCodiceServizio, token);
			ls = LogSupportBuilder.init(metodo, headerParam, httpRequest.getRequestURI(), ApiMethodEnum.GET)
					.queryParam("Token: " + token)
					.isLoggableParam(true)
					.build();
			logAudit(ls);
			
			checkToken(token);
			Utente result = getInfoProfiloUtente(shibIdentitaCodiceFiscale, xRequestId,
					xForwardedFor, token, xCodiceServizio, httpRequest);
			ls.setResponse(result);
			insertLogAuditSuccess(ls);
			return Response.ok(result).build();

		} catch (ConfiguratoreIPException e) {
			error = handleConfiguratoreIPException(metodo, e);
		} catch (ConfiguratoreGeneralException e) {
			error = handleConfiguratoreGeneralException(metodo, e);
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
	
	public Utente getInfoProfiloUtente(String shibIdentitaCodiceFiscale, String xRequestId,
			String xForwardedFor, String token, String xCodiceServizio, HttpServletRequest httpRequest) throws Exception {
		String metodo = new Object() {}.getClass().getEnclosingMethod().getName();
		logInfo(metodo, "token:" + token);
		Utente result = new Utente();

		TokenInformazione configuratoreResponse = configuratoreService.sendGetTokenInformation(shibIdentitaCodiceFiscale, 
				xRequestId,	xForwardedFor, token, xCodiceServizio, httpRequest);
		if(configuratoreResponse!=null) {
			Persona richiedente = buildRichiedente(
					configuratoreResponse.getRichiedente());
			result.setRichiedente(richiedente);
			result.setProfili(buildProfili(configuratoreResponse.getFunzionalita()));
		}
		
		return result;
	}
	
	private Persona buildRichiedente(Richiedente richiedente) {
		Persona persona = new Persona();
		persona.setCodiceFiscale(richiedente.getCodiceFiscale());
		persona.setCognome(richiedente.getCognome());
		persona.setNome(richiedente.getNome());
		persona.setRuolo(richiedente.getRuolo());
		persona.setCollocazione(buildCollocazione(richiedente.getCollocazione()));
		
		return persona;
	}
	
	private CollocazioneAzienda buildCollocazione(Collocazione collocazione) {
		CollocazioneAzienda collocazioneAzienda = new CollocazioneAzienda();
		collocazioneAzienda.setCodiceAzienda(collocazione.getCodiceAzienda());
		collocazioneAzienda.setDescrizioneAzienda(collocazione.getDescrizioneAzienda());
		collocazioneAzienda.setCodiceCollocazione(collocazione.getCodiceCollocazione());
		collocazioneAzienda.setDescrizioneCollocazione(collocazione.getDescrizioneCollocazione());
		
		return collocazioneAzienda;
	}
	
	private List<Profilo> buildProfili(List<Funzionalita> listaFunzionalita) {
		List<Profilo> profili = new ArrayList<Profilo>();
		//funzionalità che diventano profili
		
		List<Funzionalita> funzionalitàProfili = listaFunzionalita.stream()
				.filter(p -> p.getCodiceFunzionalitaPadre() == null)
				.toList();
		
		for(Funzionalita funzionalitaProfilo : funzionalitàProfili) {
			profili.add(buildProfilo(funzionalitaProfilo.getCodice(), funzionalitaProfilo.getDescrizione()));
		}
		
		List<Funzionalita> funzionalitàAzioni = listaFunzionalita.stream()
				.filter(p -> p.getCodiceFunzionalitaPadre() != null)
				.toList();
		
		for(Funzionalita funzionalita : funzionalitàAzioni) {
			profili.stream()
		       .filter(profilo -> profilo.getCodice().equals(funzionalita.getCodiceFunzionalitaPadre()))
		       .findFirst()
		       .ifPresent(profilo -> profilo.getAzioni().add(buildAzione(funzionalita.getCodice(), funzionalita.getDescrizione())));
		}
		
		return profili;
	}
	
	private Profilo buildProfilo(String codice, String descrizione) {
		Profilo profilo = new Profilo();
		profilo.setCodice(codice);
		profilo.setDescrizione(descrizione);
		profilo.setAzioni(new ArrayList<Azione>());
		
		return profilo;
	}

	private Azione buildAzione(String codice, String descrizione) {
		Azione azione = new Azione();
		azione.setCodice(codice);
		azione.setDescrizione(descrizione);
		return azione;
	}
	
}