/*
* SPDX-FileCopyrightText: (C) Copyright 2025 Regione Piemonte
*
* SPDX-License-Identifier: EUPL-1.2 
*/
package it.csi.monpnrr.monpnrrbe.integration.dao;

import java.sql.Types;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.csi.monpnrr.monpnrrbe.api.dto.ChecklistProgettoList;
import it.csi.monpnrr.monpnrrbe.api.dto.ChecklistRisposta;
import it.csi.monpnrr.monpnrrbe.api.dto.ChecklistRispostaList;
import it.csi.monpnrr.monpnrrbe.api.dto.DataUtente;
import it.csi.monpnrr.monpnrrbe.api.dto.ProceduraRisposteCig;
import it.csi.monpnrr.monpnrrbe.api.dto.Progetto;
import it.csi.monpnrr.monpnrrbe.api.dto.ProgettoDettaglio;
import it.csi.monpnrr.monpnrrbe.api.dto.VerificaRisposta;
import it.csi.monpnrr.monpnrrbe.api.dto.VerificaRispostaCig;
import it.csi.monpnrr.monpnrrbe.api.dto.VerificaRispostaList;
import it.csi.monpnrr.monpnrrbe.exception.DatabaseException;
import it.csi.monpnrr.monpnrrbe.integration.dao.dto.ChecklistProgettoDto;
import it.csi.monpnrr.monpnrrbe.integration.dao.dto.FiltersDto;
import it.csi.monpnrr.monpnrrbe.integration.dao.dto.ProceduraCigDto;
import it.csi.monpnrr.monpnrrbe.integration.dao.dto.RispostaDto;
import it.csi.monpnrr.monpnrrbe.integration.dao.dto.VerificaDto;
import it.csi.monpnrr.monpnrrbe.integration.dao.dto.mapper.ProgettoMapper;
import it.csi.monpnrr.monpnrrbe.util.enumeration.ProfiloEnum;
import it.csi.monpnrr.monpnrrbe.util.enumeration.RispostaEnum;
import it.csi.monpnrr.monpnrrbe.util.enumeration.VerificaLivelloEnum;
import it.csi.monpnrr.monpnrrbe.util.enumeration.VerificaRilevazioneEnum;

@Service
public class ProgettiDao extends UtilDao {
	@Autowired
	private NamedParameterJdbcTemplate jdbcTemplate;
	
	@Autowired
	private ProgettoMapper progettoMapper;
	
	public List<Progetto> getProgetti(String cf, ProfiloEnum profilo, FiltersDto filters) throws DatabaseException {
		String metodo = new Object() {}.getClass().getEnclosingMethod().getName();
		try {
			return switch (profilo) {
			    case RUP -> getProgettiRup(cf, filters);
			    case RUA -> getProgettiRua(filters);
			    case SUPER_USER -> getProgettiRPSuperUser(filters);
			    case AUTOCONTROLLO_2 -> getProgettiRPLiv2(filters);
			    case AUTOCONTROLLO_3 -> getProgettiRPLiv3(filters);
			    default -> null;
			};

		} catch (Exception e) {
			logError(metodo, e.getMessage());
			throw new DatabaseException(e);
		}
	}
	
	public List<Progetto> getProgettiRup(String cf, FiltersDto filtersDto) throws DatabaseException {
		String metodo = new Object() {}.getClass().getEnclosingMethod().getName();
		try {
			MapSqlParameterSource namedParameters = new MapSqlParameterSource().addValue("cf", cf);
			String filters = addProgettiFilters(filtersDto, namedParameters);
			
			String SELECT_PROGETTO_RUP = 
				    SELECT_RUP_FOR_RUP_PROFILE +
					SELECT_RUA +
					SELECT_ULTIMA_MODIFICA +
					SELECT_ULTIMA_VERIFICA_ASR +
					GET_PROGETTI_SELECT +
					GET_PROGETTI_SELECT_VERIFICA_ASR + 
					GET_PROGETTI_FROM +
					GET_PROGETTI_FROM_VERIFICA_ASR + 
					GET_PROGETTI_WHERE +
					filters +
					GET_PROGETTI_GROUP_BY +
					GET_PROGETTI_GROUP_BY_ASR_AND_RP_LIV +
					addOrdering(filtersDto) +
					addPagination(filtersDto);
			
			List<Progetto> result = jdbcTemplate.query(SELECT_PROGETTO_RUP, namedParameters, 
					(rs, rowNum) -> progettoMapper.buildProgetto(rs));
			return result;
		} catch (Exception e) {
			logError(metodo, e.getMessage());
			throw new DatabaseException(e);
		}
	}
	
	public List<Progetto> getProgettiRua(FiltersDto filtersDto) throws DatabaseException {
		String metodo = new Object() {}.getClass().getEnclosingMethod().getName();
		try {
			MapSqlParameterSource namedParameters = new MapSqlParameterSource();
			String filters = addProgettiFilters(filtersDto, namedParameters);
			filters += addProgettiFiltersASR(filtersDto, namedParameters);
			
			String SELECT_PROGETTO_RUA = 
				    SELECT_RUP_FOR_OTHERS_PROFILE +
					SELECT_RUA +
					SELECT_ULTIMA_MODIFICA +
					SELECT_ULTIMA_VERIFICA_ASR +
					GET_PROGETTI_SELECT +
					GET_PROGETTI_SELECT_VERIFICA_ASR + 
					GET_PROGETTI_FROM +
					GET_PROGETTI_FROM_VERIFICA_ASR +
					GET_PROGETTI_WHERE +
					filters +
					GET_PROGETTI_GROUP_BY +
					GET_PROGETTI_GROUP_BY_ASR_AND_RP_LIV +	
					addOrdering(filtersDto) +
					addPagination(filtersDto);
			
			List<Progetto> result = jdbcTemplate.query(SELECT_PROGETTO_RUA, namedParameters,
					(rs, rowNum) -> progettoMapper.buildProgetto(rs)); 
			return result;
		} catch (Exception e) {
			logError(metodo, e.getMessage());
			throw new DatabaseException(e);
		}
	}
	
