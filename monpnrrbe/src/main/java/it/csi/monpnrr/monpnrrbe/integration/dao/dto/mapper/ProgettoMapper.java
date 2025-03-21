/*
* SPDX-FileCopyrightText: (C) Copyright 2025 Regione Piemonte
*
* SPDX-License-Identifier: EUPL-1.2 
*/
package it.csi.monpnrr.monpnrrbe.integration.dao.dto.mapper;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.apache.logging.log4j.util.Strings;
import org.springframework.stereotype.Component;

import it.csi.monpnrr.monpnrrbe.api.dto.Azienda;
import it.csi.monpnrr.monpnrrbe.api.dto.Checklist;
import it.csi.monpnrr.monpnrrbe.api.dto.ChecklistProgetto;
import it.csi.monpnrr.monpnrrbe.api.dto.ChecklistProgettoList;
import it.csi.monpnrr.monpnrrbe.api.dto.ChecklistProgettoRilevazione;
import it.csi.monpnrr.monpnrrbe.api.dto.ChecklistProgettoSezione;
import it.csi.monpnrr.monpnrrbe.api.dto.ChecklistProgettoSottosezione;
import it.csi.monpnrr.monpnrrbe.api.dto.DataUtente;
import it.csi.monpnrr.monpnrrbe.api.dto.ProceduraCig;
import it.csi.monpnrr.monpnrrbe.api.dto.Progetto;
import it.csi.monpnrr.monpnrrbe.api.dto.ProgettoDettaglio;
import it.csi.monpnrr.monpnrrbe.api.dto.UtenteSanitario;
import it.csi.monpnrr.monpnrrbe.integration.dao.dto.ChecklistProgettoDto;
import it.csi.monpnrr.monpnrrbe.integration.dao.dto.ProceduraCigDto;
import it.csi.monpnrr.monpnrrbe.integration.dao.dto.RispostaDto;
import it.csi.monpnrr.monpnrrbe.integration.dao.dto.VerificaDto;
import it.csi.monpnrr.monpnrrbe.util.Constants;
import it.csi.monpnrr.monpnrrbe.util.Util;
import it.csi.monpnrr.monpnrrbe.util.enumeration.VerificaRilevazioneEnum;
import it.csi.monpnrr.monpnrrbe.util.log.AbstractLogger;

@Component
public class ProgettoMapper extends AbstractLogger {
	
	public Progetto buildProgetto(ResultSet rs) throws SQLException {
		Progetto result = new Progetto();
		
		result.setTotalCount(rs.getBigDecimal("total_count"));
		result.setCup(rs.getString("id_cup"));
		result.setDescrizione(rs.getString("misura_desc"));
		result.setRua(buildUtenteSanitario(rs.getString("rua")));
		result.setRup(buildUtenteSanitario(rs.getString("rup")));
		result.setChecklist(buildChecklist(rs.getString("checklist_desc"), rs.getString("stato_rilevazione_desc")));
		result.setUltimaModifica(buildDataUtente(rs.getString("ultima_modifica_utente"), rs.getString("ultima_modifica_data")));
		result.setUltimaVerifica(buildDataUtente(getUltimaVerificaUtente(rs), getUltimaVerificaData(rs)));
		result.setModificaRecente(rs.getBoolean("modifica_recente"));
		result.setAzienda(buildAzienda(rs.getString("asl_cod"), rs.getString("asl_azienda_desc")));
		return result;
	}
	
	private String getUltimaVerificaUtente(ResultSet rs) throws SQLException {
		if(findColumn(rs, "ultima_verifica_utente")) {
			return rs.getString("ultima_verifica_utente");
		} else if(findColumn(rs, "ultima_verifica_utente_liv2") && findColumn(rs, "ultima_verifica_utente_liv3")) {
			String liv = getMaxDate(rs.getString("ultima_verifica_data_liv2"), rs.getString("ultima_verifica_data_liv3"));
			return rs.getString("ultima_verifica_utente_" + liv);
		} else if(findColumn(rs, "ultima_verifica_utente_liv2")) {
			return rs.getString("ultima_verifica_utente_liv2");
		} else if(findColumn(rs, "ultima_verifica_utente_liv3")) {
			return rs.getString("ultima_verifica_utente_liv3");
		} 
		
		return Strings.EMPTY;
	}
	
