/*
* SPDX-FileCopyrightText: (C) Copyright 2025 Regione Piemonte
*
* SPDX-License-Identifier: EUPL-1.2 
*/
package it.csi.monpnrr.monpnrrbe.integration.dao;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;

import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Service;

import it.csi.monpnrr.monpnrrbe.api.dto.DateModifica;
import it.csi.monpnrr.monpnrrbe.exception.DatabaseException;
import it.csi.monpnrr.monpnrrbe.integration.dao.dto.FiltersDto;
import it.csi.monpnrr.monpnrrbe.integration.dao.util.SQLStatements;
import it.csi.monpnrr.monpnrrbe.util.Constants;
import it.csi.monpnrr.monpnrrbe.util.Util;
import it.csi.monpnrr.monpnrrbe.util.enumeration.OrderQueryEnum;
import it.csi.monpnrr.monpnrrbe.util.enumeration.ParametroEnum;
import it.csi.monpnrr.monpnrrbe.util.enumeration.ProfiloEnum;

@Service
public class UtilDao extends SQLStatements {
	
	private static final String ASCENDING = "asc";
	private static final String DESCENDING = "desc";
	
	private static final String OR = " or ";
	private static final String AND = " and ";
	
	@Autowired
	private ParametriDao parametriDao;

	protected String addOrdering(FiltersDto filtersDto) {
		String orderBy = Strings.EMPTY;
		boolean descendingBoolean = false;
		
		if(filtersDto == null) {
			orderBy = OrderQueryEnum.CUP.name();
			descendingBoolean = false;
			
			return getOrdering(descendingBoolean, orderBy);
		} 
			
		if(!Util.isValorizzato(filtersDto.orderBy())) {
			orderBy = OrderQueryEnum.CUP.name();
		} else {
			orderBy = filtersDto.orderBy();
		}
		
		if(filtersDto.descending() == null) {
			descendingBoolean = false;
		} else {
			descendingBoolean = filtersDto.descending();
		}
        
		return getOrdering(descendingBoolean, orderBy);
	}
	
	private String getOrdering(boolean descendingBoolean, String orderBy) {
		String descending = descendingBoolean ? DESCENDING : ASCENDING;
		OrderQueryEnum order = OrderQueryEnum.fromName(orderBy);
		String ordering = " order by " + order.getCode() + " " + descending;
		return ordering;
	}
	
	protected String addPagination(FiltersDto filtersDto) throws DatabaseException {
		if(filtersDto == null) {
			return Strings.EMPTY;
		}
		
		if(filtersDto.rowPerPage() == null || filtersDto.pageNumber() == null) {
			return Strings.EMPTY;
		}
		
		if(filtersDto.pageNumber().compareTo(BigDecimal.ZERO) <= 0){
			throw new DatabaseException("The pageNumber filter must be equal or greater than one");
		}

		BigDecimal pageNumber = filtersDto.pageNumber().subtract(BigDecimal.ONE);
		String pagination = " limit " + filtersDto.rowPerPage().toString() + " offset " + 
				(filtersDto.rowPerPage().multiply(pageNumber)).toString(); 
		return pagination;
	}
	
	private String addFilterEquals(String key, String code, String value, MapSqlParameterSource namedParameters) {
		if(!Util.isValorizzato(value)) {
			return Strings.EMPTY;
		}
		
		namedParameters.addValue(code, value);
		String filtering = " and " + key + " = :" + code;
		return filtering;
	}
	
	private String addFilterLike(String key, String code, String value, MapSqlParameterSource namedParameters) {
		if(!Util.isValorizzato(value)) {
			return Strings.EMPTY;
		}
		
		String newValue = "%".concat(value).concat("%");
		namedParameters.addValue(code, newValue);
		
		String filtering = " and " + key + " LIKE :" + code;
		return filtering;
	}
	
	private String addFilterGreaterThan(String key, String code, String value, MapSqlParameterSource namedParameters) {
		namedParameters.addValue(code, value);
		String filtering = key + "::date >= :" + code + "::date";
		return filtering;
	}
	
