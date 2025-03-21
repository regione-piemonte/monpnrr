/*
* SPDX-FileCopyrightText: (C) Copyright 2025 Regione Piemonte
*
* SPDX-License-Identifier: EUPL-1.2 
*/
package it.csi.monpnrr.monpnrrbe.integration.configuratore;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

import it.csi.monpnrr.monpnrrbe.api.impl.base.RESTBaseService;
import it.csi.monpnrr.monpnrrbe.exception.ConfiguratoreGeneralException;
import it.csi.monpnrr.monpnrrbe.exception.ConfiguratoreIPException;
import it.csi.monpnrr.monpnrrbe.integration.configuratore.dto.ErroreConfiguratore;
import it.csi.monpnrr.monpnrrbe.integration.configuratore.dto.TokenInformazione;
import it.csi.monpnrr.monpnrrbe.util.enumeration.ApiMethodEnum;
import it.csi.monpnrr.monpnrrbe.util.enumeration.HeaderEnum;
import it.csi.monpnrr.monpnrrbe.util.log.HttpHeaderParam;
import it.csi.monpnrr.monpnrrbe.util.log.LogSupport;
import it.csi.monpnrr.monpnrrbe.util.log.LogSupportBuilder;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.ws.rs.core.Response.Status;

@Service
public class ConfiguratoreService extends RESTBaseService {

	@Value("${configuratore.base.url}")
	String configuratoreBaseUrl;
	
	@Value("${configuratore.auth.base64}")
	String configuratoreAuthBase64;

	private static final String LOGIN_TOKEN_INFORMATION2 = "login/token-information2";
	private static final String BASIC_AUTH = "Basic ";
	private static final String ERRORE_IP_NON_CONGRUENTE ="AUTH_ER_602";

	private final HttpClient httpClient = HttpClient.newBuilder().version(HttpClient.Version.HTTP_2).build();

	public TokenInformazione sendGetTokenInformation(String shibIdentitaCodiceFiscale, String xRequestId,
			String xForwardedFor, String token, String xCodiceServizio, HttpServletRequest httpRequest) throws Exception {
		String metodo = new Object() {}.getClass().getEnclosingMethod().getName();
		
		TokenInformazione result = null;
		String xForwadedForInHeader = extractXForwadedFor(xForwardedFor);
		String url = getUrl(LOGIN_TOKEN_INFORMATION2);
		
		HttpHeaderParam headerParam = new HttpHeaderParam(shibIdentitaCodiceFiscale, xRequestId, xForwardedFor, xCodiceServizio);
		LogSupport ls = LogSupportBuilder
				.init(metodo,headerParam, httpRequest.getRequestURI(), ApiMethodEnum.GET)
				.isLoggableParam(true)
				.build();
		logAudit(ls);
		
		logInfo(ls.getMethodName(), "[HeaderParam - Token] value : " + token);
		logInfo(ls.getMethodName(), "[HeaderParam - AUTHORIZATION] value : " + getBasicAuth());
		
		HttpRequest request = HttpRequest.newBuilder().GET().uri(URI.create(url))
				.setHeader(HeaderEnum.SHIB_IDENTITA_CODICEFISCALE.getCode(), shibIdentitaCodiceFiscale)
				.setHeader(HeaderEnum.X_REQUEST_ID.getCode(), xRequestId)
				.setHeader(HeaderEnum.X_FORWARDED_FOR.getCode(), xForwadedForInHeader)
				.setHeader(HeaderEnum.X_CODICE_SERVIZIO.getCode(), xCodiceServizio)
				.setHeader(HeaderEnum.TOKEN.getCode(), token)
				.setHeader(HeaderEnum.AUTHORIZATION.getCode(), getBasicAuth()).build();

		HttpResponse<String> response = null;
		
		try {
			response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
			logInfo(metodo, "configuratore statusCode: " + response.statusCode());
			logInfo(metodo, "configuratore responseBody: " + response.body());
			
			ObjectMapper mapper = new ObjectMapper();
			if(response.statusCode() == Status.OK.getStatusCode()) {
				result = mapper.readValue(response.body(), TokenInformazione.class);
			} else {
				ErroreConfiguratore errore = mapper.readValue(response.body(),ErroreConfiguratore.class);
				if(ERRORE_IP_NON_CONGRUENTE.equals(errore.getCodice())) {
					throw new ConfiguratoreIPException(errore.getDescrizione(), errore.getStatus(), errore.getCodice());
				} else {
					throw new ConfiguratoreGeneralException(errore.getDescrizione(), errore.getStatus(), errore.getCodice());
				}
			}
		} catch(ConfiguratoreIPException | ConfiguratoreGeneralException uEx) {
			throw uEx;
		} catch (InterruptedException e) {
			logError("sendGetTokenInformationException", e.getMessage(),e );
			Thread.currentThread().interrupt();
			throw new Exception(e);
		} catch (Exception e) {
			logError("sendGetTokenInformationException", e.getMessage(),e );
			throw new Exception(e);
		}
		
		return result;
	}

	private String getBasicAuth() {
		StringBuffer sb = new StringBuffer();
		sb.append(BASIC_AUTH).append(configuratoreAuthBase64);
		return sb.toString();
	}

	private String extractXForwadedFor(String xForwadedFor) {
		String result = "";
		if (xForwadedFor.contains(",")) {
			result = xForwadedFor.split(",")[0].trim();
		} else {
			result = xForwadedFor;
		}
		return result;
	}

	private String getUrl(String loginTokenInformation) {
		StringBuffer sb = new StringBuffer();
		sb.append(configuratoreBaseUrl).append(loginTokenInformation);
		return sb.toString();
	}
	
}
