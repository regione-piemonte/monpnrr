/*
* SPDX-FileCopyrightText: (C) Copyright 2025 Regione Piemonte
*
* SPDX-License-Identifier: EUPL-1.2 
*/
package it.csi.monpnrr.monpnrrbe.integration.configuratore.dto;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TokenInformazione implements Serializable {

	private static final long serialVersionUID = -6159438653261259760L;

	@JsonProperty("richiedente")
	private Richiedente richiedente;

	@JsonProperty("parametri_login")
	private List<ParametroLogin> parametriLogin;

	@JsonProperty("funzionalita")
	private List<Funzionalita> funzionalita;

	public Richiedente getRichiedente() {
		return richiedente;
	}

	public void setRichiedente(Richiedente richiedente) {
		this.richiedente = richiedente;
	}

	public List<ParametroLogin> getParametriLogin() {
		return parametriLogin;
	}

	public void setParametriLogin(List<ParametroLogin> parametriLogin) {
		this.parametriLogin = parametriLogin;
	}

	public List<Funzionalita> getFunzionalita() {
		return funzionalita;
	}

	public void setFunzionalita(List<Funzionalita> funzionalita) {
		this.funzionalita = funzionalita;
	}

	@Override
	public String toString() {
		return "TokenInformazione [richiedente=" + richiedente + ", parametriLogin=" + parametriLogin
				+ ", funzionalita=" + funzionalita + ", getClass()=" + getClass() + ", hashCode()=" + hashCode()
				+ ", toString()=" + super.toString() + "]";
	}

}