	private String addFilterLessThan(String key, String code, String value, MapSqlParameterSource namedParameters) {
		namedParameters.addValue(code, value);
		String filtering = key + "::date <= :" + code + "::date";
		return filtering;
	}
	
	private String addFilterBetween(String key, String codeDa, String valueDa, 
			String codeA, String valueA, MapSqlParameterSource namedParameters) {
		namedParameters.addValue(codeDa, valueDa);
		namedParameters.addValue(codeA, valueA);

		String filtering = key + "::date between :" + codeDa + "::date and :" + codeA + "::date";
		return filtering;
	}
	
	private String addFilterBetween(String key, String codeDa, String codeA,
			Integer ultimaModifica, MapSqlParameterSource namedParameters) {
		Date now = new Date();
		String valueA = Util.fromDateToString(now, Constants.FORMATTER_USA);
		
		Date ultimaModificaDate = new Date();
		Calendar c = Calendar.getInstance(); 
		c.setTime(ultimaModificaDate); 
		c.add(Calendar.DATE, -ultimaModifica);
		ultimaModificaDate = c.getTime();
		
		String valueDa = Util.fromDateToString(ultimaModificaDate, Constants.FORMATTER_USA);
			
		namedParameters.addValue(codeDa, valueDa);
		namedParameters.addValue(codeA, valueA);
		
		String filtering = key + "::date between :" + codeDa + "::date and :" + codeA + "::date";
		return filtering;
	}
	
	protected boolean isLiv2FilterEnabled(FiltersDto filtersDto) {
		return filtersDto.verificatoLiv2() != null && filtersDto.verificatoLiv2();
	}
	
	protected boolean isLiv3FilterEnabled(FiltersDto filtersDto) {
		return filtersDto.verificatoLiv3() != null && filtersDto.verificatoLiv3();
	}
	
	protected boolean isModificaRupDopoVerificaFilterEnabled(FiltersDto filtersDto) {
		return filtersDto.modificatoRupDopoVerifica() != null && filtersDto.modificatoRupDopoVerifica();
	}
	
	protected String addProgettiFilters(FiltersDto filtersDto, MapSqlParameterSource namedParameters) {
		String filters = Strings.EMPTY;
		
		if(filtersDto == null) {
			return filters;
		}
		
		filters += addFilterEquals("pda.asl_cod", Constants.AZIENDA_QUERY_CODE, filtersDto.aziendaSanitaria(), namedParameters);
		filters += addFilterLike("pdp.id_cup", Constants.CUP_QUERY_CODE, filtersDto.cup(), namedParameters);
		filters += addFilterEquals("rua.cf", Constants.RUA_QUERY_CODE, filtersDto.rua(), namedParameters);
		filters += addFilterEquals("rup.cf", Constants.RUP_QUERY_CODE, filtersDto.rup(), namedParameters);
		filters += addFilterEquals("pdc.checklist_cod", Constants.CHECKLIST_QUERY_CODE, filtersDto.checklist(), namedParameters);
		filters += addFilterEquals("pdsr.stato_rilevazione_cod", Constants.STATO_QUERY_CODE, filtersDto.stato(), namedParameters);
		
		return filters;
	}
	
	protected String addProgettiFiltersASR(FiltersDto filtersDto, MapSqlParameterSource namedParameters) throws DatabaseException {
		String filters = Strings.EMPTY;
		
		if(filtersDto == null || getDefaultDates(filtersDto, ProfiloEnum.RUA) ||
				(!Util.isValorizzato(filtersDto.modificaRupDa()) && !Util.isValorizzato(filtersDto.modificaRupA()))) {
			Integer ultimaModificaRup = parametriDao.getParametroByCod(ParametroEnum.ULTIMA_MODIFICA_RUP.getCode());

			filters += " and (".concat(addFilterBetween("ultima_modifica.data_modifica", Constants.DATA_MODIFICA_DA_QUERY_CODE,
					Constants.DATA_MODIFICA_A_QUERY_CODE, ultimaModificaRup, namedParameters)).concat(") ");
		} else {
			filters += " and (".concat(addUltimaModificaFilters(filtersDto, namedParameters)).concat(") ");
		}
		
		return filters;
	}
	
