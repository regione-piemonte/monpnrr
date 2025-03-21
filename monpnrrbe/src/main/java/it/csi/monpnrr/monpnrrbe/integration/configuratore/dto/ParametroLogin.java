/*
* SPDX-FileCopyrightText: (C) Copyright 2025 Regione Piemonte
*
* SPDX-License-Identifier: EUPL-1.2 
*/
package it.csi.monpnrr.monpnrrbe.integration.configuratore.dto;

import java.io.Serializable;

public class ParametroLogin implements Serializable {

	private static final long serialVersionUID = 1799280703576307803L;

	private String codice;

	private String valore;

	public String getCodice() {
		return codice;
	}

	public void setCodice(String codice) {
		this.codice = codice;
	}

	public String getValore() {
		return valore;
	}

	public void setValore(String valore) {
		this.valore = valore;
	}

	@Override
	public String toString() {
		return "ParametroLogin [codice=" + codice + ", valore=" + valore + "]";
	}

}
