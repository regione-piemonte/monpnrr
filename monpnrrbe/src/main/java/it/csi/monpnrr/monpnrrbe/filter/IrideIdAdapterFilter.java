/*
* SPDX-FileCopyrightText: (C) Copyright 2025 Regione Piemonte
*
* SPDX-License-Identifier: EUPL-1.2 
*/
package it.csi.monpnrr.monpnrrbe.filter;

import java.io.IOException;
import java.io.PrintWriter;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import it.csi.iride2.policy.entity.Identita;
import it.csi.iride2.policy.exceptions.MalformedIdTokenException;
import it.csi.monpnrr.monpnrrbe.api.impl.base.AuditableApiServiceImpl;
import it.csi.monpnrr.monpnrrbe.util.Constants;
import it.csi.monpnrr.monpnrrbe.util.Util;
import it.csi.monpnrr.monpnrrbe.util.record.ErrorRecord;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
@Order(1)
public class IrideIdAdapterFilter extends AuditableApiServiceImpl implements Filter {

	@Value("${IrideIdAdapterFilter.devmode:false}")
	private boolean devmode;

	@PostConstruct
	public void init() {
		String metodo = new Object() {}.getClass().getEnclosingMethod().getName();
		logDebug(metodo, " **** DEV-MODE " + devmode);
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain)
			throws IOException, ServletException {

		String metodo = new Object() {}.getClass().getEnclosingMethod().getName();
		
		if (!(request instanceof HttpServletRequest)) {
			filterChain.doFilter(request, response);
			return;
		}
		
		HttpServletRequest httpRequest = (HttpServletRequest) request;
		HttpServletResponse httpResponse = (HttpServletResponse) response;

		String token = Util.getIrideToken(httpRequest, devmode);
		logDebug(metodo, "token: " + token + " for url: " + httpRequest.getRequestURL());

		if (token == null) {
			// il marcatore deve sempre essere presente altrimenti e' una
			// condizione di errore (escluse le pagine home e di servizio)
			if (mustCheckPage(httpRequest.getRequestURI())) {
				// LOG.error("[IrideIdAdapterFilter::doFilter] Tentativo di accesso a pagina non
				// home e non di servizio senza token di sicurezza");

				httpResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
				PrintWriter writer = httpResponse.getWriter();
				writer.append(new ErrorRecord("Tentativo di accesso a pagina non home e non di servizio senza token di sicurezza").toString());

				return;

				// throw new ServletException(
				// "Tentativo di accesso a pagina non home e non di servizio senza token di
				// sicurezza");
			}
		}
		
		Identita identita;
		try {
			identita = new Identita(normalizeToken(token));
		} catch (MalformedIdTokenException e) {
			logError(metodo, "Token iride non valido: " + token + " - " + e);
			respWithError(httpResponse);
			return;
		}

		httpRequest.setAttribute(Constants.IRIDE_ID_REQ_ATTR, identita);

		filterChain.doFilter(httpRequest, httpResponse);
	}

	private static void respWithError(HttpServletResponse httpResponse) throws IOException {
		httpResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
		httpResponse.setContentType("text/json");
		PrintWriter writer = httpResponse.getWriter();
		writer.append(new ErrorRecord("Token di sicurezza non valido").toString());
	}

	private boolean mustCheckPage(String requestURI) {
		// return requestURI.startsWith("/") &&
		// !requestURI.startsWith("/lgspaweb/api/test");
		return true;
	}

	@Override
	public void destroy() {
		String metodo = new Object() {}.getClass().getEnclosingMethod().getName();
		logDebug(metodo, "destroy");
	}

	private String normalizeToken(String token) {
		return token;
	}
}