	protected String addProgettiFiltersRPSuperUser(FiltersDto filtersDto, MapSqlParameterSource namedParameters) throws DatabaseException {
		String filters = Strings.EMPTY;
		
		if(filtersDto == null || getDefaultDates(filtersDto, ProfiloEnum.SUPER_USER) ||
				(!Util.isValorizzato(filtersDto.modificaRupDa()) && !Util.isValorizzato(filtersDto.modificaRupA())) &&
				!isLiv2FilterEnabled(filtersDto) && !isLiv3FilterEnabled(filtersDto)) {
			Integer ultimaModificaRup = parametriDao.getParametroByCod(ParametroEnum.ULTIMA_MODIFICA_RUP.getCode());
			Integer ultimaVerificaLiv2 = parametriDao.getParametroByCod(ParametroEnum.ULTIMA_VERIFICA_LIV2.getCode());
			Integer ultimaVerificaLiv3 = parametriDao.getParametroByCod(ParametroEnum.ULTIMA_VERIFICA_LIV3.getCode());
			
			filters += " and (".concat(addFilterBetween("ultima_modifica.data_modifica", Constants.DATA_MODIFICA_DA_QUERY_CODE,
					Constants.DATA_MODIFICA_A_QUERY_CODE, ultimaModificaRup, namedParameters)).concat(OR);
			
			filters += addFilterBetween("ultima_verifica_liv2.data_modifica", Constants.DATA_VERIFICA2_DA_QUERY_CODE, 
					Constants.DATA_VERIFICA2_A_QUERY_CODE, ultimaVerificaLiv2, namedParameters).concat(OR);
			
			filters += addFilterBetween("ultima_verifica_liv3.data_modifica", Constants.DATA_VERIFICA3_DA_QUERY_CODE, 
					Constants.DATA_VERIFICA3_A_QUERY_CODE, ultimaVerificaLiv3, namedParameters).concat(") ");
			
		} else {
			filters += AND;
			
			String ultimaModifica = addUltimaModificaFilters(filtersDto, namedParameters);
			filters += ultimaModifica;
			filters += Strings.isEmpty(ultimaModifica) ? Strings.EMPTY : AND;
					
			String ultimaVerificaLiv2 = addUltimaVerificaLiv2Filters(filtersDto, namedParameters);
			filters += ultimaVerificaLiv2;
			filters += Strings.isEmpty(ultimaVerificaLiv2) ? Strings.EMPTY : AND;
			
			String ultimaVerificaLiv3 = addUltimaVerificaLiv3Filters(filtersDto, namedParameters);
			filters += ultimaVerificaLiv3;

			if(Strings.isEmpty(ultimaVerificaLiv3)) { 
				filters = filters.substring(0, filters.length() - AND.length());
			}
		}
		
		return filters;
	}
	
