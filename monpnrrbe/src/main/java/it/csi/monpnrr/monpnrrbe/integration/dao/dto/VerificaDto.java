/*
* SPDX-FileCopyrightText: (C) Copyright 2025 Regione Piemonte
*
* SPDX-License-Identifier: EUPL-1.2 
*/
package it.csi.monpnrr.monpnrrbe.integration.dao.dto;

public record VerificaDto (
		Integer rilevazioneDomandaId,
		Integer verificaId,
		Integer valoriVerificaId,
		String nota,
		Integer cigId) {}