	public List<Progetto> getProgettiRPSuperUser(FiltersDto filtersDto) throws DatabaseException {
		String metodo = new Object() {}.getClass().getEnclosingMethod().getName();
		try {
			MapSqlParameterSource namedParameters = new MapSqlParameterSource();
			namedParameters.addValue("verifica2Cod", VerificaLivelloEnum.VERIFICA_LIVELLO_2.getCode());
			namedParameters.addValue("verifica3Cod", VerificaLivelloEnum.VERIFICA_LIVELLO_3.getCode());
			
			String filters = addProgettiFilters(filtersDto, namedParameters);
			filters += addProgettiFiltersRPSuperUser(filtersDto, namedParameters);
			
			String SELECT_PROGETTO_RP_SUPER_USER = 
				    SELECT_RUP_FOR_OTHERS_PROFILE +
					SELECT_RUA +
					SELECT_ULTIMA_MODIFICA +
					SELECT_ULTIMA_VERIFICA_LIV2 + ", " +
					SELECT_ULTIMA_VERIFICA_LIV3 +
					GET_PROGETTI_SELECT +
					getSelectVerificaRpSuperUser(filtersDto) +
					GET_PROGETTI_FROM +
					GET_PROGETTI_FROM_VERIFICA_RP_LIV2 + 
					GET_PROGETTI_FROM_VERIFICA_RP_LIV3 + 
					GET_PROGETTI_WHERE +
					filters +
					GET_PROGETTI_GROUP_BY +
					GET_PROGETTI_GROUP_BY_RP_SUPERUSER +
					addOrdering(filtersDto) +
					addPagination(filtersDto);
			
			List<Progetto> result = jdbcTemplate.query(SELECT_PROGETTO_RP_SUPER_USER, namedParameters,
					(rs, rowNum) -> progettoMapper.buildProgetto(rs)); 
			return result;
		} catch (Exception e) {
			logError(metodo, e.getMessage());
			throw new DatabaseException(e);
		}
	}
	
	private String getSelectVerificaRpSuperUser(FiltersDto filtersDto) {
		if((!isLiv2FilterEnabled(filtersDto) && !isLiv3FilterEnabled(filtersDto))
				|| (isLiv2FilterEnabled(filtersDto) && isLiv3FilterEnabled(filtersDto))) {
			return GET_PROGETTI_SELECT_VERIFICA_RP_SUPERUSER;
		} else if(isLiv2FilterEnabled(filtersDto) && !isLiv3FilterEnabled(filtersDto)) {
			return GET_PROGETTI_SELECT_VERIFICA_RP_LIV2;
		} else if(!isLiv2FilterEnabled(filtersDto) && isLiv3FilterEnabled(filtersDto)) {
			return GET_PROGETTI_SELECT_VERIFICA_RP_LIV3;
		}
		
		return Strings.EMPTY;
	}
	
	public List<Progetto> getProgettiRPLiv2(FiltersDto filtersDto) throws DatabaseException {
		String metodo = new Object() {}.getClass().getEnclosingMethod().getName();
		try {
			MapSqlParameterSource namedParameters = new MapSqlParameterSource();
			namedParameters.addValue("verifica2Cod", VerificaLivelloEnum.VERIFICA_LIVELLO_2.getCode());
			
			String filters = addProgettiFilters(filtersDto, namedParameters);
			filters += addProgettiFiltersRPLiv2(filtersDto, namedParameters);
			
			String SELECT_PROGETTO_RP_LIV2 = 
					SELECT_RUP_FOR_OTHERS_PROFILE +
					SELECT_RUA +
					SELECT_ULTIMA_MODIFICA +
					SELECT_ULTIMA_VERIFICA_LIV2 + 
					GET_PROGETTI_SELECT +
					GET_PROGETTI_SELECT_VERIFICA_RP_LIV2 + 
					GET_PROGETTI_FROM +
					GET_PROGETTI_FROM_VERIFICA_RP_LIV2 + 
					GET_PROGETTI_WHERE +
					filters +
					GET_PROGETTI_GROUP_BY +
					GET_PROGETTI_GROUP_BY_ASR_AND_RP_LIV +
					addOrdering(filtersDto) +
					addPagination(filtersDto);
			
			List<Progetto> result = jdbcTemplate.query(SELECT_PROGETTO_RP_LIV2, namedParameters,
					(rs, rowNum) -> progettoMapper.buildProgetto(rs)); 
			return result;
		} catch (Exception e) {
			logError(metodo, e.getMessage());
			throw new DatabaseException(e);
		}
	}
	
	public List<Progetto> getProgettiRPLiv3(FiltersDto filtersDto) throws DatabaseException {
		String metodo = new Object() {}.getClass().getEnclosingMethod().getName();
		try {
			MapSqlParameterSource namedParameters = new MapSqlParameterSource();
			namedParameters.addValue("verifica3Cod", VerificaLivelloEnum.VERIFICA_LIVELLO_3.getCode());
			
			String filters = addProgettiFilters(filtersDto, namedParameters);
			filters += addProgettiFiltersRPLiv3(filtersDto, namedParameters);
			
			String SELECT_PROGETTO_RP_LIV3 = 
					SELECT_RUP_FOR_OTHERS_PROFILE +
					SELECT_RUA +
					SELECT_ULTIMA_MODIFICA +
					SELECT_ULTIMA_VERIFICA_LIV3 + 
					GET_PROGETTI_SELECT +
					GET_PROGETTI_SELECT_VERIFICA_RP_LIV3 + 
					GET_PROGETTI_FROM +
					GET_PROGETTI_FROM_VERIFICA_RP_LIV3 + 
					GET_PROGETTI_WHERE +
					filters +
					GET_PROGETTI_GROUP_BY +
					GET_PROGETTI_GROUP_BY_ASR_AND_RP_LIV +
					addOrdering(filtersDto) +
					addPagination(filtersDto);
			
			List<Progetto> result = jdbcTemplate.query(SELECT_PROGETTO_RP_LIV3, namedParameters,
					(rs, rowNum) -> progettoMapper.buildProgetto(rs)); 
			return result;
		} catch (Exception e) {
			logError(metodo, e.getMessage());
			throw new DatabaseException(e);
		}
	}
	
	public DataUtente getDataUtenteUltimaModifica(Integer id) throws DatabaseException {
		String metodo = new Object() {}.getClass().getEnclosingMethod().getName();
		try {
			SqlParameterSource namedParameters = new MapSqlParameterSource().addValue("id", id);
			DataUtente result = jdbcTemplate.queryForObject(SELECT_DATA_UTENTE_PROGETTO, namedParameters,
					(rs, rowNum) -> progettoMapper.buildDataUtente(rs)); 
			return result;
		} catch (EmptyResultDataAccessException e) {
			logWarn(metodo, e.getMessage());
			return null;
		} catch (Exception e) {
			logError(metodo, e.getMessage());
			throw new DatabaseException(e);
		}
	}
	