	protected String addProgettiFiltersRPLiv2(FiltersDto filtersDto, MapSqlParameterSource namedParameters) throws DatabaseException {
		String filters = Strings.EMPTY;
		
		if(filtersDto == null || getDefaultDates(filtersDto, ProfiloEnum.AUTOCONTROLLO_2) ||
				(!Util.isValorizzato(filtersDto.modificaRupDa()) && !Util.isValorizzato(filtersDto.modificaRupA())) &&
				!isLiv2FilterEnabled(filtersDto) && !isModificaRupDopoVerificaFilterEnabled(filtersDto)) {
			Integer ultimaModificaRup = parametriDao.getParametroByCod(ParametroEnum.ULTIMA_MODIFICA_RUP.getCode());
			Integer ultimaVerificaLiv = parametriDao.getParametroByCod(ParametroEnum.ULTIMA_VERIFICA_LIV2.getCode());
			
			filters += " and (".concat(addFilterBetween("ultima_modifica.data_modifica", Constants.DATA_MODIFICA_DA_QUERY_CODE,
					Constants.DATA_MODIFICA_A_QUERY_CODE, ultimaModificaRup, namedParameters)).concat(OR);
			
			filters += addFilterBetween("ultima_verifica_liv2.data_modifica", Constants.DATA_VERIFICA_DA_QUERY_CODE, 
					Constants.DATA_VERIFICA_A_QUERY_CODE, ultimaVerificaLiv, namedParameters).concat(") ");
			
		} else {
			filters += AND;
			
			String ultimaModifica = addUltimaModificaFilters(filtersDto, namedParameters);
			filters += ultimaModifica;
			filters += Strings.isEmpty(ultimaModifica) ? Strings.EMPTY : AND;
					
			String ultimaVerificaLiv2 = addUltimaVerificaLiv2Filters(filtersDto, namedParameters);
			filters += ultimaVerificaLiv2;
			filters += Strings.isEmpty(ultimaVerificaLiv2) ? Strings.EMPTY : AND;
			
			String modificaRupDopoVerifica = addModificaRupDopoVerificaFilters(filtersDto, namedParameters, Constants.LIV2);
			filters += modificaRupDopoVerifica;

			if(Strings.isEmpty(modificaRupDopoVerifica)) { 
				filters = filters.substring(0, filters.length() - AND.length());
			}
		}
		
		return filters;
	}
	
	protected String addProgettiFiltersRPLiv3(FiltersDto filtersDto, MapSqlParameterSource namedParameters) throws DatabaseException {
		String filters = Strings.EMPTY;
		
		if(filtersDto == null || getDefaultDates(filtersDto, ProfiloEnum.AUTOCONTROLLO_3) ||
				(!Util.isValorizzato(filtersDto.modificaRupDa()) && !Util.isValorizzato(filtersDto.modificaRupA())) &&
				!isLiv3FilterEnabled(filtersDto) && !isModificaRupDopoVerificaFilterEnabled(filtersDto)) {
			Integer ultimaModificaRup = parametriDao.getParametroByCod(ParametroEnum.ULTIMA_MODIFICA_RUP.getCode());
			Integer ultimaVerificaLiv = parametriDao.getParametroByCod(ParametroEnum.ULTIMA_VERIFICA_LIV3.getCode());
			
			filters += " and (".concat(addFilterBetween("ultima_modifica.data_modifica", Constants.DATA_MODIFICA_DA_QUERY_CODE,
					Constants.DATA_MODIFICA_A_QUERY_CODE, ultimaModificaRup, namedParameters)).concat(OR);
			
			filters += addFilterBetween("ultima_verifica_liv3.data_modifica", Constants.DATA_VERIFICA_DA_QUERY_CODE, 
					Constants.DATA_VERIFICA_A_QUERY_CODE, ultimaVerificaLiv, namedParameters).concat(") ");
			
		} else {
			filters += AND;
			
			String ultimaModifica = addUltimaModificaFilters(filtersDto, namedParameters);
			filters += ultimaModifica;
			filters += Strings.isEmpty(ultimaModifica) ? Strings.EMPTY : AND;
			
			String ultimaVerificaLiv3 = addUltimaVerificaLiv3Filters(filtersDto, namedParameters);
			filters += ultimaVerificaLiv3;
			filters += Strings.isEmpty(ultimaVerificaLiv3) ? Strings.EMPTY : AND;
			
			String modificaRupDopoVerifica = addModificaRupDopoVerificaFilters(filtersDto, namedParameters, Constants.LIV3);
			filters += modificaRupDopoVerifica;

			if(Strings.isEmpty(modificaRupDopoVerifica)) { 
				filters = filters.substring(0, filters.length() - AND.length());
			}
		}
		
		return filters;
	}
	
