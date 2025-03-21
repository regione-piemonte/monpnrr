/*
* SPDX-FileCopyrightText: (C) Copyright 2025 Regione Piemonte
*
* SPDX-License-Identifier: EUPL-1.2 
*/
package it.csi.monpnrr.monpnrrbe.util.enumeration;

public enum CodeErrorEnum {
	ERR01("ERR01", "Si è verificato un errore interno del server",false),
	ERR02("ERR02", "Si è verificato un errore negli header della chiamata",false),
	ERR03("ERR03", "Parametro %s errato o mancante",false),
	ERR04("ERR04", "Cambio Stato della checklist non permesso",true),
	ERR05("ERR05", "Pdf non disponibile nello stato Da Compilare",true),
	ERR06("ERR06", "Errore nella creazione del pdf",true),
	ERR07("ERR07", "Ip Address non congruente con quello fornito al momento della generazione del token", true),
	ERR08("ERR08", "%s", false),
	ERR09("ERR09", "Esito deve essere valorizzato in caso di domanda in stato " + StatoRilevazioneEnum.COMPILATA.getCode(), true),
	ERR10("ERR10", "Esito non può essere valorizzato con " + RispostaEnum.NON_APPLICABILE_PER_AVANZAMENTO.getCode() + " in caso di domanda in stato Completata" + StatoRilevazioneEnum.COMPLETATA.getCode(), true),
	ERR11("ERR11", "Errore nella creazione del csv",true),
	ERR12("ERR12", "Profilo non ammesso", true),
	ERR13("ERR13", "Azienda Sanitaria non ammessa", true);
	
	private final String code;
	private final String message;
	private final boolean exceptionMessageVisible;

	CodeErrorEnum(String code, String message,boolean visiblein) {
		this.code = code;
		this.message = message;
		this.exceptionMessageVisible=visiblein;
	}

	public String getCode() {
		return code;
	}

	public String getMessage() {
		return message;
	}

	public boolean isExceptionMessageVisible() {
		return exceptionMessageVisible;
	}

	
}
