/*
* SPDX-FileCopyrightText: (C) Copyright 2025 Regione Piemonte
*
* SPDX-License-Identifier: EUPL-1.2 
*/
package it.csi.monpnrr.monpnrrbe.api.impl.base;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.micrometer.common.util.StringUtils;
import it.csi.monpnrr.monpnrrbe.exception.DatabaseException;
import it.csi.monpnrr.monpnrrbe.integration.dao.CSILogAuditDao;
import it.csi.monpnrr.monpnrrbe.integration.dao.dto.CsiLogAudit;
import it.csi.monpnrr.monpnrrbe.util.log.AbstractLogger;
import it.csi.monpnrr.monpnrrbe.util.log.LogSupport;

@Service
public abstract class AuditableApiServiceImpl extends AbstractLogger {

	@Autowired
	private CSILogAuditDao csiLogAuditDao;

	protected void logAudit(LogSupport ls) {
		logInfo(ls.getMethodName(), "[Api - url]  " + ls.getApiEnum().getCode() + " value : " + ls.getApiUrl());
		
		logInfo(ls.getMethodName(), "[HeaderParam - Shib-Identita-CodiceFiscale / Utente] value : "
				+ ls.getHttpHeaderParam().shibIdentitaCodiceFiscale());
		logInfo(ls.getMethodName(), "[HeaderParam - X-Forwarded-For] value : " + ls.getHttpHeaderParam().xForwardedFor());
		logInfo(ls.getMethodName(), "[HeaderParam - X-Request-Id] value : " + ls.getHttpHeaderParam().xRequestId());
		logInfo(ls.getMethodName(), "[HeaderParam - X-Codice-Servizio] value : " + ls.getHttpHeaderParam().xCodiceServizio());
		
		if (StringUtils.isNotBlank(ls.getQueryParam()) && ls.isLoggableParam()) {
			logInfo(ls.getMethodName(), "[QueryParam] value : " + ls.getQueryParam());
		}
	}

	protected void insertLogAuditSuccess(LogSupport ls) {
		try {
			CsiLogAudit audit = buildCsiLogAudit(ls, 200);
			csiLogAuditDao.saveAudit(audit);
		} catch (DatabaseException e) {
			logError("insertLogAuditSuccess", e.getMessage(), e);
		} catch (Exception e) {
			logError("insertLogAuditSuccess", e.getMessage(), e);
		}
	}

	protected void insertLogAuditError(LogSupport ls, Integer status) {
		try {
			CsiLogAudit audit = buildCsiLogAudit(ls, status);
			// da eliminare in un prodotto con autenticazione 
			if(StringUtils.isBlank(audit.getUtente())) {
				audit.setUtente("CF_UTENTE_NON_INSERITO");
			}
			csiLogAuditDao.saveAudit(audit);
		} catch (DatabaseException e) {
			logError("insertLogAuditError", e.getMessage(), e);
		} catch (Exception e) {
			logError("insertLogAuditError", e.getMessage(), e);
		}
	}
//	data_ora now()
//	id_app x-codice-servizo
//	ip_address @HeaderParam("X-Forwarded-For") String xForwardedFor,
//	utente
//	operazione VERBO PUT GET POST DELETE
//	ogg_oper Payload dell'operazione
//	key_oper METODO CHIAMATO
//	uuid  x request id
//	request_payload cifrato
//	response_payload cifrato
//	esito_chiamata int

	private CsiLogAudit buildCsiLogAudit(LogSupport ls, Integer status) {
		CsiLogAudit audit = new CsiLogAudit();
		audit.setIdApp(ls.getHttpHeaderParam().xCodiceServizio());
		audit.setIpAddress(ls.getHttpHeaderParam().xForwardedFor());
		audit.setUtente(ls.getHttpHeaderParam().shibIdentitaCodiceFiscale());
		audit.setOperazione(ls.getApiEnum().getCode());
		if (ls.getRequest() != null) {
			audit.setOggOper(ls.getRequest().getClass().getCanonicalName());
		} else {
			audit.setOggOper("-");
		}
		audit.setKeyOper(ls.getApiUrl());
		audit.setUuid(ls.getHttpHeaderParam().xRequestId());
		audit.setRequestPayload(buildPayload(ls.getRequest(), ls.getQueryParam()));
		audit.setResponsePayload(buildPayload(ls.getResponse(), null));
		audit.setEsitoChiamata(status);
		return audit;
	}

	private String buildPayload(Object payload, String queryParam) {
		StringBuffer sb = new StringBuffer();
		if (StringUtils.isNotBlank(queryParam)) {
			sb.append("QParam: ").append(queryParam).append(" ");
		}
		if (payload != null) {
			sb.append("Payload -").append(payload.getClass()).append(" ").append(payload.toString()).append(" ");
		}
		String result= sb.toString();
		return StringUtils.isNotBlank(result)?result:null;
	}

}