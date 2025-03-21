/*
* SPDX-FileCopyrightText: (C) Copyright 2025 Regione Piemonte
*
* SPDX-License-Identifier: EUPL-1.2 
*/
package it.csi.monpnrr.monpnrrbe.integration.configuratore.dto;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Collocazione implements Serializable {

	private static final long serialVersionUID = -4002197226586202490L;
	
	@JsonProperty("codice_collocazione")
	private String codiceCollocazione;
	
	@JsonProperty("descrizione_collocazione")
	private String descrizioneCollocazione;
	
	@JsonProperty("codice_azienda")
	private String codiceAzienda;
	
	@JsonProperty("descrizione_azienda")
	private String descrizioneAzienda;

	public String getCodiceCollocazione() {
		return codiceCollocazione;
	}

	public void setCodiceCollocazione(String codiceCollocazione) {
		this.codiceCollocazione = codiceCollocazione;
	}

	public String getDescrizioneCollocazione() {
		return descrizioneCollocazione;
	}

	public void setDescrizioneCollocazione(String descrizioneCollocazione) {
		this.descrizioneCollocazione = descrizioneCollocazione;
	}

	public String getCodiceAzienda() {
		return codiceAzienda;
	}

	public void setCodiceAzienda(String codiceAzienda) {
		this.codiceAzienda = codiceAzienda;
	}

	public String getDescrizioneAzienda() {
		return descrizioneAzienda;
	}

	public void setDescrizioneAzienda(String descrizioneAzienda) {
		this.descrizioneAzienda = descrizioneAzienda;
	}

	@Override
	public String toString() {
		return "Collocazione [codiceCollocazione=" + codiceCollocazione + ", descrizioneCollocazione="
				+ descrizioneCollocazione + ", codiceAzienda=" + codiceAzienda + ", descrizioneAzienda="
				+ descrizioneAzienda + "]";
	}

}