	public ProgettoDettaglio getProgettoDettaglio(String cup) throws DatabaseException {
		String metodo = new Object() {}.getClass().getEnclosingMethod().getName();
		try {
			SqlParameterSource namedParameters = new MapSqlParameterSource()
					.addValue("cup", cup)
					.addValue("verifica2Cod", VerificaLivelloEnum.VERIFICA_LIVELLO_2.getCode())
					.addValue("verifica3Cod", VerificaLivelloEnum.VERIFICA_LIVELLO_3.getCode());
			ProgettoDettaglio result = jdbcTemplate.queryForObject(SELECT_PROGETTO_DETTAGLIO, namedParameters,
					(rs, rowNum) -> progettoMapper.buildProgettoDettaglio(rs)); 
			return result;
		} catch (Exception e) {
			logError(metodo, e.getMessage());
			throw new DatabaseException(e);
		}
	}
	
	public List<ChecklistProgettoDto> getChecklistProgettoDto(String cupId, String livello) throws DatabaseException {
		String metodo = new Object() {}.getClass().getEnclosingMethod().getName();
		try {
			SqlParameterSource namedParameters = new MapSqlParameterSource()
					.addValue("cupId", cupId)
					.addValue("verificaLivello2Cod", VerificaLivelloEnum.VERIFICA_LIVELLO_2.getCode())
					.addValue("verificaLivello3Cod", VerificaLivelloEnum.VERIFICA_LIVELLO_3.getCode())
					.addValue("verificaCod", VerificaRilevazioneEnum.OK.getCode())
					.addValue("rispostaCod", RispostaEnum.SI.getCode());
			List<ChecklistProgettoDto> progettiDto = jdbcTemplate.query(SELECT_CHECKLIST_PROGETTO, namedParameters, 
					(rs, rowNum) -> progettoMapper.buildProgettoDto(rs));
			
			return progettiDto;
		} catch (Exception e) {
			logError(metodo, e.getMessage());
			throw new DatabaseException(e);
		}
	}
	
	public List<ProceduraCigDto> getProcedureCigDto(Integer rilevazioneId) throws DatabaseException {
		String metodo = new Object() {}.getClass().getEnclosingMethod().getName();
		try {
			SqlParameterSource namedParameters = new MapSqlParameterSource().addValue("rilevazioneId", rilevazioneId);
			
			List<ProceduraCigDto> cigProcedureDto = jdbcTemplate.query(SELECT_CIG, namedParameters, 
					(rs, rowNum) -> progettoMapper.buildCigProcedureDto(rs));
			
			return cigProcedureDto;
		} catch (Exception e) {
			logError(metodo, e.getMessage());
			throw new DatabaseException(e);
		}
	}
	
	public Integer getProcedureCigDtoFromCodAndDescrizione(Integer rilevazioneId,
			String cigCod, String descrizioneProceduraCigCod) throws DatabaseException {
		String metodo = new Object() {}.getClass().getEnclosingMethod().getName();
		try {
			SqlParameterSource namedParameters = new MapSqlParameterSource()
					.addValue("rilevazioneId", rilevazioneId)
					.addValue("descrizioneProceduraCigCod", descrizioneProceduraCigCod)
					.addValue("cigCod", cigCod);
			
			return jdbcTemplate.queryForObject(SELECT_CIG_ID_BY_COD_AND_DESCRIZIONE, namedParameters, 
					Integer.class);
		} catch (Exception e) {
			logError(metodo, e.getMessage());
			throw new DatabaseException(e);
		}
	}
	
	public ChecklistProgettoList getChecklistProgetto(String cupId, String livello) throws DatabaseException {
		String metodo = new Object() {}.getClass().getEnclosingMethod().getName();
		try {
			List<ChecklistProgettoDto> progettiDto = getChecklistProgettoDto(cupId, livello);
			Integer rilevazioneId = getRilevazioneByCup(cupId);
			List<ProceduraCigDto> cigProcedureDto = getProcedureCigDto(rilevazioneId);
			String stato = getStatoProgetto(cupId);
			int progressivoCig = getProgressivoCig(rilevazioneId);
			ChecklistProgettoList result = progettoMapper.buildChecklistProgetto(progettiDto, cigProcedureDto, progressivoCig, stato);
			return result;
		} catch (Exception e) {
			logError(metodo, e.getMessage());
			throw new DatabaseException(e);
		}
	}
	
	public String getStatoProgetto(String cup) throws DatabaseException {
		String metodo = new Object() {}.getClass().getEnclosingMethod().getName();
		try {
			SqlParameterSource namedParameters = new MapSqlParameterSource().addValue("cup", cup);
			return jdbcTemplate.queryForObject(SELECT_STATO_PROGETTO, namedParameters, String.class);
		} catch (Exception e) {
			logError(metodo, e.getMessage());
			throw new DatabaseException(e);
		}
	}
	
	public boolean checkAziendaSanitariaProgetto(String cup, String aslCod) throws DatabaseException {
		String metodo = new Object() {}.getClass().getEnclosingMethod().getName();
		try {
			SqlParameterSource namedParameters = new MapSqlParameterSource().addValue("cup", cup).addValue("aslCod", aslCod);
			Integer count = jdbcTemplate.queryForObject(COUNT_AZIENDA_SANITARIA_PROGETTO, namedParameters, Integer.class);
			if(count == 0) { 
				return false;
			} 
			
		} catch (Exception e) {
			logError(metodo, e.getMessage());
			throw new DatabaseException(e);
		}

		return true;
	}
	
	public int getProgressivoCig(Integer rilevazioneId) throws DatabaseException {
		String metodo = new Object() {}.getClass().getEnclosingMethod().getName();
		try {
			SqlParameterSource namedParameters = new MapSqlParameterSource().addValue("rilevazioneId", rilevazioneId);
			int progressivo = jdbcTemplate.queryForObject(SELECT_PROGRESSIVO_PROGETTO, namedParameters, Integer.class);
			progressivo = progressivo + 1; //restituisco il valore da usare
			return progressivo;
		} catch (Exception e) {
			logError(metodo, e.getMessage());
			throw new DatabaseException(e);
		}
	}
	
	public Integer getRispostaId(String rispostaCod) throws DatabaseException {
		String metodo = new Object() {}.getClass().getEnclosingMethod().getName();
		try {
			SqlParameterSource namedParameters = new MapSqlParameterSource().addValue("rispostaCod", rispostaCod);
			return jdbcTemplate.queryForObject(SELECT_RISPOSTA_ID, namedParameters, Integer.class);
		} catch (Exception e) {
			logError(metodo, e.getMessage());
			throw new DatabaseException(e);
		}
	}
	