	private String getUltimaVerificaData(ResultSet rs) throws SQLException {
		if(findColumn(rs, "ultima_verifica_data")) {
			return rs.getString("ultima_verifica_data");
		} else if(findColumn(rs, "ultima_verifica_data_liv2") && findColumn(rs, "ultima_verifica_data_liv3")) {
			String liv = getMaxDate(rs.getString("ultima_verifica_data_liv2"), rs.getString("ultima_verifica_data_liv3"));
			return rs.getString("ultima_verifica_data_" + liv);
		} else if(findColumn(rs, "ultima_verifica_data_liv2")) {
			return rs.getString("ultima_verifica_data_liv2");
		} else if(findColumn(rs, "ultima_verifica_data_liv3")) {
			return rs.getString("ultima_verifica_data_liv3");
		} 
		
		return Strings.EMPTY;
	}
	
	private String getMaxDate(String dateLiv2, String dateLiv3) {
		if(!Util.isValorizzato(dateLiv2) && !Util.isValorizzato(dateLiv3)) {
			return Constants.LIV2;
		} else if (!Util.isValorizzato(dateLiv2)) {
			return Constants.LIV3;
		} else if (!Util.isValorizzato(dateLiv3)) {
			return Constants.LIV2;
		}
		
		Date liv2 = Util.fromStringToDate(dateLiv2, Constants.FORMATTER_USA);
		Date liv3 = Util.fromStringToDate(dateLiv3, Constants.FORMATTER_USA);
		
		if(liv2.before(liv3)) {
			return Constants.LIV3;
		} else {
			return Constants.LIV2;
		}
	}
	
	private Boolean findColumn(ResultSet rs, String column) throws SQLException {
		ResultSetMetaData rsMetaData = rs.getMetaData();
		int numberOfColumns = rsMetaData.getColumnCount();

		// get the column names; column indexes start from 1
		for (int i = 1; i < numberOfColumns + 1; i++) {
		    String columnName = rsMetaData.getColumnName(i);
		    // Get the name of the column's table name
		    if (column.equals(columnName)) {
		        return true;
		    }
		}
		
		return false;
	}
	
	private Checklist buildChecklist(String checklistCod, String statoRilevazioneCod) {
		Checklist checklist = new Checklist();
		checklist.setCodice(checklistCod);
		checklist.setStato(statoRilevazioneCod);
		
		return checklist;
	}
	
	private List<UtenteSanitario> buildUtenteSanitario(String utenteString) {
		List<UtenteSanitario> utenteSanitarioList = new ArrayList<UtenteSanitario>();
		
		String[] utenteSplitted = utenteString.split(",");
		for(String utenteCod : utenteSplitted) {
			UtenteSanitario utente = new UtenteSanitario();
			String[] utenteNameSurname = utenteCod.split("-");
			utente.setNome(utenteNameSurname[0].trim());
			utente.setCognome(utenteNameSurname[1].trim());
			
			utenteSanitarioList.add(utente);
		}
				
		return utenteSanitarioList;
	}
	
	public DataUtente buildDataUtente(ResultSet rs) throws SQLException {
		return buildDataUtente(rs.getString("utente_modifica"), rs.getString("data_modifica"));
	}
	
	private DataUtente buildDataUtente(String utente, String dataString) {
		DataUtente dataUtente = new DataUtente();
		
		if(utente != null) {
			dataUtente.setUtente(utente.trim());
		}
		
		if(dataString != null) {
			Date date = Util.fromStringToDate(dataString, Constants.FORMATTER_USA);
			dataUtente.setData(Util.fromDateToString(date, Constants.FORMATTER_ITA));
		}
		
		return dataUtente;
	}
	
	private Azienda buildAzienda(String codice, String descrizione) {
		Azienda azienda = new Azienda();
		azienda.setCodice(codice);
		azienda.setDescrizione(descrizione);
		return azienda;
	}
	