	private String addUltimaModificaFilters(FiltersDto filtersDto, MapSqlParameterSource namedParameters) {
		String filters = Strings.EMPTY;
		
		if(Util.isValorizzato(filtersDto.modificaRupDa()) && Util.isValorizzato(filtersDto.modificaRupA())) {
			filters += addFilterBetween("ultima_modifica.data_modifica", Constants.DATA_MODIFICA_DA_QUERY_CODE, 
					filtersDto.modificaRupDa(), Constants.DATA_MODIFICA_A_QUERY_CODE,
					filtersDto.modificaRupA(), namedParameters);
		} else if(Util.isValorizzato(filtersDto.modificaRupDa()) && !Util.isValorizzato(filtersDto.modificaRupA())) {
			filters += addFilterGreaterThan("ultima_modifica.data_modifica", Constants.DATA_MODIFICA_DA_QUERY_CODE, 
					filtersDto.modificaRupDa(), namedParameters);
		} else if(!Util.isValorizzato(filtersDto.modificaRupDa()) && Util.isValorizzato(filtersDto.modificaRupA())) {
			filters += addFilterLessThan("ultima_modifica.data_modifica", Constants.DATA_MODIFICA_A_QUERY_CODE, 
					filtersDto.modificaRupA(), namedParameters);
		}
		
		return filters;
	}
	
	private String addUltimaVerificaLiv2Filters(FiltersDto filtersDto, 
			MapSqlParameterSource namedParameters) throws DatabaseException {
		String filters = Strings.EMPTY;
		
		if(!isLiv2FilterEnabled(filtersDto)) {
			return filters;
		}
		
		if(Util.isValorizzato(filtersDto.modificaVerifica2Da()) && Util.isValorizzato(filtersDto.modificaVerifica2A())) {
			filters += addFilterBetween("ultima_verifica_liv2.data_modifica", Constants.DATA_VERIFICA2_DA_QUERY_CODE, 
					filtersDto.modificaVerifica2Da(), Constants.DATA_VERIFICA2_A_QUERY_CODE,
					filtersDto.modificaVerifica2A(), namedParameters);
		} else if(Util.isValorizzato(filtersDto.modificaVerifica2Da()) && !Util.isValorizzato(filtersDto.modificaVerifica2A())) {
			filters += addFilterGreaterThan("ultima_verifica_liv2.data_modifica", Constants.DATA_VERIFICA2_DA_QUERY_CODE, 
					filtersDto.modificaVerifica2Da(), namedParameters);
		} else if(!Util.isValorizzato(filtersDto.modificaVerifica2Da()) && Util.isValorizzato(filtersDto.modificaVerifica2A())) {
			filters += addFilterLessThan("ultima_verifica_liv2.data_modifica", Constants.DATA_VERIFICA2_A_QUERY_CODE, 
					filtersDto.modificaVerifica2A(), namedParameters);
		} else {
			Integer ultimaVerificaLiv = parametriDao.getParametroByCod(ParametroEnum.ULTIMA_VERIFICA_LIV2.getCode());
			
			filters += addFilterBetween("ultima_verifica_liv2.data_modifica", Constants.DATA_VERIFICA2_DA_QUERY_CODE, 
					Constants.DATA_VERIFICA2_A_QUERY_CODE, ultimaVerificaLiv, namedParameters);
		}
		
		return filters;
	}
	
