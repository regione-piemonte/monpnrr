/*
* SPDX-FileCopyrightText: (C) Copyright 2025 Regione Piemonte
*
* SPDX-License-Identifier: EUPL-1.2 
*/
package it.csi.monpnrr.monpnrrbe.integration.dao.dto;

public record CambiStatoDto (
		String statoInizialeCod,
		String statoInizialeDesc,
		Integer statoInizialeOrdinamento,
		String statoFinaleCod,
		String statoFinaleDesc,
		Integer statoFinaleOrdinamento) {}