	public ProgettoDettaglio buildProgettoDettaglio(ResultSet rs) throws SQLException {
		ProgettoDettaglio progettoDettaglio = new ProgettoDettaglio();
		progettoDettaglio.setChecklist(rs.getString("checklist_desc"));
		progettoDettaglio.setCup(rs.getString("id_cup"));
		progettoDettaglio.setIntervento(rs.getString("misura_desc"));
		String modalitaAttuativa = rs.getString("modalita_attuativa") == null ? Strings.EMPTY : rs.getString("modalita_attuativa");
		progettoDettaglio.setModalitaAttuativa(modalitaAttuativa);
		progettoDettaglio.setAzienda(buildAzienda(rs.getString("asl_cod"), rs.getString("asl_azienda_desc")));
		progettoDettaglio.setRup(buildUtenteSanitario(rs.getString("rup")));
		progettoDettaglio.setUltimaModifica(buildDataUtente(rs.getString("ultima_modifica_utente"), rs.getString("ultima_modifica_data")));
		progettoDettaglio.setUltimaVerificaLiv2(buildDataUtente(rs.getString("ultima_verifica_liv2_utente"), rs.getString("ultima_verifica_liv2_data")));
		progettoDettaglio.setUltimaVerificaLiv3(buildDataUtente(rs.getString("ultima_verifica_liv3_utente"), rs.getString("ultima_verifica_liv3_data")));
		progettoDettaglio.setUltimaVerifica(buildDataUtente(rs.getString("ultima_verifica_utente"), rs.getString("ultima_verifica_data")));
		
		return progettoDettaglio;
	}
	
	public ChecklistProgettoDto buildProgettoDto(ResultSet rs) throws SQLException {
		ChecklistProgettoDto progettoDto = new ChecklistProgettoDto(
				rs.getString("macrosezioni_cod"),
				rs.getInt("cig_id"),
				rs.getString("numero_sezione"),
				rs.getString("sezione_cod"),
				rs.getString("sotto_sezione_cod"),
				rs.getInt("r_rilevazione_domanda_id"),
				rs.getString("documentazione_da_caricare_cod"),
				rs.getString("note"),
				rs.getString("risposta_cod"),
				rs.getString("nota"),
				rs.getString("utente_modifica_rup"),
				rs.getString("data_modifica_rup"),
				rs.getString("verifica_cod_liv2"),
				rs.getString("nota_verifica_liv2"),
				rs.getString("verifica_livello_cod_liv2"),
				rs.getString("utente_verifica_liv2"),
				rs.getString("data_verifica_liv2"),
				rs.getBoolean("is_verificato_liv2"),
				rs.getString("verifica_cod_liv3"),
				rs.getString("nota_verifica_liv3"),
				rs.getString("verifica_livello_cod_liv3"),
				rs.getString("utente_verifica_liv3"),
				rs.getString("data_verifica_liv3"),
				rs.getBoolean("is_verificato_liv3")
		);
				
		return progettoDto;
	}
	
	public ProceduraCigDto buildCigProcedureDto(ResultSet rs) throws SQLException {
		ProceduraCigDto cigProcedureDto = new ProceduraCigDto(
				rs.getInt("cig_id"),
				rs.getString("cig_cod"),
				rs.getString("descrizione_procedura_cig_cod"),
				rs.getInt("progressivo_rilevazione")
		);
				
		return cigProcedureDto;
	}
	
	public ChecklistProgettoList buildChecklistProgetto(List<ChecklistProgettoDto> progettiDto, 
			List<ProceduraCigDto> cigProcedureDto, int progressivoCig, String stato) {
        ChecklistProgettoList checklistProgettoList = new ChecklistProgettoList();
        checklistProgettoList.setStato(stato);
        checklistProgettoList.setProgressivoCig(progressivoCig);
        for (ChecklistProgettoDto progettoDto : progettiDto) {
            convertToChecklistProgetto(progettoDto, checklistProgettoList, cigProcedureDto);
        }
        return checklistProgettoList;
    }
	
	private void convertToChecklistProgetto(ChecklistProgettoDto progettoDto, ChecklistProgettoList checklistProgettoList, 
			List<ProceduraCigDto> cigProcedureDto) {
	    String macrosezioneCod = progettoDto.macroSezioneCod();
	    Integer proceduraCigId = progettoDto.cigId();
	    String proceduraCigCod = proceduraCigId != null ? findProceduraCigCodById(cigProcedureDto, proceduraCigId) : null;
	    ChecklistProgetto macrosezioneEsistente = findMacrosezioneByNumeroAndCig(checklistProgettoList, macrosezioneCod, proceduraCigCod);
	    if (macrosezioneEsistente != null) {
	    	convertToChecklistProgettoSezioni(progettoDto, macrosezioneEsistente);
//	        macrosezioneEsistente.setCig(buildProceduraCig(proceduraCigId, cigProcedureDto));
	        macrosezioneEsistente.setSezioni(macrosezioneEsistente.getSezioni());
	    } else {
	        ChecklistProgetto macrosezione = new ChecklistProgetto();
	        macrosezione.setMacrosezione(macrosezioneCod);
	        macrosezione.setCig(buildProceduraCig(progettoDto.cigId(), cigProcedureDto));
	        convertToChecklistProgettoSezioni(progettoDto, macrosezione);
	        checklistProgettoList.getMacrosezioni().add(macrosezione); // Modifica diretta della lista passata come argomento
	    }
	}
	