	private String addUltimaVerificaLiv3Filters(FiltersDto filtersDto, 
			MapSqlParameterSource namedParameters) throws DatabaseException {
		String filters = Strings.EMPTY;
		
		if(!isLiv3FilterEnabled(filtersDto)) {
			return filters;
		}
		
		if(Util.isValorizzato(filtersDto.modificaVerifica3Da()) && Util.isValorizzato(filtersDto.modificaVerifica3A())) {
			filters += addFilterBetween("ultima_verifica_liv3.data_modifica", Constants.DATA_VERIFICA3_DA_QUERY_CODE, 
					filtersDto.modificaVerifica3Da(), Constants.DATA_VERIFICA3_A_QUERY_CODE,
					filtersDto.modificaVerifica3A(), namedParameters);
		} else if(Util.isValorizzato(filtersDto.modificaVerifica3Da()) && !Util.isValorizzato(filtersDto.modificaVerifica3A())) {
			filters += addFilterGreaterThan("ultima_verifica_liv3.data_modifica", Constants.DATA_VERIFICA3_DA_QUERY_CODE, 
					filtersDto.modificaVerifica3Da(), namedParameters);
		} else if(!Util.isValorizzato(filtersDto.modificaVerifica3Da()) && Util.isValorizzato(filtersDto.modificaVerifica3A())) {
			filters += addFilterLessThan("ultima_verifica_liv3.data_modifica", Constants.DATA_VERIFICA3_A_QUERY_CODE, 
					filtersDto.modificaVerifica3A(), namedParameters);
		} else {
			Integer ultimaVerificaLiv = parametriDao.getParametroByCod(ParametroEnum.ULTIMA_VERIFICA_LIV3.getCode());
			
			filters += addFilterBetween("ultima_verifica_liv3.data_modifica", Constants.DATA_VERIFICA3_DA_QUERY_CODE, 
					Constants.DATA_VERIFICA3_A_QUERY_CODE, ultimaVerificaLiv, namedParameters);
		}
		
		return filters;
	}
	
	private String addModificaRupDopoVerificaFilters(FiltersDto filtersDto,
			MapSqlParameterSource namedParameters, String livello) throws DatabaseException {
		String filters = Strings.EMPTY;
		
		if(!isModificaRupDopoVerificaFilterEnabled(filtersDto)) {
			return filters;
		}
		
		if(livello.equals(Constants.LIV2)) {
			filters += " ultima_modifica.data_modifica > ultima_verifica_liv2.data_modifica and ";
		} else if(livello.equals(Constants.LIV3)) {
			filters += " ultima_modifica.data_modifica > ultima_verifica_liv3.data_modifica and ";
		} else {
			return filters;
		}
		
		if(Util.isValorizzato(filtersDto.modificaRupDopoVerificaDa()) && Util.isValorizzato(filtersDto.modificaRupDopoVerificaA())) {
			filters += addFilterBetween("ultima_modifica.data_modifica", Constants.DATA_MODIFICA_RUP_DOPO_VERIFICA_DA_QUERY_CODE, 
					filtersDto.modificaRupDopoVerificaDa(), Constants.DATA_MODIFICA_RUP_DOPO_VERIFICA_A_QUERY_CODE,
					filtersDto.modificaRupDopoVerificaA(), namedParameters);
		} else if(Util.isValorizzato(filtersDto.modificaRupDopoVerificaDa()) && !Util.isValorizzato(filtersDto.modificaRupDopoVerificaA())) {
			filters += addFilterGreaterThan("ultima_modifica.data_modifica", Constants.DATA_MODIFICA_RUP_DOPO_VERIFICA_DA_QUERY_CODE, 
					filtersDto.modificaRupDopoVerificaDa(), namedParameters);
		} else if(!Util.isValorizzato(filtersDto.modificaRupDopoVerificaDa()) && Util.isValorizzato(filtersDto.modificaRupDopoVerificaA())) {
			filters += addFilterLessThan("ultima_modifica.data_modifica", Constants.DATA_MODIFICA_RUP_DOPO_VERIFICA_A_QUERY_CODE, 
					filtersDto.modificaRupDopoVerificaA(), namedParameters);
		} else {
			Integer ultimaModificaRupDopoVerifica = parametriDao.getParametroByCod(ParametroEnum.ULTIMA_MODIFICA_RUP_DOPO_VERIFICA.getCode());
			
			filters += addFilterBetween("ultima_modifica.data_modifica", Constants.DATA_MODIFICA_RUP_DOPO_VERIFICA_DA_QUERY_CODE, 
					Constants.DATA_MODIFICA_RUP_DOPO_VERIFICA_A_QUERY_CODE, ultimaModificaRupDopoVerifica, namedParameters);
		}
		
		return filters;
	}
	
