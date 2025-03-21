/*
* SPDX-FileCopyrightText: (C) Copyright 2025 Regione Piemonte
*
* SPDX-License-Identifier: EUPL-1.2 
*/
package it.csi.monpnrr.monpnrrbe.integration.csv;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import it.csi.monpnrr.monpnrrbe.api.dto.DataUtente;
import it.csi.monpnrr.monpnrrbe.api.dto.Progetto;
import it.csi.monpnrr.monpnrrbe.api.dto.ProgettoDettaglio;
import it.csi.monpnrr.monpnrrbe.api.dto.UtenteSanitario;
import it.csi.monpnrr.monpnrrbe.api.impl.base.RESTBaseService;
import it.csi.monpnrr.monpnrrbe.exception.CsvException;
import it.csi.monpnrr.monpnrrbe.exception.DatabaseException;
import it.csi.monpnrr.monpnrrbe.integration.dao.ProgettiDao;
import it.csi.monpnrr.monpnrrbe.integration.dao.dto.ChecklistProgettoDto;
import it.csi.monpnrr.monpnrrbe.integration.dao.dto.ProceduraCigDto;
import it.csi.monpnrr.monpnrrbe.util.Constants;
import it.csi.monpnrr.monpnrrbe.util.enumeration.MacrosezioneEnum;
import it.csi.monpnrr.monpnrrbe.util.enumeration.ProfiloEnum;
import it.csi.monpnrr.monpnrrbe.util.enumeration.VerificaLivelloEnum;
import it.csi.monpnrr.monpnrrbe.util.enumeration.VerificaRilevazioneEnum;

@Service
public class CsvService extends RESTBaseService {
	
	@Autowired
	private ProgettiDao progettiDao;
	
	public String createCsvProgetti(List<Progetto> progetti, ProfiloEnum profilo) throws CsvException {
		String metodo = new Object() {}.getClass().getEnclosingMethod().getName();
		
		try {
			boolean isRp = ProfiloEnum.isRp(profilo);
			String livello = getLivelloFromProfilo(profilo);
			
			StringBuilder csvContent = new StringBuilder();
			csvContent.append(getProgetti(progetti, isRp));
			csvContent.append(Strings.LINE_SEPARATOR);
			csvContent.append(getChecklist(progetti, isRp, livello));
			
			return csvContent.toString();
		} catch (Exception e) {
			logError(metodo, e.getMessage());
			throw new CsvException(); 
		}
	}
	
	private String getDate(DataUtente dataUtente) {
		if(dataUtente != null && dataUtente.getData() != null) {
			return dataUtente.getData();
		}
		
		return Strings.EMPTY;
	}
	
	private String getUtente(DataUtente dataUtente) {
		if(dataUtente != null && dataUtente.getUtente() != null) {
			return dataUtente.getUtente();
		}
		
		return Strings.EMPTY;
	}
	
	private String buildUtenteSanitario(List<UtenteSanitario> utenti) {
		String utenteSanitario = Strings.EMPTY;
		for(UtenteSanitario utente : utenti) {
			utenteSanitario += utente.getCognome() + " " + utente.getNome() + ", ";
		}
		
		return utenteSanitario.substring(0, utenteSanitario.length() - ", ".length());
	}
	
	private String getProgetti(List<Progetto> progetti, boolean isRp) throws DatabaseException {
		StringBuilder csvContent = new StringBuilder();
		
		if(isRp) {
			csvContent.append(Constants.CSV_HEADER_PROGETTI_RP);
		} else {
			csvContent.append(Constants.CSV_HEADER_PROGETTI_ASR);
		}
		
		for(Progetto progetto : progetti) {
			String dataUltimaModifica = getDate(progetto.getUltimaModifica());
			String utenteUltimaModifica = getUtente(progetto.getUltimaModifica());
			
			csvContent.append(progetto.getCup()).append(Constants.CSV_SEPARATOR)
				.append(progetto.getDescrizione()).append(Constants.CSV_SEPARATOR)
				.append(progetto.getAzienda().getDescrizione()).append(Constants.CSV_SEPARATOR)
				.append(getUtenteSanitario(progetto.getRup())).append(Constants.CSV_SEPARATOR) 
				.append(getUtenteSanitario(progetto.getRua())).append(Constants.CSV_SEPARATOR)
				.append(progetto.getChecklist().getCodice()).append(Constants.CSV_SEPARATOR)
				.append(progetto.getChecklist().getStato()).append(Constants.CSV_SEPARATOR)
				.append(dataUltimaModifica).append(Constants.CSV_SEPARATOR)
				.append(utenteUltimaModifica);
			
			if(isRp) {
				String dataUltimaVerifica = getDate(progetto.getUltimaVerifica());
				String utenteUltimaVerifica = getUtente(progetto.getUltimaVerifica());
				
				csvContent.append(Constants.CSV_SEPARATOR)
					.append(dataUltimaVerifica).append(Constants.CSV_SEPARATOR)
					.append(utenteUltimaVerifica).append(Constants.CSV_SEPARATOR)
					.append(progetto.isModificaRecente() ? "Sì" : "No");
			}
			
			csvContent.append(Strings.LINE_SEPARATOR);
		}
		
		return csvContent.toString();
	}
	