	private ChecklistProgetto findMacrosezioneByNumeroAndCig(ChecklistProgettoList progetto,
			String numeroMacrosezione, String cigCod) {
		Optional<ChecklistProgetto> checklistProgetto = Optional.empty();
		if(progetto.getMacrosezioni() != null) { 
			if(cigCod != null)
				checklistProgetto = progetto.getMacrosezioni().stream().filter(p -> p.getMacrosezione().equalsIgnoreCase(numeroMacrosezione) && p.getCig() != null && p.getCig().getNumeroCig().equalsIgnoreCase(cigCod)).findFirst();
			else 
				checklistProgetto = progetto.getMacrosezioni().stream().filter(p -> p.getMacrosezione().equalsIgnoreCase(numeroMacrosezione)).findFirst();
			
			if(checklistProgetto.isPresent()) {
				return checklistProgetto.get();
			}
		}
		
		return null;
	}
	
	private String findProceduraCigCodById(List<ProceduraCigDto> cigProcedureDto,
			Integer proceduraCigId) {
		Optional<ProceduraCigDto> proceduraCig = cigProcedureDto.stream().filter(p -> p.cigId().equals(proceduraCigId)).findFirst();
		if(proceduraCig.isPresent()) {
			return proceduraCig.get().cigCod();
		}
		
		return null;
	}

	private void convertToChecklistProgettoSezioni(ChecklistProgettoDto progettoDto, ChecklistProgetto checklistProgetto) {
	    String sezioneCod = progettoDto.sezioneCod();
	    ChecklistProgettoSezione sezioneEsistente = findSezioneByNome(checklistProgetto.getSezioni(), sezioneCod);
	    if (sezioneEsistente != null) {
	        convertToChecklistProgettoSottosezioni(progettoDto, sezioneEsistente);
	        sezioneEsistente.setSottosezioni(sezioneEsistente.getSottosezioni());
	    } else {
	        ChecklistProgettoSezione sezione = new ChecklistProgettoSezione();
	        sezione.setNumero(progettoDto.numeroSezione());
	        sezione.setNome(sezioneCod);
	        convertToChecklistProgettoSottosezioni(progettoDto, sezione);
	        checklistProgetto.getSezioni().add(sezione); // Modifica diretta della lista passata come argomento
	    }
	}

	private ChecklistProgettoSezione findSezioneByNome(List<ChecklistProgettoSezione> sezioni,
			String nomeSezione) {
		Optional<ChecklistProgettoSezione> sezione = sezioni.stream().filter(s -> s.getNome().equals(nomeSezione)).findFirst();
		if(sezione.isPresent()) {
			return sezione.get();
		}
		
		return null;
	}

	public void convertToChecklistProgettoSottosezioni(ChecklistProgettoDto progettoDto, ChecklistProgettoSezione sezione) {
		String sottosezioneCod = progettoDto.sottoSezioneCod();
	    ChecklistProgettoSottosezione sottosezioneEsistente = findSottosezioneByNumero(sezione.getSottosezioni(), sottosezioneCod);
	    if (sottosezioneEsistente != null) {
	        buildChecklistProgettoRilevazioni(progettoDto, sottosezioneEsistente);
	    } else {
	        ChecklistProgettoSottosezione sottosezione = new ChecklistProgettoSottosezione();
	        sottosezione.setNome(sottosezioneCod);
	        buildChecklistProgettoRilevazioni(progettoDto, sottosezione);
	        sezione.getSottosezioni().add(sottosezione); // Modifica diretta della lista passata come argomento
	    }
	    
	}
	