	public Integer getDescrizioneProceduraId(String descrizioneProceduraCigCod) throws DatabaseException {
		String metodo = new Object() {}.getClass().getEnclosingMethod().getName();
		try {
			SqlParameterSource namedParameters = new MapSqlParameterSource().addValue("descrizioneProceduraCigCod", descrizioneProceduraCigCod);
			return jdbcTemplate.queryForObject(SELECT_DESCRIZIONE_PROCEDURA_ID, namedParameters, Integer.class);
		} catch (Exception e) {
			logError(metodo, e.getMessage());
			throw new DatabaseException(e);
		}
	}
	
	public Integer getDescrizioneProceduraCigId(String descrizioneProceduraCigCod) throws DatabaseException {
		String metodo = new Object() {}.getClass().getEnclosingMethod().getName();
		try {
			SqlParameterSource namedParameters = new MapSqlParameterSource().addValue("descrizioneProceduraCigCod", descrizioneProceduraCigCod);
			return jdbcTemplate.queryForObject(SELECT_DESCRIZIONE_PROCEDURA_CIG_ID, namedParameters, Integer.class);
		} catch (Exception e) {
			logError(metodo, e.getMessage());
			throw new DatabaseException(e);
		}
	}
	
	public Integer getRilevazioneByCup(String cup) throws DatabaseException {
		String metodo = new Object() {}.getClass().getEnclosingMethod().getName();
		try {
			SqlParameterSource namedParameters = new MapSqlParameterSource().addValue("cup", cup);
			return jdbcTemplate.queryForObject(SELECT_RILEVAZIONE_ID, namedParameters, Integer.class);
		} catch (Exception e) {
			logError(metodo, e.getMessage());
			throw new DatabaseException(e);
		}
	}
	
	private Integer getStatoRilevazioneIdByCod(String stato) throws DatabaseException {
		String metodo = new Object() {}.getClass().getEnclosingMethod().getName();
		try {
			SqlParameterSource namedParameters = new MapSqlParameterSource().addValue("stato", stato);
			return jdbcTemplate.queryForObject(SELECT_STATO_RILEVAZIONE_ID, namedParameters, Integer.class);
		} catch (Exception e) {
			logError(metodo, e.getMessage());
			throw new DatabaseException(e);
		}
	}
	
	private Integer getUtenteIdByCf(String cf) throws DatabaseException {
		String metodo = new Object() {}.getClass().getEnclosingMethod().getName();
		try {
			SqlParameterSource namedParameters = new MapSqlParameterSource().addValue("cf", cf);
			return jdbcTemplate.queryForObject(SELECT_UTENTE_ID, namedParameters, Integer.class);
		} catch (Exception e) {
			logError(metodo, e.getMessage());
			throw new DatabaseException(e);
		}
	}
	
	@Transactional
	public void saveRisposte(String cup, String cf, String statoAttuale, ChecklistRispostaList body) throws DatabaseException {
		String metodo = new Object() {}.getClass().getEnclosingMethod().getName();
		Integer rilevazioneId = getRilevazioneByCup(cup);
		logInfo(metodo, "salva risposte per progetto: " + cup + " e rilevazione id: " + rilevazioneId);
		saveRisposte(cf, rilevazioneId, body);
		Integer utenteIdRup = getUtenteIdByCf(cf);
		changeUtente(rilevazioneId, utenteIdRup, cf);
		Integer statoRilevazioneId = getStatoRilevazioneIdByCod(body.getStato());
		changeStato(rilevazioneId, statoRilevazioneId, cf);
	}
	
	private void insertRisposte(ChecklistRisposta risposta, String cf, Integer rispostaId, Integer cigId) throws DatabaseException {

		KeyHolder keyHolder = new GeneratedKeyHolder();
		MapSqlParameterSource params =  new MapSqlParameterSource();
		
		if(cigId == null) {
			updateRisposte(risposta.getRilevazioneDomandaId(), cf);
		} else {
			updateRisposteWithCig(risposta.getRilevazioneDomandaId(), cigId, cf);
		}
//		//inserisco nuovo 
//		Integer rispostaId = null;
//		if(risposta.getEsito() != null) {
//			rispostaId = getRispostaId(risposta.getEsito());
//		}
		params = buildParamsRisposte(risposta.getRilevazioneDomandaId(), rispostaId,
				cigId, risposta.getNoteAsr(), cf);
		jdbcTemplate.update(INSERT_RISPOSTE, params, keyHolder, new String[] { "r_domanda_risposta_id" });
	}
	
	private void updateRisposte(Integer rilevazioneDomandaId, String cf) throws DatabaseException {
		KeyHolder keyHolder = new GeneratedKeyHolder();
		//aggiorno vecchio
		MapSqlParameterSource params =  new MapSqlParameterSource();
		params.addValue("id", rilevazioneDomandaId, Types.INTEGER);
		params.addValue("cf", cf, Types.VARCHAR);
		jdbcTemplate.update(UPDATE_VALIDITA_FINE_RISPOSTA, params, keyHolder, new String[] { "r_domanda_risposta_id" });
	}
	
	private void updateRisposteWithCig(Integer rilevazioneDomandaId, Integer cigId, String cf) throws DatabaseException {
		KeyHolder keyHolder = new GeneratedKeyHolder();
		//aggiorno vecchio
		MapSqlParameterSource params =  new MapSqlParameterSource();
		params.addValue("id", rilevazioneDomandaId, Types.INTEGER);
		params.addValue("cigId", cigId, Types.INTEGER);
		params.addValue("cf", cf, Types.VARCHAR);
		jdbcTemplate.update(UPDATE_VALIDITA_FINE_RISPOSTA_WITH_CIG, params, keyHolder, new String[] { "r_domanda_risposta_id" });
	}
	
	private void updateRisposteByCig(Integer cigId, String cf) throws DatabaseException {
		KeyHolder keyHolder = new GeneratedKeyHolder();
		//aggiorno vecchio
		MapSqlParameterSource params =  new MapSqlParameterSource();
		params.addValue("cigId", cigId, Types.INTEGER);
		params.addValue("cf", cf, Types.VARCHAR);
		jdbcTemplate.update(UPDATE_VALIDITA_FINE_RISPOSTA_CIG, params, keyHolder, new String[] { "r_domanda_risposta_id" });
	}
	
