/*
* SPDX-FileCopyrightText: (C) Copyright 2025 Regione Piemonte
*
* SPDX-License-Identifier: EUPL-1.2 
*/
package it.csi.monpnrr.monpnrrbe.util;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.csi.iride2.policy.entity.Identita;
import jakarta.servlet.http.HttpServletRequest;

public class Util {
	private static final Logger LOG = LoggerFactory.getLogger(Util.class);
	
	public static boolean isValorizzato(String stringa) {
		if (stringa == null || stringa.equalsIgnoreCase("null"))
			return false;

		if (stringa.trim().length() == 0)
			return false;

		return true;
	}
	
	public static Date fromStringToDate(String data, String dateFormatPattern) {
		if (!Util.isValorizzato(data))
			return null;

		try {
			SimpleDateFormat sdf = new SimpleDateFormat(dateFormatPattern);
			Date dataDate = sdf.parse(data);

			return dataDate;
		} catch (ParseException pe) {
			return null;
		}
	}
	
	public static String fromDateToString(Date data, String dateFormatPattern) {
		SimpleDateFormat sdf = new SimpleDateFormat(dateFormatPattern);
		String dataString = sdf.format(data);
		return dataString;
	}
	
	public static String getIrideToken(HttpServletRequest httpRequest, boolean devmode) {
		String marker = (String) httpRequest.getHeader(Constants.AUTH_ID_MARKER);
		if (marker == null) {
			marker = (String) httpRequest.getHeader(Constants.AUTH_ID_MARKER2);
		}
		if (marker == null && devmode) {
			return getIrideTokenDevMode(httpRequest);
		}

		return marker;
	}
	
	private static String getIrideTokenDevMode(HttpServletRequest httpRequest) {
		LOG.info(" **** DEV-MODE ON -- getIrideTokenDevMode");
		String marker = (String) httpRequest.getParameter(Constants.AUTH_ID_MARKER);
		if (marker == null) {
			marker = (String) httpRequest.getParameter(Constants.AUTH_ID_MARKER2);
		}

		// use default
		if (marker == null) {
			Identita i = new Identita();
			i.setCodFiscale("test");
			i.setCognome("Piemonte");
			i.setNome("Demo 21");
			i.setIdProvider("SHIB");
			i.setTimestamp("timestamp");
			i.setLivelloAutenticazione(0);
			i.setMac("mac");
			marker = i.toString();
		}
		return marker;
	}
	
	// Rimuove i caratteri non validi come \u0000
	public static String cleanString(String input) {
        if (input == null) {
            return null;
        }
        
		input = input.replaceAll("\\p{Cntrl}", "");
		input = input.replaceAll("[^A-Za-z0-9 -_]", "");
        return input;
    }
	
    public static BigDecimal checkPageNumber(BigDecimal value) {
        if (value.compareTo(Constants.DEFAULT_MIN) < 0) {
            return Constants.DEFAULT_PAGE_NUMBER_VALUE;
        } else if (value.compareTo(Constants.DEFAULT_MAX) > 0) {
            return Constants.DEFAULT_PAGE_NUMBER_VALUE;
        } else {
            return value;
        }
    }
    
    public static BigDecimal checkRowPerPage(BigDecimal value) {
        if (value.compareTo(Constants.DEFAULT_MIN) < 0) {
            return Constants.DEFAULT_ROW_PAGE_VALUE;
        } else if (value.compareTo(Constants.DEFAULT_MAX) > 0) {
            return Constants.DEFAULT_ROW_PAGE_VALUE;
        } else {
            return value;
        }
    }
}
