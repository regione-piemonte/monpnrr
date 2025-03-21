/*
* SPDX-FileCopyrightText: (C) Copyright 2025 Regione Piemonte
*
* SPDX-License-Identifier: EUPL-1.2 
*/
package it.csi.monpnrr.monpnrrbe.integration.dao.dto;

import java.math.BigDecimal;

import it.csi.monpnrr.monpnrrbe.util.Constants;
import it.csi.monpnrr.monpnrrbe.util.Util;

public record FiltersDto(
	String cup, 
	String checklist,
	String stato, 
	String modificaRupDa, 
	String modificaRupA, 
	String aziendaSanitaria, 
	String rua, 
	String rup,
	Boolean verificatoLiv2,
	String modificaVerifica2Da, 
	String modificaVerifica2A, 
	Boolean verificatoLiv3,
	String modificaVerifica3Da,
	String modificaVerifica3A, 
	Boolean modificatoRupDopoVerifica,
	String modificaRupDopoVerificaDa, 
	String modificaRupDopoVerificaA,
	String orderBy, 
	Boolean descending, 
	BigDecimal pageNumber, 
	BigDecimal rowPerPage) {
	
    public FiltersDto {
    	cup = convertToNullIfStringNull(Util.cleanString(cup));
    	checklist = convertToNullIfStringNull(Util.cleanString(checklist));
		stato = convertToNullIfStringNull(Util.cleanString(stato));
		modificaRupDa = convertToNullIfStringNull(Util.cleanString(modificaRupDa)); 
		modificaRupA = convertToNullIfStringNull(Util.cleanString(modificaRupA)); 
		aziendaSanitaria = convertToNullIfStringNull(Util.cleanString(aziendaSanitaria)); 
		rua = convertToNullIfStringNull(Util.cleanString(rua)); 
		rup = convertToNullIfStringNull(Util.cleanString(rup));
		modificaVerifica2Da = convertToNullIfStringNull(Util.cleanString(modificaVerifica2Da)); 
		modificaVerifica2A = convertToNullIfStringNull(Util.cleanString(modificaVerifica2A)); 
		modificaVerifica3Da = convertToNullIfStringNull(Util.cleanString(modificaVerifica3Da));
		modificaVerifica3A = convertToNullIfStringNull(Util.cleanString(modificaVerifica3A)); 
		modificaRupDopoVerificaDa = convertToNullIfStringNull(Util.cleanString(modificaRupDopoVerificaDa)); 
		modificaRupDopoVerificaA = convertToNullIfStringNull(Util.cleanString(modificaRupDopoVerificaA));
		orderBy = convertToNullIfStringNull(Util.cleanString(orderBy));
		pageNumber = pageNumber != null ? Util.checkPageNumber(pageNumber) : pageNumber;
		rowPerPage = rowPerPage != null ? Util.checkRowPerPage(rowPerPage) : rowPerPage;
    }
	
    public static String convertToNullIfStringNull(String input) {
        if (Constants.NULL_STRING.equalsIgnoreCase(input)) {
            return null; 
        }
        return input;
    }
}
