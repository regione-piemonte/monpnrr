/*
* SPDX-FileCopyrightText: (C) Copyright 2025 Regione Piemonte
*
* SPDX-License-Identifier: EUPL-1.2 
*/
package it.csi.monpnrr.monpnrrbe.util;

import java.math.BigDecimal;

import org.apache.logging.log4j.util.Strings;

public class Constants {
	public static final String IRIDE_ID_REQ_ATTR = "iride2_id";
	public static final String AUTH_ID_MARKER = "Shib-Iride-IdentitaDigitale";
	public static final String AUTH_ID_MARKER2 = "shib-iride-identitadigitale";
	public static final String UTENTE_REQ_ATTR = "utente";
	
	public static final String FORMATTER_DATE = "yyyy-MM-dd";
	public static final String FORMATTER_USA = "yyyy-MM-dd HH:mm:ss";
	public static final String FORMATTER_ITA = "dd-MM-yyyy HH:mm:ss";
	public static final String FORMATTER_FILE = "ddMMyyyy_HHmmss";
	
	public static final Integer DATA_PARAMETRO_DEFAULT = 7;
	
	public static final String NON_INDICATO = "*** non indicato ***";
	public static final String AUTOCONTROLLATO = "Autocontrollato";
	
	public static final String PDF_FILE_NAME = "progetto_";
	public static final String PDF_EXTENSION = ".pdf";
	public static final String CSV_FILE_NAME = "elenco_progetti_";
	public static final String CSV_EXTENSION = ".csv";
	
	public final static float[] PDF_RELATIVE_WIDTHS_ASR = {0.4f,1.2f,1.3f,2f,2f,0.6f,2f};
	public final static float[] PDF_RELATIVE_WIDTHS_RP_LIV2 = {0.4f,0.8f,0.8f,1.5f,1.5f,0.6f,1f,0.7f,0.7f,0.6f,1f};
	public final static float[] PDF_RELATIVE_WIDTHS_RP_LIV3 = {0.4f,0.8f,0.8f,1.2f,1.2f,0.6f,1f,0.5f,0.5f,0.5f,1f,0.5f,0.5f,0.5f,1f};
	public final static float[] PDF_RELATIVE_WIDTHS_RP_SUPERUSER = {0.4f,0.7f,0.7f,1f,1f,0.6f,1f,0.5f,0.5f,0.5f,0.5f,0.5f,1f,0.5f,0.5f,0.5f,1f};
	
	public final static int PDF_WIDTH_TABLE = 100;
	public final static int PDF_LEADING_SPACE = 10;
	
	public static final String N_SEZIONE_HEADER = "N. SEZ.";
	public static final String SEZIONE_HEADER = "SEZIONE";
	public static final String SOTTOSEZIONE_HEADER = "SOTTOSEZ.";
	public static final String DOCUMENTAZIONE_DA_CARICARE_PDF_HEADER = "DOCUMENTAZIONE\r\nda caricare";
	public static final String DOCUMENTAZIONE_DA_CARICARE_CSV_HEADER = "DOCUMENTAZIONE DA CARICARE";
	public static final String NOTE_HEADER = "NOTE";
	public static final String ESITO_HEADER = "ESITO";
	public static final String NOTA_ASR_HEADER = "NOTA ASR";
	public static final String AUTOCONTROLLO_LIVELLO_2_HEADER = "CONTR.";
	public static final String NOTA_AUTOCONTROLLO_LIVELLO_2_HEADER = "NOTA CONTR.";
	public static final String DATA_AUTOCONTROLLO_LIVELLO_2_HEADER = "DATA CONTR.";
	public static final String UTENTE_AUTOCONTROLLO_LIVELLO_2_HEADER = "UTENTE CONTR.";
	public static final String AUTOCONTROLLO_LIVELLO_3_HEADER = "CONTR. CART.";
	public static final String NOTA_AUTOCONTROLLO_LIVELLO_3_HEADER = "NOTA CONTR. CART.";
	public static final String DATA_AUTOCONTROLLO_LIVELLO_3_HEADER = "DATA CONTR. CART.";
	public static final String UTENTE_AUTOCONTROLLO_LIVELLO_3_HEADER = "UTENTE CONTR. CART.";
	
	public static final String CUP_HEADER = "CUP";
	public static final String DESCRIZIONE_HEADER = "DESCRIZIONE";
	public static final String AZIENDA_SANITARIA_HEADER = "AZIENDA SANITARIA";
	public static final String RUA_HEADER = "RUA";
	public static final String RUP_HEADER = "RUP";
	public static final String CHECKLIST_HEADER = "CHECKLIST";
	public static final String STATO_CHECKLIST_HEADER = "STATO CHECKLIST";
	public static final String DATA_ULTIMA_MODIFICA_HEADER = "DATA ULTIMA MODIFICA";
	public static final String UTENTE_ULTIMA_MODIFICA_HEADER = "UTENTE ULTIMA MODIFICA";
	public static final String DATA_ULTIMO_AUTOCONTROLLO_HEADER = "DATA ULTIMO AUTOCONTROLLO";
	public static final String UTENTE_ULTIMO_AUTOCONTROLLO_HEADER = "UTENTE ULTIMO AUTOCONTROLLO";
	public static final String MODIFICA_RECENTE_HEADER = "MODIFICA RECENTE";
	
