/*
* SPDX-FileCopyrightText: (C) Copyright 2025 Regione Piemonte
*
* SPDX-License-Identifier: EUPL-1.2 
*/
package it.csi.monpnrr.monpnrrbe.integration.dao;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Service;

import it.csi.monpnrr.monpnrrbe.api.dto.CambiStato;
import it.csi.monpnrr.monpnrrbe.api.dto.DateModifica;
import it.csi.monpnrr.monpnrrbe.api.dto.Decodifica;
import it.csi.monpnrr.monpnrrbe.exception.DatabaseException;
import it.csi.monpnrr.monpnrrbe.integration.dao.dto.CambiStatoDto;
import it.csi.monpnrr.monpnrrbe.integration.dao.dto.mapper.CambiStatoMapper;
import it.csi.monpnrr.monpnrrbe.util.enumeration.ProfiloEnum;

@Service
public class DecodificheDao extends UtilDao {

	private static final String GET_PROCEDURE ="""
			select pddpc.descrizione_procedura_cig_cod, pddpc.descrizione_procedura_cig_desc 
			from pnrr_d_descrizione_procedura_cig pddpc 
			where pddpc.validita_fine is null 
			and pddpc.data_cancellazione is null
			order by pddpc.descrizione_procedura_cig_cod;
			""";
	
	private static final String GET_RISPOSTE ="""
			select pdr.risposta_cod, pdr.risposta_desc 
			from pnrr_d_risposta pdr 
			where pdr.validita_fine is null 
			and pdr.data_cancellazione is null
			order by pdr.ordinamento;
			""";	
	
	private static final String GET_CAMBISTATO ="""
			select pdsr.stato_rilevazione_cod AS stato_da_cod,
				pdsr.stato_rilevazione_desc AS stato_da_desc,
				pdsr.ordinamento AS stato_da_ordinamento,
				pdsr_a.stato_rilevazione_cod AS stato_a_cod,
				pdsr_a.stato_rilevazione_desc AS stato_a_desc,
				pdsr_a.ordinamento AS stato_a_ordinamento
			from pnrr_d_stato_rilevazione pdsr
			left join pnrr_r_stato_rilevazione_passaggi prsrp ON pdsr.stato_rilevazione_id = prsrp.stato_rilevazione_id_da
			left join pnrr_d_stato_rilevazione pdsr_a ON prsrp.stato_rilevazione_id_a = pdsr_a.stato_rilevazione_id
			where pdsr.validita_fine is null 
			and pdsr.data_cancellazione is null
			and prsrp.validita_fine is null 
			and prsrp.data_cancellazione is null
			and pdsr_a.validita_fine is null 
			and pdsr_a.data_cancellazione is null
			order by pdsr.stato_rilevazione_id;
		""";
	
	private static final String GET_AZIENDE_SANITARIE ="""
			select pda.asl_cod, pda.asl_azienda_desc 
			from pnrr_d_asl pda
			where pda.validita_fine is null 
			and pda.data_cancellazione is null
			order by pda.asl_cod;
		""";
	
	private static final String GET_CHECKLIST ="""
			select pdc.checklist_cod, pdc.checklist_desc 
			from pnrr_d_checklist pdc 
			where pdc.validita_fine is null 
			and pdc.data_cancellazione is null
			order by pdc.checklist_cod;
		""";
	
	private static final String GET_RUA_RUP ="""
			select pdu.cf, pdu.cognome, pdu.nome 
			from pnrr_d_utente pdu
			join pnrr_r_utente_ruolo prur on pdu.utente_id = prur.utente_id 
			join pnrr_d_ruolo pdr on pdr.ruolo_id = prur.ruolo_id 
			where pdu.validita_fine is null 
			and pdu.data_cancellazione is null
			and ruolo_cod = :ruolo
			order by pdu.cognome, pdu.nome;
		""";
	
	@Autowired
	private CambiStatoMapper cambiStatoMapper;
	
	@Autowired
	private NamedParameterJdbcTemplate jdbcTemplate;
	
	public List<Decodifica> getProcedure() throws DatabaseException {
		String metodo = new Object() {}.getClass().getEnclosingMethod().getName();
		try {
			List<Decodifica> result = jdbcTemplate.query(GET_PROCEDURE,  
					(rs, rowNum) -> buildDecodifica(rs.getString("descrizione_procedura_cig_cod"), rs.getString("descrizione_procedura_cig_desc"))); 
			return result;
		} catch (Exception e) {
			logError(metodo, e.getMessage());
			throw new DatabaseException(e);
		}
	}	
	
	public List<Decodifica> getRisposte() throws DatabaseException {
		String metodo = new Object() {}.getClass().getEnclosingMethod().getName();
		try {
			List<Decodifica> result = jdbcTemplate.query(GET_RISPOSTE,  
					(rs, rowNum) -> buildDecodifica(rs.getString("risposta_cod"), rs.getString("risposta_desc"))); 
			return result;
		} catch (Exception e) {
			logError(metodo, e.getMessage());
			throw new DatabaseException(e);
		}
	}	
	