	private boolean getDefaultDates(FiltersDto filtersDto, ProfiloEnum profilo) throws DatabaseException {
		String metodo = new Object() {}.getClass().getEnclosingMethod().getName();
		
		boolean areDefaultDates = false;
		
		try {
			return switch (profilo) {
			    case RUA -> getDefaultDatesRua(filtersDto);
			    case SUPER_USER -> getDefaultDatesSuperUser(filtersDto);
			    case AUTOCONTROLLO_2 -> getDefaultDatesRPLiv2(filtersDto);
			    case AUTOCONTROLLO_3 -> getDefaultDatesRPLiv3(filtersDto);
			    default -> areDefaultDates;
			};

		} catch (Exception e) {
			logError(metodo, e.getMessage());
			throw new DatabaseException(e);
		}
	}
	
	protected DateModifica getDateModificaRup(DateModifica dateModifica) throws DatabaseException {
		Integer ultimaModificaRup = parametriDao.getParametroByCod(ParametroEnum.ULTIMA_MODIFICA_RUP.getCode());
		Date ultimaModificaA = new Date();
		
		Calendar c = Calendar.getInstance(); 
		c.setTime(ultimaModificaA); 
		c.add(Calendar.DATE, -ultimaModificaRup);
		Date ultimaModificaDa = c.getTime();
		
		dateModifica.setModificaRupDa(Util.fromDateToString(ultimaModificaDa, Constants.FORMATTER_DATE));
		dateModifica.setModificaRupA(Util.fromDateToString(ultimaModificaA, Constants.FORMATTER_DATE));
		
		return dateModifica;
	}
	
	protected DateModifica getDateAutoControlloLiv2(DateModifica dateModifica) throws DatabaseException {
		Integer ultimaModificaLiv2 = parametriDao.getParametroByCod(ParametroEnum.ULTIMA_VERIFICA_LIV2.getCode());
		Date ultimaModificaLiv2A = new Date();
		
		Calendar c = Calendar.getInstance(); 
		c.setTime(ultimaModificaLiv2A); 
		c.add(Calendar.DATE, -ultimaModificaLiv2);
		Date ultimaModificaLiv2Da = c.getTime();
		
		dateModifica.setModificaAutocontrolloLiv2Da(Util.fromDateToString(ultimaModificaLiv2Da, Constants.FORMATTER_DATE));
		dateModifica.setModificaAutocontrolloLiv2A(Util.fromDateToString(ultimaModificaLiv2A, Constants.FORMATTER_DATE));
		
		return dateModifica;
	}
	
	protected DateModifica getDateAutoControlloLiv3(DateModifica dateModifica) throws DatabaseException {
		Integer ultimaModificaLiv3 = parametriDao.getParametroByCod(ParametroEnum.ULTIMA_VERIFICA_LIV3.getCode());
		Date ultimaModificaLiv3A = new Date();
		
		Calendar c = Calendar.getInstance(); 
		c.setTime(ultimaModificaLiv3A); 
		c.add(Calendar.DATE, -ultimaModificaLiv3);
		Date ultimaModificaLiv3Da = c.getTime();
		
		dateModifica.setModificaAutocontrolloLiv3Da(Util.fromDateToString(ultimaModificaLiv3Da, Constants.FORMATTER_DATE));
		dateModifica.setModificaAutocontrolloLiv3A(Util.fromDateToString(ultimaModificaLiv3A, Constants.FORMATTER_DATE));
		
		return dateModifica;
	}
	
	private boolean getDefaultDatesRua(FiltersDto filtersDto) throws DatabaseException {
		DateModifica dateModifica = new DateModifica();
		
		if(Util.isValorizzato(filtersDto.modificaRupDa()) || Util.isValorizzato(filtersDto.modificaRupA())) {
			getDateModificaRup(dateModifica);
			
			if(dateModifica.getModificaRupDa().equalsIgnoreCase(filtersDto.modificaRupDa()) 
					&& dateModifica.getModificaRupA().equalsIgnoreCase(filtersDto.modificaRupA())) {
				return true;
			}
		}
		
		return false;
	}
	