	public static final String CSV_SEPARATOR = ";";
	public static final String CSV_RUA_SEPARATOR = ",";
	public static final String SPACE = " ";
	
	public static final String CSV_HEADER_PROGETTI_RP = CUP_HEADER + CSV_SEPARATOR + DESCRIZIONE_HEADER + CSV_SEPARATOR + AZIENDA_SANITARIA_HEADER + CSV_SEPARATOR 
			+ RUP_HEADER + CSV_SEPARATOR + RUA_HEADER + CSV_SEPARATOR + CHECKLIST_HEADER + CSV_SEPARATOR + STATO_CHECKLIST_HEADER + CSV_SEPARATOR 
			+ DATA_ULTIMA_MODIFICA_HEADER + CSV_SEPARATOR + UTENTE_ULTIMA_MODIFICA_HEADER + CSV_SEPARATOR + DATA_ULTIMO_AUTOCONTROLLO_HEADER + CSV_SEPARATOR 
			+ UTENTE_ULTIMO_AUTOCONTROLLO_HEADER + CSV_SEPARATOR + MODIFICA_RECENTE_HEADER + Strings.LINE_SEPARATOR;
	public static final String CSV_HEADER_CHECKLIST_RP_LIV2 = N_SEZIONE_HEADER + CSV_SEPARATOR + SEZIONE_HEADER + CSV_SEPARATOR + SOTTOSEZIONE_HEADER + CSV_SEPARATOR 
			+ DOCUMENTAZIONE_DA_CARICARE_CSV_HEADER + CSV_SEPARATOR + ESITO_HEADER + CSV_SEPARATOR + NOTA_ASR_HEADER + CSV_SEPARATOR
			+ UTENTE_ULTIMA_MODIFICA_HEADER + CSV_SEPARATOR + DATA_ULTIMA_MODIFICA_HEADER + CSV_SEPARATOR
			+ AUTOCONTROLLO_LIVELLO_2_HEADER + CSV_SEPARATOR + NOTA_AUTOCONTROLLO_LIVELLO_2_HEADER + Strings.LINE_SEPARATOR;
	public static final String CSV_HEADER_CHECKLIST_RP_LIV3 = N_SEZIONE_HEADER + CSV_SEPARATOR + SEZIONE_HEADER + CSV_SEPARATOR + SOTTOSEZIONE_HEADER + CSV_SEPARATOR 
			+ DOCUMENTAZIONE_DA_CARICARE_CSV_HEADER + CSV_SEPARATOR + ESITO_HEADER + CSV_SEPARATOR + NOTA_ASR_HEADER + CSV_SEPARATOR
			+ UTENTE_ULTIMA_MODIFICA_HEADER + CSV_SEPARATOR + DATA_ULTIMA_MODIFICA_HEADER + CSV_SEPARATOR
			+ AUTOCONTROLLO_LIVELLO_2_HEADER + CSV_SEPARATOR + NOTA_AUTOCONTROLLO_LIVELLO_2_HEADER + CSV_SEPARATOR
			+ UTENTE_ULTIMO_AUTOCONTROLLO_HEADER + CSV_SEPARATOR + DATA_ULTIMO_AUTOCONTROLLO_HEADER + CSV_SEPARATOR
			+ AUTOCONTROLLO_LIVELLO_3_HEADER + CSV_SEPARATOR + NOTA_AUTOCONTROLLO_LIVELLO_3_HEADER + Strings.LINE_SEPARATOR; 
	public static final String CSV_HEADER_CHECKLIST_RP_SUPERUSER = N_SEZIONE_HEADER + CSV_SEPARATOR + SEZIONE_HEADER + CSV_SEPARATOR + SOTTOSEZIONE_HEADER + CSV_SEPARATOR 
			+ DOCUMENTAZIONE_DA_CARICARE_CSV_HEADER + CSV_SEPARATOR + ESITO_HEADER + CSV_SEPARATOR + NOTA_ASR_HEADER + CSV_SEPARATOR
			+ UTENTE_ULTIMA_MODIFICA_HEADER + CSV_SEPARATOR + DATA_ULTIMA_MODIFICA_HEADER + CSV_SEPARATOR
			+ AUTOCONTROLLO_LIVELLO_2_HEADER + CSV_SEPARATOR + NOTA_AUTOCONTROLLO_LIVELLO_2_HEADER + CSV_SEPARATOR
			+ UTENTE_ULTIMO_AUTOCONTROLLO_HEADER + CSV_SEPARATOR + DATA_ULTIMO_AUTOCONTROLLO_HEADER + CSV_SEPARATOR
			+ AUTOCONTROLLO_LIVELLO_3_HEADER + CSV_SEPARATOR + NOTA_AUTOCONTROLLO_LIVELLO_3_HEADER + CSV_SEPARATOR
			+ UTENTE_ULTIMO_AUTOCONTROLLO_HEADER + CSV_SEPARATOR + DATA_ULTIMO_AUTOCONTROLLO_HEADER + Strings.LINE_SEPARATOR; 
	