	private ChecklistProgettoSottosezione findSottosezioneByNumero(List<ChecklistProgettoSottosezione> sottosezioni,
			String nomeSottosezione) {
		Optional<ChecklistProgettoSottosezione> sottosezione = sottosezioni.stream().filter(s -> s.getNome().equals(nomeSottosezione)).findFirst();
		if(sottosezione.isPresent()) {
			return sottosezione.get();
		}
		
		return null;
	}
	
	public void buildChecklistProgettoRilevazioni(ChecklistProgettoDto progettoDto, ChecklistProgettoSottosezione sottosezione) {
    	ChecklistProgettoRilevazione rilevazione = new ChecklistProgettoRilevazione();
        rilevazione.setNote(progettoDto.note());
        rilevazione.setEsito(progettoDto.rispostaCod());
        rilevazione.setNome(progettoDto.documentazioneDaCaricareCod());
        rilevazione.setNoteAsr(progettoDto.notaAsr());
        rilevazione.setRilevazioneDomandaId(progettoDto.rilevazioneDomandaId());
        rilevazione.setUltimaModifica(buildDataUtente(progettoDto.utenteModificaRup(), progettoDto.dataModificaRup()));
        rilevazione.setEsitoVerificaLiv2(isEsitoVerificato(progettoDto.verificaCodLiv2()));
        rilevazione.setEsitoVerificaLiv3(isEsitoVerificato(progettoDto.verificaCodLiv3()));
        rilevazione.setVerificatoLiv2(isVerificato(progettoDto.isVerificatoLiv2()));
        rilevazione.setVerificatoLiv3(isVerificato(progettoDto.isVerificatoLiv3()));
        
    	if(progettoDto.livelloVerificaLiv2() != null) {
        	rilevazione.setNotaVerificaLiv2(progettoDto.noteVerificaLiv2());
        	rilevazione.setUltimaVerificaLiv2(buildDataUtente(progettoDto.utenteVerificaLiv2(), progettoDto.dataVerificaLiv2()));
        	
    	}
    	if(progettoDto.livelloVerificaLiv3() != null) {
        	rilevazione.setNotaVerificaLiv3(progettoDto.noteVerificaLiv3());
        	rilevazione.setUltimaVerificaLiv3(buildDataUtente(progettoDto.utenteVerificaLiv3(), progettoDto.dataVerificaLiv3()));
        	
    	}
        
        sottosezione.getRilevazioni().add(rilevazione); // Modifica diretta della lista passata come argomento
	}
	
	private boolean isEsitoVerificato(String verificaCod) {
		if(verificaCod == null) {
			return false;
		}

		return verificaCod.equals(VerificaRilevazioneEnum.OK.getCode()) ? true : false;
	}
	
	private boolean isVerificato(Boolean isVerificato) {
		if(isVerificato == null) {
			return false;
		}

		return isVerificato;
	}
	
	private ProceduraCig buildProceduraCig(Integer cigId, List<ProceduraCigDto> procedureCig) {
		Optional<ProceduraCigDto> proceduraCigDtoOptional = procedureCig.stream().filter(p -> p.cigId().equals(cigId)).findFirst();
		if(proceduraCigDtoOptional.isPresent()) {
			ProceduraCig proceduraCig = new ProceduraCig();
			proceduraCig.setDescrizioneProceduraCig(proceduraCigDtoOptional.get().descrizioneProceduraCigCod());
			proceduraCig.setNumeroCig(proceduraCigDtoOptional.get().cigCod());
			proceduraCig.setProgressivo(proceduraCigDtoOptional.get().progressivoRilevazione());
			return proceduraCig;
		}
		
		return null;
	}
	
	public RispostaDto buildRispostaDto(ResultSet rs) throws SQLException {
		RispostaDto result = new RispostaDto(
				rs.getInt("r_rilevazione_domanda_id"),
				rs.getInt("risposta_id"),
				rs.getString("nota"),
				rs.getInt("cig_id")
			);

		return result;
	}
	
	public VerificaDto buildVerificaDto(ResultSet rs) throws SQLException {
		VerificaDto result = new VerificaDto(
				rs.getInt("r_rilevazione_domanda_id"),
				rs.getInt("verifica_id"),
				rs.getInt("valori_verifica_id"),
				rs.getString("nota"),
				rs.getInt("cig_id")
			);

		return result;
	}
}