	private String getChecklist(List<Progetto> progetti, boolean isRp, String livello) throws DatabaseException {
		StringBuilder csvContent = new StringBuilder();
		
		for(Progetto progetto : progetti) {
			ProgettoDettaglio progettoDettaglio = progettiDao.getProgettoDettaglio(progetto.getCup());
			Integer rilevazioneId = progettiDao.getRilevazioneByCup(progetto.getCup());
			List<ProceduraCigDto> procedureCigDto = progettiDao.getProcedureCigDto(rilevazioneId);
			
			csvContent.append(getDettaglio(progettoDettaglio, progetto.getChecklist().getStato(), isRp, livello));
			
			csvContent.append(Strings.LINE_SEPARATOR);
			
			List<ChecklistProgettoDto> checklistDtos = progettiDao.getChecklistProgettoDto(progetto.getCup(), livello);
			List<ChecklistProgettoDto> checklistCup = checklistDtos.stream()
					.filter(c -> c.macroSezioneCod().equals(MacrosezioneEnum.CUP.getCode())).toList();
			csvContent.append(getCup(checklistCup, isRp, livello));
				
			csvContent.append(Strings.LINE_SEPARATOR);
				
			List<ChecklistProgettoDto> checklistCig = checklistDtos.stream()
					.filter(c -> c.macroSezioneCod().equals(MacrosezioneEnum.CIG.getCode())).toList();
			csvContent.append(getCig(checklistCig, procedureCigDto, isRp, livello));
				
			csvContent.append(Strings.LINE_SEPARATOR);
		}
		
		return csvContent.toString();
	}
	