	public static final String CSV_HEADER_PROGETTI_ASR = CUP_HEADER + CSV_SEPARATOR + DESCRIZIONE_HEADER + CSV_SEPARATOR + AZIENDA_SANITARIA_HEADER + CSV_SEPARATOR 
			+ RUP_HEADER + CSV_SEPARATOR + RUA_HEADER + CSV_SEPARATOR + CHECKLIST_HEADER + CSV_SEPARATOR + STATO_CHECKLIST_HEADER + CSV_SEPARATOR 
			+ DATA_ULTIMA_MODIFICA_HEADER + CSV_SEPARATOR + UTENTE_ULTIMA_MODIFICA_HEADER + Strings.LINE_SEPARATOR;
	public static final String CSV_HEADER_CHECKLIST_ASR = N_SEZIONE_HEADER + CSV_SEPARATOR + SEZIONE_HEADER + CSV_SEPARATOR + SOTTOSEZIONE_HEADER + CSV_SEPARATOR 
			+ DOCUMENTAZIONE_DA_CARICARE_CSV_HEADER + CSV_SEPARATOR + ESITO_HEADER + CSV_SEPARATOR + NOTA_ASR_HEADER + Strings.LINE_SEPARATOR; 
	
	public static final String DOCUMENTAZIONE_CIG = "Documentazione CIG";
	public static final String DOCUMENTAZIONE_CUP = "Documentazione CUP";
	
	public static final String CUP = "CUP: ";
	public static final String STATO = "Stato: ";
	public static final String RUP = "RUP: ";
	public static final String INTERVENTO = "Intervento: ";
	public static final String PDF_CHECKLIST = "Checklist ";
	public static final String CSV_CHECKLIST = "Checklist: ";
	public static final String AZIENDA_SANITARIA = "Azienda Sanitaria: ";
	public static final String MODALITA_ATTUATIVA = "Modalità attuativa: ";
	public static final String DATA_ULTIMA_MODIFICA = "Data ultima modifica: ";
	public static final String UTENTE_ULTIMA_MODIFICA = "Utente ultima modifica: ";
	public static final String DATA_ULTIMO_AUTOCONTROLLO = "Data ultimo autocontrollo: ";
	public static final String UTENTE_ULTIMO_AUTOCONTROLLO = "Utente ultimo autocontrollo: ";
	public static final String DATA_ULTIMO_AUTOCONTROLLO_LIV2 = "Data ultimo autocontrollo livello 2: ";
	public static final String UTENTE_ULTIMO_AUTOCONTROLLO_LIV2 = "Utente ultimo autocontrollo livello 2: ";
	public static final String DATA_ULTIMO_AUTOCONTROLLO_LIV3 = "Data ultimo autocontrollo livello 3: ";
	public static final String UTENTE_ULTIMO_AUTOCONTROLLO_LIV3 = "Utente ultimo autocontrollo livello 3: ";
	
	public static final String CODICE_CIG = "Codice CIG ";
	public static final String PROCEDURA = " - Procedura ";
	
	public static final String AZIENDA_QUERY_CODE = "azienda";
	public static final String CUP_QUERY_CODE = "cup";
	public static final String RUA_QUERY_CODE = "rua";
	public static final String RUP_QUERY_CODE = "rup";
	public static final String CHECKLIST_QUERY_CODE = "checklist";
	public static final String STATO_QUERY_CODE = "stato";
	public static final String DATA_MODIFICA_DA_QUERY_CODE = "dataModificaDa";
	public static final String DATA_MODIFICA_A_QUERY_CODE = "dataModificaA";
	public static final String DATA_VERIFICA_DA_QUERY_CODE = "dataVerificaDa";
	public static final String DATA_VERIFICA_A_QUERY_CODE = "dataVerificaA";
	public static final String DATA_VERIFICA2_DA_QUERY_CODE = "dataVerifica2Da";
	public static final String DATA_VERIFICA2_A_QUERY_CODE = "dataVerifica2A";
	public static final String DATA_VERIFICA3_DA_QUERY_CODE = "dataVerifica3Da";
	public static final String DATA_VERIFICA3_A_QUERY_CODE = "dataVerifica3A";
	public static final String DATA_MODIFICA_RUP_DOPO_VERIFICA_DA_QUERY_CODE = "dataModificaRupDopoVerificaDa";
	public static final String DATA_MODIFICA_RUP_DOPO_VERIFICA_A_QUERY_CODE = "dataModificaRupDopoVerificaA";
	
	public static final String LIV2 = "liv2";
	public static final String LIV3 = "liv3";
	
	public static final String NULL_STRING = "null";
	
	public static final BigDecimal DEFAULT_MIN = new BigDecimal("0.0");
	public static final BigDecimal DEFAULT_MAX = new BigDecimal("1000.0");
	public static final BigDecimal DEFAULT_PAGE_NUMBER_VALUE = new BigDecimal("1.0");
	public static final BigDecimal DEFAULT_ROW_PAGE_VALUE = new BigDecimal("5.0");
}