	private boolean getDefaultDatesSuperUser(FiltersDto filtersDto) throws DatabaseException {
		DateModifica dateModifica = new DateModifica();
		
		if(Util.isValorizzato(filtersDto.modificaRupDa()) || Util.isValorizzato(filtersDto.modificaRupA())
				|| Util.isValorizzato(filtersDto.modificaVerifica2Da()) || Util.isValorizzato(filtersDto.modificaVerifica2A())
				|| Util.isValorizzato(filtersDto.modificaVerifica3Da()) || Util.isValorizzato(filtersDto.modificaVerifica3A()) ) {
			getDateModificaRup(dateModifica);
			getDateAutoControlloLiv2(dateModifica);
			getDateAutoControlloLiv3(dateModifica);
			
			if(dateModifica.getModificaRupDa().equalsIgnoreCase(filtersDto.modificaRupDa()) 
				&& dateModifica.getModificaRupA().equalsIgnoreCase(filtersDto.modificaRupA())
				&& dateModifica.getModificaAutocontrolloLiv2Da().equalsIgnoreCase(filtersDto.modificaVerifica2Da()) 
				&& dateModifica.getModificaAutocontrolloLiv2A().equalsIgnoreCase(filtersDto.modificaVerifica2A())
				&& dateModifica.getModificaAutocontrolloLiv3Da().equalsIgnoreCase(filtersDto.modificaVerifica3Da()) 
				&& dateModifica.getModificaAutocontrolloLiv3A().equalsIgnoreCase(filtersDto.modificaVerifica3A())) {
					return true;
			}
		}
		
		return false;
	}
	
	private boolean getDefaultDatesRPLiv2(FiltersDto filtersDto) throws DatabaseException {
		DateModifica dateModifica = new DateModifica();
		
		if(Util.isValorizzato(filtersDto.modificaRupDa()) || Util.isValorizzato(filtersDto.modificaRupA())
				|| Util.isValorizzato(filtersDto.modificaVerifica2Da()) || Util.isValorizzato(filtersDto.modificaVerifica2A()) ) {
			getDateModificaRup(dateModifica);
			getDateAutoControlloLiv2(dateModifica);
			
			if(dateModifica.getModificaRupDa().equalsIgnoreCase(filtersDto.modificaRupDa()) 
				&& dateModifica.getModificaRupA().equalsIgnoreCase(filtersDto.modificaRupA())
				&& dateModifica.getModificaAutocontrolloLiv2Da().equalsIgnoreCase(filtersDto.modificaVerifica2Da()) 
				&& dateModifica.getModificaAutocontrolloLiv2A().equalsIgnoreCase(filtersDto.modificaVerifica2A()) ) {
					return true;
			}
		}
		
		return false;
	}
	
	private boolean getDefaultDatesRPLiv3(FiltersDto filtersDto) throws DatabaseException {
		DateModifica dateModifica = new DateModifica();
		
		if(Util.isValorizzato(filtersDto.modificaRupDa()) || Util.isValorizzato(filtersDto.modificaRupA())
				|| Util.isValorizzato(filtersDto.modificaVerifica3Da()) || Util.isValorizzato(filtersDto.modificaVerifica3A()) ) {
			getDateModificaRup(dateModifica);
			getDateAutoControlloLiv3(dateModifica);
			
			if(dateModifica.getModificaRupDa().equalsIgnoreCase(filtersDto.modificaRupDa()) 
				&& dateModifica.getModificaRupA().equalsIgnoreCase(filtersDto.modificaRupA())
				&& dateModifica.getModificaAutocontrolloLiv3Da().equalsIgnoreCase(filtersDto.modificaVerifica3Da()) 
				&& dateModifica.getModificaAutocontrolloLiv3A().equalsIgnoreCase(filtersDto.modificaVerifica3A()) ) {
					return true;
			}
		}
		
		return false;
	}
}