	private String getDettaglio(ProgettoDettaglio progettoDettaglio, String stato, boolean isRp, String livello) {
		StringBuilder csvContent = new StringBuilder();
		
		csvContent.append(Constants.CUP).append(progettoDettaglio.getCup()).append(Strings.LINE_SEPARATOR);
		csvContent.append(Constants.INTERVENTO).append(progettoDettaglio.getIntervento()).append(Strings.LINE_SEPARATOR);
		csvContent.append(Constants.CSV_CHECKLIST).append(progettoDettaglio.getChecklist()).append(Strings.LINE_SEPARATOR);
		csvContent.append(Constants.STATO).append(stato).append(Strings.LINE_SEPARATOR);
		csvContent.append(Constants.RUP).append(buildUtenteSanitario(progettoDettaglio.getRup())).append(Strings.LINE_SEPARATOR);
		csvContent.append(Constants.AZIENDA_SANITARIA).append(progettoDettaglio.getAzienda().getDescrizione()).append(Strings.LINE_SEPARATOR);
		csvContent.append(Constants.MODALITA_ATTUATIVA).append(progettoDettaglio.getModalitaAttuativa()).append(Strings.LINE_SEPARATOR);
		
		if(progettoDettaglio.getUltimaModifica() != null) {
			csvContent.append(Constants.DATA_ULTIMA_MODIFICA).append(progettoDettaglio.getUltimaModifica().getData()).append(Strings.LINE_SEPARATOR);
			csvContent.append(Constants.UTENTE_ULTIMA_MODIFICA).append(progettoDettaglio.getUltimaModifica().getUtente()).append(Strings.LINE_SEPARATOR);
		}
		
		if(isRp) {
			if(VerificaLivelloEnum.VERIFICA_LIVELLO_3.getCode().equalsIgnoreCase(livello)) {
				if(progettoDettaglio.getUltimaVerificaLiv2() != null) {
					String data = progettoDettaglio.getUltimaVerificaLiv2().getData() == null ? Strings.EMPTY : 
						progettoDettaglio.getUltimaVerificaLiv2().getData();
					String utente = progettoDettaglio.getUltimaVerificaLiv2().getUtente() == null ? Strings.EMPTY : 
						progettoDettaglio.getUltimaVerificaLiv2().getUtente();
					csvContent.append(Constants.DATA_ULTIMO_AUTOCONTROLLO).append(data).append(Strings.LINE_SEPARATOR);
					csvContent.append(Constants.UTENTE_ULTIMO_AUTOCONTROLLO).append(utente).append(Strings.LINE_SEPARATOR);
				}
			} else if(Strings.isEmpty(livello)) { 
				if(progettoDettaglio.getUltimaVerificaLiv2() != null) {
					String data = progettoDettaglio.getUltimaVerificaLiv2().getData() == null ? Strings.EMPTY : 
						progettoDettaglio.getUltimaVerificaLiv2().getData();
					String utente = progettoDettaglio.getUltimaVerificaLiv2().getUtente() == null ? Strings.EMPTY : 
						progettoDettaglio.getUltimaVerificaLiv2().getUtente();
					csvContent.append(Constants.DATA_ULTIMO_AUTOCONTROLLO_LIV2).append(data).append(Strings.LINE_SEPARATOR);
					csvContent.append(Constants.UTENTE_ULTIMO_AUTOCONTROLLO_LIV2).append(utente).append(Strings.LINE_SEPARATOR);
				}
				if(progettoDettaglio.getUltimaVerificaLiv3() != null) {
					String data = progettoDettaglio.getUltimaVerificaLiv3().getData() == null ? Strings.EMPTY : 
						progettoDettaglio.getUltimaVerificaLiv3().getData();
					String utente = progettoDettaglio.getUltimaVerificaLiv3().getUtente() == null ? Strings.EMPTY : 
						progettoDettaglio.getUltimaVerificaLiv3().getUtente();
					csvContent.append(Constants.DATA_ULTIMO_AUTOCONTROLLO_LIV3).append(data).append(Strings.LINE_SEPARATOR);
					csvContent.append(Constants.UTENTE_ULTIMO_AUTOCONTROLLO_LIV3).append(utente).append(Strings.LINE_SEPARATOR);
				}
			}
		}
		
		return csvContent.toString();
	}
	
