/*
* SPDX-FileCopyrightText: (C) Copyright 2025 Regione Piemonte
*
* SPDX-License-Identifier: EUPL-1.2 
*/
package it.csi.monpnrr.monpnrrbe.util.enumeration;

public enum BodyRispostaEnum {
	CHECKLIST_RISPOSTE("CHECKLIST_RISPOSTE"),
	STATO("STATO"),
	CUP("CUP"),
	CIG("CIG"),
	DESCRIZIONE_PROCEDURA_CIG("DESCRIZIONE_PROCEDURA_CIG"),
	NUMERO_CIG("NUMERO_CIG"),
	PROGRESSIVO("PROGRESSIVO"),
	RISPOSTE("RISPOSTE"),
	RILEVAZIONE_DOMANDA_ID("RILEVAZIONE_DOMANDA_ID"),
	ESITO("ESITO"),
	NOTE_ASR("NOTE_ASR"),
	CHECKLIST_RISPOSTE_VERIFICHE("CHECKLIST_RISPOSTE_VERIFICHE"),
	NOTE_RP("NOTE_RP");
	
	private final String code;
	
	private BodyRispostaEnum(String code) {
		this.code = code;
	}

	public String getCode() {
		return code;
	}
}