	private void updateCigProcedure(Integer cigId, String cf) throws DatabaseException {
		KeyHolder keyHolder = new GeneratedKeyHolder();
		//aggiorno vecchio
		MapSqlParameterSource params =  new MapSqlParameterSource();
		params.addValue("cigId", cigId, Types.INTEGER);
		params.addValue("cf", cf, Types.VARCHAR);
		jdbcTemplate.update(UPDATE_VALIDITA_FINE_PROCEDURE_CIG, params, keyHolder, new String[] { "r_cig_procedure_id" });
	}
	
	private void updateCig(Integer cigId, String cf) throws DatabaseException {
		KeyHolder keyHolder = new GeneratedKeyHolder();
		//aggiorno vecchio
		MapSqlParameterSource params =  new MapSqlParameterSource();
		params.addValue("cigId", cigId, Types.INTEGER);
		params.addValue("cf", cf, Types.VARCHAR);
		jdbcTemplate.update(UPDATE_VALIDITA_FINE_CIG, params, keyHolder, new String[] { "cig_id" });
	}
	
	private List<RispostaDto> getRisposteCupOld(Integer rilevazioneId) throws DatabaseException {
		String metodo = new Object() {}.getClass().getEnclosingMethod().getName();
		try {
			SqlParameterSource namedParameters = new MapSqlParameterSource().addValue("rilevazioneId", rilevazioneId);
			
			List<RispostaDto> result = jdbcTemplate.query(SELECT_RISPOSTE_CUP_OLD, namedParameters,
					(rs, rowNum) -> progettoMapper.buildRispostaDto(rs)); 
			
			return result;
		} catch (Exception e) {
			logError(metodo, e.getMessage());
			throw new DatabaseException(e);
		}
	}
	
	private List<RispostaDto> getRisposteCigOld(Integer rilevazioneId, Integer cigId) throws DatabaseException {
		String metodo = new Object() {}.getClass().getEnclosingMethod().getName();
		try {
			SqlParameterSource namedParameters = new MapSqlParameterSource()
					.addValue("rilevazioneId", rilevazioneId)
					.addValue("cigId", cigId);
			
			List<RispostaDto> result = jdbcTemplate.query(SELECT_RISPOSTE_CIG_OLD, namedParameters,
					(rs, rowNum) -> progettoMapper.buildRispostaDto(rs)); 
			
			return result;
		} catch (Exception e) {
			logError(metodo, e.getMessage());
			throw new DatabaseException(e);
		}
	}
	
	private List<VerificaDto> getVerificheCupOld(Integer rilevazioneId, Integer livelloId) throws DatabaseException {
		String metodo = new Object() {}.getClass().getEnclosingMethod().getName();
		try {
			SqlParameterSource namedParameters = new MapSqlParameterSource()
					.addValue("rilevazioneId", rilevazioneId)
					.addValue("livelloId", livelloId);
			
			List<VerificaDto> result = jdbcTemplate.query(SELECT_VERIFICHE_CUP_OLD, namedParameters,
					(rs, rowNum) -> progettoMapper.buildVerificaDto(rs)); 
			
			return result;
		} catch (Exception e) {
			logError(metodo, e.getMessage());
			throw new DatabaseException(e);
		}
	}
	
	private List<VerificaDto> getVerificheCigOld(Integer rilevazioneId, Integer livelloId, Integer cigId) throws DatabaseException {
		String metodo = new Object() {}.getClass().getEnclosingMethod().getName();
		try {
			SqlParameterSource namedParameters = new MapSqlParameterSource()
					.addValue("rilevazioneId", rilevazioneId)
					.addValue("livelloId", livelloId)
					.addValue("cigId", cigId);
			
			List<VerificaDto> result = jdbcTemplate.query(SELECT_VERIFICHE_CIG_OLD, namedParameters,
					(rs, rowNum) -> progettoMapper.buildVerificaDto(rs)); 
			
			return result;
		} catch (Exception e) {
			logError(metodo, e.getMessage());
			throw new DatabaseException(e);
		}
	}
	
	public void saveRisposte(String cf, Integer rilevazioneId, ChecklistRispostaList body) throws DatabaseException {
		String metodo = new Object() {}.getClass().getEnclosingMethod().getName();
		logInfo(metodo, "inserisci risposte per rilevazione id: " + rilevazioneId);
		
		//cup
		List<RispostaDto> risposteOld = getRisposteCupOld(rilevazioneId);
		for(ChecklistRisposta risposta : body.getCup()) {
			Optional<RispostaDto> rispostaOld = risposteOld.stream()
					.filter(r -> r.rilevazioneDomandaId().equals(risposta.getRilevazioneDomandaId())).findFirst();
			
			Integer rispostaId = null;
			if(risposta.getEsito() != null) {
				rispostaId = getRispostaId(risposta.getEsito());
			}
			
			String notaOld = Strings.EMPTY;
			if(rispostaOld.isPresent()) {
				notaOld = rispostaOld.get().nota() == null ? Strings.EMPTY : rispostaOld.get().nota();
			}
			String nota = risposta.getNoteAsr() == null ? Strings.EMPTY : risposta.getNoteAsr();
			
			if(rispostaOld.isEmpty() || 
				(rispostaOld.isPresent() && rispostaId != null &&
						(!rispostaOld.get().rispostaId().equals(rispostaId) || !notaOld.equals(nota)))) {
				insertRisposte(risposta, cf, rispostaId, null);
			}
		}
		
		//cig
		List<ProceduraCigDto> procedureCigDto = getProcedureCigDto(rilevazioneId);
		insertRisposteCig(body, procedureCigDto, rilevazioneId, cf);
		
		//remove deleted
		for(ProceduraCigDto proceduraCigDto : procedureCigDto) {
			updateRisposteByCig(proceduraCigDto.cigId(), cf);
			updateCigProcedure(proceduraCigDto.cigId(), cf); 
			updateCig(proceduraCigDto.cigId(), cf);
		}
	}
	
