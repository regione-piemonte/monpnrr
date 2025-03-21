/*
* SPDX-FileCopyrightText: (C) Copyright 2025 Regione Piemonte
*
* SPDX-License-Identifier: EUPL-1.2 
*/
package it.csi.monpnrr.monpnrrbe.integration.configuratore.dto;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Funzionalita implements Serializable {

	private static final long serialVersionUID = 1655652009459601374L;

	@JsonProperty("codice")
	private String codice;

	@JsonProperty("descrizione")
	private String descrizione;

	@JsonProperty("codice_funzionalita_padre")
	private String codiceFunzionalitaPadre;

	@JsonProperty("descrizione_funzionalita_padre")
	private String descrizioneFunzionalitaPadre;

	public String getCodice() {
		return codice;
	}

	public void setCodice(String codice) {
		this.codice = codice;
	}

	public String getDescrizione() {
		return descrizione;
	}

	public void setDescrizione(String descrizione) {
		this.descrizione = descrizione;
	}

	public String getCodiceFunzionalitaPadre() {
		return codiceFunzionalitaPadre;
	}

	public void setCodiceFunzionalitaPadre(String codiceFunzionalitaPadre) {
		this.codiceFunzionalitaPadre = codiceFunzionalitaPadre;
	}

	public String getDescrizioneFunzionalitaPadre() {
		return descrizioneFunzionalitaPadre;
	}

	public void setDescrizioneFunzionalitaPadre(String descrizioneFunzionalitaPadre) {
		this.descrizioneFunzionalitaPadre = descrizioneFunzionalitaPadre;
	}

	@Override
	public String toString() {
		return "Funzionalita [codice=" + codice + ", descrizione=" + descrizione + ", codiceFunzionalitaPadre="
				+ codiceFunzionalitaPadre + ", descrizioneFunzionalitaPadre=" + descrizioneFunzionalitaPadre + "]";
	}

}
