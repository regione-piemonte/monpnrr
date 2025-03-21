/*
* SPDX-FileCopyrightText: (C) Copyright 2025 Regione Piemonte
*
* SPDX-License-Identifier: EUPL-1.2 
*/
package it.csi.monpnrr.monpnrrbe.integration.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Service;

import it.csi.monpnrr.monpnrrbe.exception.DatabaseException;
import it.csi.monpnrr.monpnrrbe.util.Constants;
import it.csi.monpnrr.monpnrrbe.util.log.AbstractLogger;

@Service
public class ParametriDao extends AbstractLogger {

	private static final String GET_PARAMETRO ="""
			select pcp.parametro_valore
			from pnrr_c_parametro pcp 
			where now()::date BETWEEN pcp.validita_inizio::date and COALESCE(pcp.validita_fine::date, now()::date) 
			and pcp.data_cancellazione is null 
			and pcp.parametro_cod = :codice;
		""";
	
	@Autowired
	private NamedParameterJdbcTemplate jdbcTemplate;

	public Integer getParametroByCod(String codice) throws DatabaseException {
		String metodo = new Object() {}.getClass().getEnclosingMethod().getName();
		try {
			SqlParameterSource namedParameters = new MapSqlParameterSource().addValue("codice", codice);
			
			return jdbcTemplate.queryForObject(GET_PARAMETRO, namedParameters, Integer.class);
		} catch (EmptyResultDataAccessException e) {
			return Constants.DATA_PARAMETRO_DEFAULT;
		} catch (Exception e) {
			logError(metodo, e.getMessage());
			throw new DatabaseException(e);
		}
	}
}