	private void insertRisposteCig(ChecklistRispostaList body, List<ProceduraCigDto> procedureCigDto, Integer rilevazioneId, String cf) throws DatabaseException {
		for(ProceduraRisposteCig proceduraRisposta : body.getCig()) {
			if(proceduraRisposta.getDescrizioneProceduraCig() == null &&
					proceduraRisposta.getNumeroCig() == null) {
				continue;
			}
			
			Predicate<ProceduraCigDto> predicate = p -> 
					p.descrizioneProceduraCigCod().equalsIgnoreCase(proceduraRisposta.getDescrizioneProceduraCig()) &&
					p.cigCod().equalsIgnoreCase(proceduraRisposta.getNumeroCig());
					
			Optional<ProceduraCigDto> proceduraCigDto = procedureCigDto.stream()
				.filter(predicate)
				.findFirst();
			
			if(proceduraCigDto.isPresent()) {
				Integer cigId = proceduraCigDto.get().cigId();
				List<RispostaDto> risposteOld = getRisposteCigOld(rilevazioneId, cigId);
				
				for(ChecklistRisposta risposta : proceduraRisposta.getRisposte()) {
					Optional<RispostaDto> rispostaOld = risposteOld.stream()
							.filter(r -> r.rilevazioneDomandaId().equals(risposta.getRilevazioneDomandaId())).findFirst();
						
					Integer rispostaId = null;
					if(risposta.getEsito() != null) {
						rispostaId = getRispostaId(risposta.getEsito());
					}
					
					String notaOld = Strings.EMPTY;
					if(rispostaOld.isPresent()) {
						notaOld = rispostaOld.get().nota() == null ? Strings.EMPTY : rispostaOld.get().nota();
					}
					String nota = risposta.getNoteAsr() == null ? Strings.EMPTY : risposta.getNoteAsr();
						
					if(rispostaOld.isEmpty() || 
							(rispostaOld.isPresent() && rispostaId != null &&
									(!rispostaOld.get().rispostaId().equals(rispostaId) || !notaOld.equals(nota)))) {
						insertRisposte(risposta, cf, rispostaId, cigId);
					}
				}
			} else {
				//pnrr_d_cig
				Integer cigId = insertCig(rilevazioneId, proceduraRisposta, cf);
				insertProcedureCig(cigId, proceduraRisposta, cf);
				
				for(ChecklistRisposta risposta : proceduraRisposta.getRisposte()) {
					Integer rispostaId = null;
					if(risposta.getEsito() != null) {
						rispostaId = getRispostaId(risposta.getEsito());
					}
					
					insertRisposte(risposta, cf, rispostaId, cigId);
				}
			}
			
			procedureCigDto.removeIf(predicate);
		}
	}
	
	private Integer insertCig(Integer rilevazioneId, ProceduraRisposteCig proceduraRisposta, String cf) throws DatabaseException {
		KeyHolder keyHolder = new GeneratedKeyHolder();
		MapSqlParameterSource params = buildParamsCig(rilevazioneId, proceduraRisposta.getNumeroCig(),
				proceduraRisposta.getNumeroCig(), proceduraRisposta.getProgressivo(), cf);
		
		jdbcTemplate.update(INSERT_CIG, params, keyHolder, new String[] { "cig_id" });
		Number key = keyHolder.getKey();
		if(key == null) {
			throw new DatabaseException("Error inserting into pnrr_d_cig table");
		}
		
		return key.intValue();
	}
	
	private void insertProcedureCig(Integer cigId, ProceduraRisposteCig proceduraRisposta, String cf) throws DatabaseException {
		KeyHolder keyHolder = new GeneratedKeyHolder();
		Integer descrizioneProceduraCigId = getDescrizioneProceduraId(proceduraRisposta.getDescrizioneProceduraCig());
		MapSqlParameterSource params = buildParamsProcedureCig(descrizioneProceduraCigId, cigId, cf);
		jdbcTemplate.update(INSERT_PROCEDURE_CIG, params, keyHolder, new String[] { "r_cig_procedure_id" });
	}
	
	@Transactional
	public void saveVerifiche(String cup, String cf, VerificaRispostaList body, String livello) throws DatabaseException {
		String metodo = new Object() {}.getClass().getEnclosingMethod().getName();
		Integer rilevazioneId = getRilevazioneByCup(cup);
		logInfo(metodo, "salva verifiche per progetto: " + cup + " e rilevazione id: " + rilevazioneId);
		Integer utenteId = getUtenteIdByCf(cf);
		saveVerifiche(cf, rilevazioneId, body, livello, utenteId);
	}

	public void saveVerifiche(String cf, Integer rilevazioneId, VerificaRispostaList body, 
			String livello, Integer utenteId) throws DatabaseException {
		String metodo = new Object() {}.getClass().getEnclosingMethod().getName();
		logInfo(metodo, "inserisci verifiche per rilevazione id: " + rilevazioneId);
		
		//cup
		Integer verificaLivelloId = getVerificaLivelloId(livello);
		List<VerificaDto> verificheOld = getVerificheCupOld(rilevazioneId, verificaLivelloId);
		for(VerificaRisposta verifica : body.getCup()) {
			Optional<VerificaDto> verificaOld = verificheOld.stream()
					.filter(r -> r.rilevazioneDomandaId().equals(verifica.getRilevazioneDomandaId())).findFirst();
			
			String verificaCod = getVerificaCod(verifica);
			if(verificaCod == null) {
				return;
			}
			
			Integer valoreVerificaId = getVerificaId(verificaCod);
			
			String notaOld = Strings.EMPTY;
			if(verificaOld.isPresent()) {
				notaOld = verificaOld.get().nota() == null ? Strings.EMPTY : verificaOld.get().nota();
			}
			
			String nota = verifica.getNoteRp() == null ? Strings.EMPTY : verifica.getNoteRp();
			
			if(verificaOld.isEmpty() || 
					(verificaOld.isPresent() && valoreVerificaId != null &&
							(!verificaOld.get().valoriVerificaId().equals(valoreVerificaId) 
									 && !notaOld.equals(nota)))) {
				insertVerifiche(rilevazioneId, verifica, cf, verificaLivelloId, utenteId);
			}
		}
		
		//cig 
		for(VerificaRispostaCig verificaCig : body.getCig()) {
			insertVerificheCig(rilevazioneId, verificaCig, cf, verificaLivelloId, utenteId);
		}
	}
	
	private void insertVerifiche(Integer rilevazioneId, VerificaRisposta verifica, String cf, Integer verificaLivelloId, Integer utenteId) throws DatabaseException {
		Integer rDomandaRispostaId = getDomandaRispostaByRilevazioneDomandaId(verifica.getRilevazioneDomandaId());
		if(rDomandaRispostaId == null) {
			return; //TODO serve qualche errore?
		}
		
		updateVerifiche(rDomandaRispostaId, verificaLivelloId, cf);
		
		//inserisco nuovo 
		insertVerifica(rilevazioneId, rDomandaRispostaId, verifica, verificaLivelloId, cf, utenteId);
	}