	private String getChecklistTable(List<ChecklistProgettoDto> checklist, boolean isRp, String livello) {
		StringBuilder csvContent = new StringBuilder();
		
		if(isRp) {
			if(VerificaLivelloEnum.VERIFICA_LIVELLO_2.getCode().equalsIgnoreCase(livello)) {
				csvContent.append(Constants.CSV_HEADER_CHECKLIST_RP_LIV2);
			} else if(VerificaLivelloEnum.VERIFICA_LIVELLO_3.getCode().equalsIgnoreCase(livello)) {
				csvContent.append(Constants.CSV_HEADER_CHECKLIST_RP_LIV3);
			} else {
				csvContent.append(Constants.CSV_HEADER_CHECKLIST_RP_SUPERUSER);
			}
		} else {
			csvContent.append(Constants.CSV_HEADER_CHECKLIST_ASR);
		}
		
		for(ChecklistProgettoDto checklistDto : checklist) {
			String risposta = checklistDto.rispostaCod() == null ? Constants.NON_INDICATO : checklistDto.rispostaCod();
			String notaAsr = checklistDto.notaAsr() == null ? Strings.EMPTY : checklistDto.notaAsr().replace("\n", " ");
			
			csvContent.append(checklistDto.numeroSezione()).append(Constants.CSV_SEPARATOR)
					.append(checklistDto.sezioneCod()).append(Constants.CSV_SEPARATOR)
					.append(checklistDto.sottoSezioneCod()).append(Constants.CSV_SEPARATOR)
					.append(checklistDto.documentazioneDaCaricareCod()).append(Constants.CSV_SEPARATOR)
					.append(risposta).append(Constants.CSV_SEPARATOR)
					.append(notaAsr);
			
			if(isRp) {
				if(VerificaLivelloEnum.VERIFICA_LIVELLO_2.getCode().equalsIgnoreCase(livello)) {
					String utenteUltimaModifica = checklistDto.utenteModificaRup() == null ? Strings.EMPTY : checklistDto.utenteModificaRup();
					String dataUltimaModifica = checklistDto.dataModificaRup() == null ? Strings.EMPTY : checklistDto.dataModificaRup();
					
					String verificatoLiv2 = (checklistDto.verificaCodLiv2() == null || checklistDto.verificaCodLiv2().equals(VerificaRilevazioneEnum.KO.getCode())) 
							? Constants.NON_INDICATO : Constants.AUTOCONTROLLATO;
					String notaRpLiv2 = checklistDto.noteVerificaLiv2() == null ? Strings.EMPTY : checklistDto.noteVerificaLiv2();
					
					csvContent.append(Constants.CSV_SEPARATOR)
						.append(utenteUltimaModifica).append(Constants.CSV_SEPARATOR) 
						.append(dataUltimaModifica).append(Constants.CSV_SEPARATOR) 
						.append(verificatoLiv2).append(Constants.CSV_SEPARATOR) 
						.append(notaRpLiv2);
					
				} else if(VerificaLivelloEnum.VERIFICA_LIVELLO_3.getCode().equalsIgnoreCase(livello)) {
					String utenteUltimaModifica = checklistDto.utenteModificaRup() == null ? Strings.EMPTY : checklistDto.utenteModificaRup();
					String dataUltimaModifica = checklistDto.dataModificaRup() == null ? Strings.EMPTY : checklistDto.dataModificaRup();
					
					String verificatoLiv2 = (checklistDto.verificaCodLiv2() == null || checklistDto.verificaCodLiv2().equals(VerificaRilevazioneEnum.KO.getCode())) 
							? Constants.NON_INDICATO : Constants.AUTOCONTROLLATO;
					String notaRpLiv2 = checklistDto.noteVerificaLiv2() == null ? Strings.EMPTY : checklistDto.noteVerificaLiv2();
					String utenteUltimaVerificaLiv2 = checklistDto.utenteVerificaLiv2() == null ? Strings.EMPTY : checklistDto.utenteVerificaLiv2();
					String dataUltimaVerificaLiv2 = checklistDto.dataVerificaLiv2() == null ? Strings.EMPTY : checklistDto.dataVerificaLiv2();
					
					String verificatoLiv3 = (checklistDto.verificaCodLiv3() == null || checklistDto.verificaCodLiv3().equals(VerificaRilevazioneEnum.KO.getCode())) 
							? Constants.NON_INDICATO : Constants.AUTOCONTROLLATO;
					String notaRpLiv3 = checklistDto.noteVerificaLiv3() == null ? Strings.EMPTY : checklistDto.noteVerificaLiv3();
					
					csvContent.append(Constants.CSV_SEPARATOR)
						.append(utenteUltimaModifica).append(Constants.CSV_SEPARATOR) 
						.append(dataUltimaModifica).append(Constants.CSV_SEPARATOR) 
						.append(verificatoLiv2).append(Constants.CSV_SEPARATOR) 
						.append(notaRpLiv2).append(Constants.CSV_SEPARATOR) 
						.append(utenteUltimaVerificaLiv2).append(Constants.CSV_SEPARATOR) 
						.append(dataUltimaVerificaLiv2).append(Constants.CSV_SEPARATOR) 
						.append(verificatoLiv3).append(Constants.CSV_SEPARATOR) 
						.append(notaRpLiv3);
				} else {
					String utenteUltimaModifica = checklistDto.utenteModificaRup() == null ? Strings.EMPTY : checklistDto.utenteModificaRup();
					String dataUltimaModifica = checklistDto.dataModificaRup() == null ? Strings.EMPTY : checklistDto.dataModificaRup();
					
					String verificatoLiv2 = (checklistDto.verificaCodLiv2() == null || checklistDto.verificaCodLiv2().equals(VerificaRilevazioneEnum.KO.getCode())) 
							? Constants.NON_INDICATO : Constants.AUTOCONTROLLATO;
					String notaRpLiv2 = checklistDto.noteVerificaLiv2() == null ? Strings.EMPTY : checklistDto.noteVerificaLiv2();
					String utenteUltimaVerificaLiv2 = checklistDto.utenteVerificaLiv2() == null ? Strings.EMPTY : checklistDto.utenteVerificaLiv2();
					String dataUltimaVerificaLiv2 = checklistDto.dataVerificaLiv2() == null ? Strings.EMPTY : checklistDto.dataVerificaLiv2();
					
					String verificatoLiv3 = (checklistDto.verificaCodLiv3() == null || checklistDto.verificaCodLiv3().equals(VerificaRilevazioneEnum.KO.getCode())) 
							? Constants.NON_INDICATO : Constants.AUTOCONTROLLATO;
					String notaRpLiv3 = checklistDto.noteVerificaLiv3() == null ? Strings.EMPTY : checklistDto.noteVerificaLiv3();
					String utenteUltimaVerificaLiv3 = checklistDto.utenteVerificaLiv3() == null ? Strings.EMPTY : checklistDto.utenteVerificaLiv3();
					String dataUltimaVerificaLiv3 = checklistDto.dataVerificaLiv3() == null ? Strings.EMPTY : checklistDto.dataVerificaLiv3();
					
					csvContent.append(Constants.CSV_SEPARATOR)
						.append(utenteUltimaModifica).append(Constants.CSV_SEPARATOR) 
						.append(dataUltimaModifica).append(Constants.CSV_SEPARATOR) 
						.append(verificatoLiv2).append(Constants.CSV_SEPARATOR) 
						.append(notaRpLiv2).append(Constants.CSV_SEPARATOR) 
						.append(utenteUltimaVerificaLiv2).append(Constants.CSV_SEPARATOR) 
						.append(dataUltimaVerificaLiv2).append(Constants.CSV_SEPARATOR) 
						.append(verificatoLiv3).append(Constants.CSV_SEPARATOR) 
						.append(notaRpLiv3).append(Constants.CSV_SEPARATOR) 
						.append(utenteUltimaVerificaLiv3).append(Constants.CSV_SEPARATOR) 
						.append(dataUltimaVerificaLiv3);
				}
			}
			
			csvContent.append(Strings.LINE_SEPARATOR);
		}
		
		return csvContent.toString();
	}
	
