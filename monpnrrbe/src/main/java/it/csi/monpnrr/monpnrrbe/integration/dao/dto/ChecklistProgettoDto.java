/*
* SPDX-FileCopyrightText: (C) Copyright 2025 Regione Piemonte
*
* SPDX-License-Identifier: EUPL-1.2 
*/
package it.csi.monpnrr.monpnrrbe.integration.dao.dto;

public record ChecklistProgettoDto (
		String macroSezioneCod,
		Integer cigId,
		String numeroSezione,
		String sezioneCod,
		String sottoSezioneCod,
		Integer rilevazioneDomandaId,
		String documentazioneDaCaricareCod,
		String note,
		String rispostaCod,
		String notaAsr,
		String utenteModificaRup,
		String dataModificaRup,
		String verificaCodLiv2,
		String noteVerificaLiv2,
		String livelloVerificaLiv2,
		String utenteVerificaLiv2,
		String dataVerificaLiv2,
		Boolean isVerificatoLiv2,
		String verificaCodLiv3,
		String noteVerificaLiv3,
		String livelloVerificaLiv3,
		String utenteVerificaLiv3,
		String dataVerificaLiv3,
		Boolean isVerificatoLiv3) {}