	private void insertVerificheCig(Integer rilevazioneId, VerificaRispostaCig verificaCig, 
			String cf, Integer verificaLivelloId, Integer utenteId) throws DatabaseException {
		Integer cigId = getProcedureCigDtoFromCodAndDescrizione(rilevazioneId, verificaCig.getNumeroCig(),
				verificaCig.getDescrizioneProceduraCig());
		
		List<VerificaDto> verificheOld = getVerificheCigOld(rilevazioneId, verificaLivelloId, cigId);
		for(VerificaRisposta verifica : verificaCig.getRisposte()) {
			
			Optional<VerificaDto> verificaOld = verificheOld.stream()
					.filter(r -> r.rilevazioneDomandaId().equals(verifica.getRilevazioneDomandaId())).findFirst();
			
			String verificaCod = getVerificaCod(verifica);
			if(verificaCod == null) {
				return;
			}
			
			Integer valoreVerificaId = getVerificaId(verificaCod);
			
			String notaOld = Strings.EMPTY;
			if(verificaOld.isPresent()) {
				notaOld = verificaOld.get().nota() == null ? Strings.EMPTY : verificaOld.get().nota();
			}
			String nota = verifica.getNoteRp() == null ? Strings.EMPTY : verifica.getNoteRp();
			
			if(verificaOld.isEmpty() || 
					(verificaOld.isPresent() && valoreVerificaId != null &&
							(!verificaOld.get().valoriVerificaId().equals(valoreVerificaId) 
									|| !notaOld.equals(nota)))) {
				MapSqlParameterSource params =  new MapSqlParameterSource();
				params.addValue("rRilevazioneDomandaId", verifica.getRilevazioneDomandaId(), Types.INTEGER);
				params.addValue("cigId", cigId, Types.INTEGER);
				
				Integer rDomandaRispostaId = jdbcTemplate.queryForObject(SELECT_DOMANDA_RISPOSTA_WITH_CIG_ID, params, Integer.class);
				
				updateVerifiche(rDomandaRispostaId, verificaLivelloId, cf);

				//inserisco nuovo 
				insertVerifica(rilevazioneId, rDomandaRispostaId, verifica, verificaLivelloId, cf, utenteId);
			}
		}
	}
	
	private void insertVerifica(Integer rilevazioneId, Integer rDomandaRispostaId, VerificaRisposta verifica, 
			Integer livelloId, String cf, Integer utenteId) throws DatabaseException {
		KeyHolder keyHolder = new GeneratedKeyHolder();
		MapSqlParameterSource params =  new MapSqlParameterSource();
		
		String verificaCod = getVerificaCod(verifica);
		if(verificaCod == null) {
			return;
		}
		
		Integer valoreVerificaId = getVerificaId(verificaCod);
		
		params = buildParamsVerifiche(livelloId, valoreVerificaId,
				rDomandaRispostaId, verifica.getNoteRp(), cf);
		jdbcTemplate.update(INSERT_R_RISPOSTE_VERIFICA, params, keyHolder, new String[] { "r_risposte_verifica_id" });

		Number key = keyHolder.getKey();
		if(key == null) {
			throw new DatabaseException("Error inserting into pnrr_r_risposte_verifica table");
		}
		
		insertUtenteVerifica(rilevazioneId, key.intValue(), utenteId, cf);
	}
	
	private String getVerificaCod(VerificaRisposta verifica) {
		String verificaCod = Strings.EMPTY;
		
		if(verifica.isEsito() == null) {
			return null;
		}
		
		verificaCod = verifica.isEsito() ?
				VerificaRilevazioneEnum.OK.getCode() : VerificaRilevazioneEnum.KO.getCode();
		return verificaCod;
	}
	
	private void updateVerifiche(Integer rDomandaRispostaId, Integer verificaLivelloId, String cf) throws DatabaseException {
		KeyHolder keyHolder = new GeneratedKeyHolder();
		//aggiorno vecchio
		MapSqlParameterSource params =  new MapSqlParameterSource();
		params.addValue("rDomandaRispostaId", rDomandaRispostaId, Types.INTEGER);
		params.addValue("verificaLivelloId", verificaLivelloId, Types.INTEGER);
		params.addValue("cf", cf, Types.VARCHAR);
		jdbcTemplate.update(UPDATE_VALIDITA_FINE_VERIFICA, params, keyHolder, new String[] { "r_risposte_verifica_id" });
	}
	
	private Integer getVerificaId(String verificaCod) throws DatabaseException {
		String metodo = new Object() {}.getClass().getEnclosingMethod().getName();
		try {
			SqlParameterSource namedParameters = new MapSqlParameterSource().addValue("verificaCod", verificaCod);
			return jdbcTemplate.queryForObject(SELECT_VERIFICA_ID, namedParameters, Integer.class);
		} catch (Exception e) {
			logError(metodo, e.getMessage());
			throw new DatabaseException(e);
		}
	}
	
	private Integer getVerificaLivelloId(String verificaLivelloCod) throws DatabaseException {
		String metodo = new Object() {}.getClass().getEnclosingMethod().getName();
		try {
			SqlParameterSource namedParameters = new MapSqlParameterSource().addValue("verificaLivelloCod", verificaLivelloCod);
			return jdbcTemplate.queryForObject(SELECT_VERIFICA_LIVELLO_ID, namedParameters, Integer.class);
		} catch (Exception e) {
			logError(metodo, e.getMessage());
			throw new DatabaseException(e);
		}
	}
	
	private Integer getDomandaRispostaByRilevazioneDomandaId(Integer rilevazioneDomandaId) throws DatabaseException {
		String metodo = new Object() {}.getClass().getEnclosingMethod().getName();
		try {
			MapSqlParameterSource params =  new MapSqlParameterSource();
			params.addValue("rRilevazioneDomandaId", rilevazioneDomandaId, Types.INTEGER);
			
			return jdbcTemplate.queryForObject(SELECT_DOMANDA_RISPOSTA_ID, params, Integer.class);
		} catch(EmptyResultDataAccessException e) {
			return null;
		} catch (Exception e) {
			logError(metodo, e.getMessage());
			throw new DatabaseException(e);
		}
	}
	
