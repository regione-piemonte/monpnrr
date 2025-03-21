/*
* SPDX-FileCopyrightText: (C) Copyright 2025 Regione Piemonte
*
* SPDX-License-Identifier: EUPL-1.2 
*/
package it.csi.monpnrr.monpnrrbe.configuration;

import java.io.IOException;
import java.util.Arrays;
import java.util.function.Supplier;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;
import org.springframework.security.web.csrf.CsrfTokenRequestHandler;
import org.springframework.util.StringUtils;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.ws.rs.HttpMethod;

@Configuration
public class SpringSecurityConfig {

	@Value("${AbstractHttpConfigurer.enableCsrf:true}")
	private boolean enableCsrf;
	@Value("${corsfilter.enablecors:true}")
	private boolean enableCors;

	@Bean
	protected SecurityFilterChain configureHttpBasic(HttpSecurity http) throws Exception {
		if (enableCsrf) {
			CookieCsrfTokenRepository repository = CookieCsrfTokenRepository.withHttpOnlyFalse();
			repository.setCookiePath("/");
			repository.setCookieCustomizer(cookie -> 
					cookie.secure(true));
	
			http.csrf(
					csrf -> csrf.csrfTokenRepository(repository).csrfTokenRequestHandler(new SpaCsrfTokenRequestHandler()))
					.addFilterAfter(new CsrfCookieFilter(), BasicAuthenticationFilter.class);
		} else {
			http.csrf(AbstractHttpConfigurer::disable);
		}
		
		http.cors(cors -> cors.configurationSource(request -> {
			CorsConfiguration configuration = new CorsConfiguration();
			if (enableCors) {
				configuration.setAllowedOrigins(Arrays.asList("http://localhost:4200",
						"https://tst-monpnrr.salutepiemonte.it", "https://monpnrr.salutepiemonte.it"));
				configuration.setAllowedMethods(Arrays.asList(HttpMethod.GET, HttpMethod.POST, HttpMethod.DELETE,
						HttpMethod.PUT, HttpMethod.OPTIONS));
				configuration.setAllowedHeaders(Arrays.asList("shib-identita-codicefiscale", "x-forwarded-for",
						"x-codice-servizio", "x-request-id", "content-type", "x-xsrf-token"));
				configuration.setAllowCredentials(true);
			}
			return configuration;
		}));

		return http.build();
	}

	final class SpaCsrfTokenRequestHandler extends CsrfTokenRequestAttributeHandler {
//		private final CsrfTokenRequestHandler delegate = new XorCsrfTokenRequestAttributeHandler();
		private final CsrfTokenRequestHandler delegate = new CsrfTokenRequestAttributeHandler();

		@Override
		public void handle(HttpServletRequest request, HttpServletResponse response, Supplier<CsrfToken> csrfToken) {
			/*
			 * Always use XorCsrfTokenRequestAttributeHandler to provide BREACH protection
			 * of the CsrfToken when it is rendered in the response body.
			 */
			this.delegate.handle(request, response, csrfToken);
		}

		@Override
		public String resolveCsrfTokenValue(HttpServletRequest request, CsrfToken csrfToken) {
			/*
			 * If the request contains a request header, use
			 * CsrfTokenRequestAttributeHandler to resolve the CsrfToken. This applies when
			 * a single-page application includes the header value automatically, which was
			 * obtained via a cookie containing the raw CsrfToken.
			 */
			if (StringUtils.hasText(request.getHeader(csrfToken.getHeaderName()))) {
				return super.resolveCsrfTokenValue(request, csrfToken);
			}
			/*
			 * In all other cases (e.g. if the request contains a request parameter), use
			 * XorCsrfTokenRequestAttributeHandler to resolve the CsrfToken. This applies
			 * when a server-side rendered form includes the _csrf request parameter as a
			 * hidden input.
			 */
			return this.delegate.resolveCsrfTokenValue(request, csrfToken);
		}
	}

	final class CsrfCookieFilter extends OncePerRequestFilter {

		@Override
		protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
				FilterChain filterChain) throws ServletException, IOException {
			CsrfToken csrfToken = (CsrfToken) request.getAttribute("_csrf");
			// Render the token value to a cookie by causing the deferred token to be loaded
			csrfToken.getToken();

			filterChain.doFilter(request, response);
		}
	}
}