	public List<CambiStatoDto> getCambiStatoDto() throws DatabaseException {
		String metodo = new Object() {}.getClass().getEnclosingMethod().getName();
		try {
			List<CambiStatoDto> cambiStatoDto = jdbcTemplate.query(GET_CAMBISTATO,  
					(rs, rowNum) -> cambiStatoMapper.buildCambiStatoDto(rs.getString("stato_da_cod"),
							rs.getString("stato_da_desc"), rs.getInt("stato_da_ordinamento"),
							rs.getString("stato_a_cod"), rs.getString("stato_a_desc"), rs.getInt("stato_a_ordinamento")));
			return cambiStatoDto;
		} catch (Exception e) {
			logError(metodo, e.getMessage());
			throw new DatabaseException(e);
		}
	}	
	
	public List<CambiStato> getCambiStato() throws DatabaseException {
		String metodo = new Object() {}.getClass().getEnclosingMethod().getName();
		try {
			List<CambiStatoDto> cambiStatoDto = getCambiStatoDto();
			List<CambiStato> cambiStato = cambiStatoMapper.buildCambiStatoList(cambiStatoDto);
			return cambiStato;
		} catch (Exception e) {
			logError(metodo, e.getMessage());
			throw new DatabaseException(e);
		}
	}	
	
	public List<Decodifica> getAziendeSanitarie() throws DatabaseException {
		String metodo = new Object() {}.getClass().getEnclosingMethod().getName();
		try {
			List<Decodifica> result = jdbcTemplate.query(GET_AZIENDE_SANITARIE,  
					(rs, rowNum) -> buildDecodifica(rs.getString("asl_cod"), rs.getString("asl_azienda_desc"))); 
			return result;
		} catch (Exception e) {
			logError(metodo, e.getMessage());
			throw new DatabaseException(e);
		}
	}	
	
	public List<Decodifica> getChecklist() throws DatabaseException {
		String metodo = new Object() {}.getClass().getEnclosingMethod().getName();
		try {
			List<Decodifica> result = jdbcTemplate.query(GET_CHECKLIST,  
					(rs, rowNum) -> buildDecodifica(rs.getString("checklist_cod"), rs.getString("checklist_desc"))); 
			return result;
		} catch (Exception e) {
			logError(metodo, e.getMessage());
			throw new DatabaseException(e);
		}
	}	
	
	public List<Decodifica> getUtente(String ruolo) throws DatabaseException {
		String metodo = new Object() {}.getClass().getEnclosingMethod().getName();
		try {
			SqlParameterSource namedParameters = new MapSqlParameterSource().addValue("ruolo", ruolo);
			List<Decodifica> result = jdbcTemplate.query(GET_RUA_RUP, namedParameters, 
					(rs, rowNum) -> buildUtente(rs.getString("cf"), rs.getString("nome"), rs.getString("cognome"))); 
			return result;
		} catch (Exception e) {
			logError(metodo, e.getMessage());
			throw new DatabaseException(e);
		}
	}	
	
	public DateModifica getDateModifica(ProfiloEnum profilo) throws DatabaseException {
		String metodo = new Object() {}.getClass().getEnclosingMethod().getName();
		try {
			return switch (profilo) {
			    case RUP -> new DateModifica();
			    case RUA -> getDateModificaRup(new DateModifica());
			    case SUPER_USER -> getDateModificaForSuperUser();
			    case AUTOCONTROLLO_2 -> getDateModificaForRPLiv2();
			    case AUTOCONTROLLO_3 -> getDateModificaForRPLiv3();
			    default -> null;
			};

		} catch (Exception e) {
			logError(metodo, e.getMessage());
			throw new DatabaseException(e);
		}
	}
	    
	private Decodifica buildDecodifica(String codice, String descrizione) {
		Decodifica result = new Decodifica();
		result.setCodice(codice);
		result.setDescrizione(descrizione);
		return result;
	}
	
	private Decodifica buildUtente(String cf, String nome, String cognome) {
		Decodifica result = new Decodifica();
		result.setCodice(cf);
		String descrizione = cognome.concat(" ").concat(nome);
		result.setDescrizione(descrizione);
		return result;
	}
	
	
	private DateModifica getDateModificaForRPLiv2() throws DatabaseException {
		DateModifica dateModifica = new DateModifica();
    	getDateModificaRup(dateModifica);
    	getDateAutoControlloLiv2(dateModifica);
    	return dateModifica;
	}
	
	private DateModifica getDateModificaForRPLiv3() throws DatabaseException {
		DateModifica dateModifica = new DateModifica();
    	getDateModificaRup(dateModifica);
    	getDateAutoControlloLiv3(dateModifica);
    	return dateModifica;
	}
	
	private DateModifica getDateModificaForSuperUser() throws DatabaseException {
		DateModifica dateModifica = new DateModifica();
    	getDateModificaRup(dateModifica);
    	getDateAutoControlloLiv2(dateModifica);
    	getDateAutoControlloLiv3(dateModifica);
    	return dateModifica;
	}
	
}