	private void changeUtente(Integer rilevazioneId, Integer utenteIdRup, String cf) {
		String metodo = new Object() {}.getClass().getEnclosingMethod().getName();
		logInfo(metodo, "cambia utente per la rilevazione id: " + rilevazioneId);
		
		KeyHolder keyHolder = new GeneratedKeyHolder();
		
		//aggiorno la validità fine
		MapSqlParameterSource params = new MapSqlParameterSource();
		params.addValue("id", rilevazioneId, Types.INTEGER);
		params.addValue("cf", cf, Types.VARCHAR);
		jdbcTemplate.update(UPDATE_VALIDITA_FINE_UTENTE_RILEVAZIONE_PROGETTO, params, keyHolder, new String[] { "r_utente_rilevazione_id" });
		//inserisco nuovo 
		params = buildParamsUtenteRilevazione(utenteIdRup, rilevazioneId, cf);
		jdbcTemplate.update(INSERT_UTENTE_RILEVAZIONE_PROGETTO, params, keyHolder, new String[] { "r_utente_rilevazione_id" });
	}

	private void insertUtenteVerifica(Integer rilevazioneId, int rRisposteVerificaId, Integer utenteId, String cf) {
		String metodo = new Object() {}.getClass().getEnclosingMethod().getName();
		logInfo(metodo, "inserisci utente di verifica " + utenteId 
				+ " per la rilevazione id: " + rilevazioneId 
				+ " per la domanda risposta " + rRisposteVerificaId);
		
		KeyHolder keyHolder = new GeneratedKeyHolder();
		
		//aggiorno la validità fine
		MapSqlParameterSource params = new MapSqlParameterSource();
		params.addValue("rRisposteVerificaId", rRisposteVerificaId, Types.INTEGER);
		params.addValue("cf", cf, Types.VARCHAR);
		jdbcTemplate.update(UPDATE_VALIDITA_FINE_UTENTE_VERIFICA_PROGETTO, params, keyHolder, new String[] { "r_utente_risposte_verifica_id" });
		
		//inserisco nuovo 
		params = new MapSqlParameterSource();
		params.addValue("rRisposteVerificaId", rRisposteVerificaId, Types.INTEGER);
		params.addValue("utenteId", utenteId, Types.INTEGER);
		params.addValue("cf", cf, Types.VARCHAR);
		jdbcTemplate.update(INSERT_UTENTE_VERIFICA_PROGETTO, params, keyHolder, new String[] { "r_utente_risposte_verifica_id" });
	}

	private void changeStato(Integer rilevazioneId, Integer statoRilevazioneId, String cf) {
		String metodo = new Object() {}.getClass().getEnclosingMethod().getName();
		logInfo(metodo, "cambia stato per la rilevazione id: " + rilevazioneId);
		
		KeyHolder keyHolder = new GeneratedKeyHolder();
		
		//aggiorno la validità fine
		MapSqlParameterSource params = new MapSqlParameterSource();
		params.addValue("id", rilevazioneId, Types.INTEGER);
		params.addValue("cf", cf, Types.VARCHAR);
		jdbcTemplate.update(UPDATE_VALIDITA_FINE_STATO_RILEVAZIONE_PROGETTO, params, keyHolder, new String[] { "r_stato_rilevazione_id" });
		//inserisco nuovo 
		params = buildParamsStatoRilevazione(statoRilevazioneId, rilevazioneId, cf);
		jdbcTemplate.update(INSERT_STATO_RILEVAZIONE_PROGETTO, params, keyHolder, new String[] { "r_stato_rilevazione_id" });
	}

	private MapSqlParameterSource buildParamsProcedureCig(Integer descrizioneProceduraCigId, Integer cigId, 
			String cf) {
		MapSqlParameterSource params = new MapSqlParameterSource();

		params.addValue("descrizioneProceduraCigId", descrizioneProceduraCigId, Types.INTEGER);
		params.addValue("cigId", cigId, Types.INTEGER);
		params.addValue("cf", cf, Types.VARCHAR);
		
		return params;
	}
	
	private MapSqlParameterSource buildParamsCig(Integer rilevazioneId, String cigCod, 
			String cigDesc, Integer progressivo, String cf) {
		MapSqlParameterSource params = new MapSqlParameterSource();

		params.addValue("rilevazioneId", rilevazioneId, Types.INTEGER);
		params.addValue("cigCod", cigCod, Types.VARCHAR);
		params.addValue("cigDesc", cigDesc, Types.VARCHAR);
		params.addValue("progressivo", progressivo, Types.INTEGER);
		params.addValue("cf", cf, Types.VARCHAR);
		
		return params;
	}
	
	private MapSqlParameterSource buildParamsRisposte(Integer rRilevazioneDomandaId, Integer rispostaId, 
			Integer cigId, String nota, String cf) {
		MapSqlParameterSource params = new MapSqlParameterSource();

		params.addValue("rRilevazioneDomandaId", rRilevazioneDomandaId, Types.INTEGER);
		params.addValue("rispostaId", rispostaId, Types.INTEGER);
		params.addValue("cigId", cigId, Types.INTEGER);
		params.addValue("nota", nota, Types.VARCHAR);
		params.addValue("cf", cf, Types.VARCHAR);
		
		return params;
	}
	
	private MapSqlParameterSource buildParamsUtenteRilevazione(Integer utenteIdRup, 
			Integer rilevazioneId, String cf) {
		MapSqlParameterSource params = new MapSqlParameterSource();

		params.addValue("utenteIdRup", utenteIdRup, Types.INTEGER);
		params.addValue("rilevazioneId", rilevazioneId, Types.INTEGER);
		params.addValue("cf", cf, Types.VARCHAR);
		
		return params;
	}
	
	private MapSqlParameterSource buildParamsStatoRilevazione(Integer statoRilevazioneId, 
			Integer rilevazioneId, String cf) {
		MapSqlParameterSource params = new MapSqlParameterSource();

		params.addValue("statoRilevazioneId", statoRilevazioneId, Types.INTEGER);
		params.addValue("rilevazioneId", rilevazioneId, Types.INTEGER);
		params.addValue("cf", cf, Types.VARCHAR);
		
		return params;
	}
	
	private MapSqlParameterSource buildParamsVerifiche(Integer verificaId, Integer valoriVerificaId, 
			Integer rDomandaRispostaId, String nota, String cf) {
		MapSqlParameterSource params = new MapSqlParameterSource();

		params.addValue("verificaId", verificaId, Types.INTEGER);
		params.addValue("valoriVerificaId", valoriVerificaId, Types.INTEGER);
		params.addValue("rDomandaRispostaId", rDomandaRispostaId, Types.INTEGER);
		params.addValue("nota", nota, Types.VARCHAR);
		params.addValue("cf", cf, Types.VARCHAR);
		
		return params;
	}
	
}
