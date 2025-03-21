/*
* SPDX-FileCopyrightText: (C) Copyright 2025 Regione Piemonte
*
* SPDX-License-Identifier: EUPL-1.2 
*/
package it.csi.monpnrr.monpnrrbe.integration.dao.dto;

/**
 * data_ora now() id_app x-codice-servizo ip_address X-Forwarded-For; utente
 * operazione VERBO PUT GET POST DELETE ogg_oper Payload dell'operazione or
 * query param key_oper METODO CHIAMATO uuid x request id request_payload
 * cifrato response_payload cifrato esito_chiamata int
 */

public class CsiLogAudit {

	private String idApp;
	private String ipAddress;
	private String utente;
	private String operazione;
	private String oggOper;
	private String keyOper;
	private String uuid;
	private String requestPayload;
	private String responsePayload;
	private int esitoChiamata;
	
	public String getIdApp() {
		return idApp;
	}
	public void setIdApp(String idApp) {
		this.idApp = idApp;
	}
	public String getIpAddress() {
		return ipAddress;
	}
	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}
	public String getUtente() {
		return utente;
	}
	public void setUtente(String utente) {
		this.utente = utente;
	}
	public String getOperazione() {
		return operazione;
	}
	public void setOperazione(String operazione) {
		this.operazione = operazione;
	}
	public String getOggOper() {
		return oggOper;
	}
	public void setOggOper(String oggOper) {
		this.oggOper = oggOper;
	}
	public String getKeyOper() {
		return keyOper;
	}
	public void setKeyOper(String keyOper) {
		this.keyOper = keyOper;
	}
	public String getUuid() {
		return uuid;
	}
	public void setUuid(String uuid) {
		this.uuid = uuid;
	}
	public String getRequestPayload() {
		return requestPayload;
	}
	public void setRequestPayload(String requestPayload) {
		this.requestPayload = requestPayload;
	}
	public String getResponsePayload() {
		return responsePayload;
	}
	public void setResponsePayload(String responsePayload) {
		this.responsePayload = responsePayload;
	}
	public int getEsitoChiamata() {
		return esitoChiamata;
	}
	public void setEsitoChiamata(int esitoChiamata) {
		this.esitoChiamata = esitoChiamata;
	}
	public CsiLogAudit() {
		super();
	}
	@Override
	public String toString() {
		return "CsiLogAudit [idApp=" + idApp + ", ipAddress=" + ipAddress + ", utente=" + utente + ", operazione="
				+ operazione + ", oggOper=" + oggOper + ", keyOper=" + keyOper + ", uuid=" + uuid + ", requestPayload="
				+ requestPayload + ", responsePayload=" + responsePayload + ", esitoChiamata=" + esitoChiamata + "]";
	}
	
	
	
}