	private String getCup(List<ChecklistProgettoDto> checklistCup, boolean isRp, String livello) {
		StringBuilder csvContent = new StringBuilder();
		csvContent.append(Constants.DOCUMENTAZIONE_CUP).append(Strings.LINE_SEPARATOR);
		csvContent.append(getChecklistTable(checklistCup, isRp, livello));
		
		return csvContent.toString();
	}
	
	private String getCig(List<ChecklistProgettoDto> checklistCig, List<ProceduraCigDto> procedureCigDto, boolean isRp, String livello) {
		StringBuilder csvContent = new StringBuilder();
		csvContent.append(Constants.DOCUMENTAZIONE_CIG).append(Strings.LINE_SEPARATOR);
		
		if(procedureCigDto.size() == 0) {
			csvContent.append(getChecklistTable(checklistCig, isRp, livello));
		} else {
			for(ProceduraCigDto proceduraCigDto : procedureCigDto) {
				String cigTitle = Constants.CODICE_CIG + proceduraCigDto.cigCod() + Constants.PROCEDURA + proceduraCigDto.descrizioneProceduraCigCod();
				csvContent.append(cigTitle).append(Strings.LINE_SEPARATOR);
				List<ChecklistProgettoDto> checklistCigById = checklistCig.stream()
						.filter(c -> c.cigId().equals(proceduraCigDto.cigId()))
						.toList();
				csvContent.append(getChecklistTable(checklistCigById, isRp, livello));
				csvContent.append(Strings.LINE_SEPARATOR);
			}
		}
		
		return csvContent.toString();
	}
	
	private String getUtenteSanitario(List<UtenteSanitario> utenti) {
		String utentiString = utenti.stream()
			.map(r -> getUtenteSanitario(r))
			.collect(Collectors.joining(Constants.CSV_RUA_SEPARATOR));
		
		return utentiString;
	}
	
	private String getUtenteSanitario(UtenteSanitario rua) {
		return rua.getCognome() + Constants.SPACE + rua.getNome();
	}
}
