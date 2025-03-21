/*
* SPDX-FileCopyrightText: (C) Copyright 2025 Regione Piemonte
*
* SPDX-License-Identifier: EUPL-1.2 
*/
package it.csi.monpnrr.monpnrrbe.integration.dao.util;

import it.csi.monpnrr.monpnrrbe.util.log.AbstractLogger;

public class SQLStatements extends AbstractLogger {
	
	protected static String SELECT_RUP_FOR_RUP_PROFILE = """
			with rup as (select pdu.nome, pdu.cognome, prupr.progetto_id
			from pnrr_d_utente pdu
			join pnrr_r_utente_progetto_rup prupr on prupr.utente_id_rup = pdu.utente_id 
			where pdu.data_cancellazione is null
			and prupr.data_cancellazione is null 
			and pdu.validita_fine is null
			and prupr.validita_fine is null
			and pdu.cf = :cf),
		""";
	
	protected static String SELECT_RUP_FOR_OTHERS_PROFILE = """
			with rup as (select pdu.nome, pdu.cognome, prupr.progetto_id, pdu.cf
			from pnrr_d_utente pdu
			join pnrr_r_utente_progetto_rup prupr on prupr.utente_id_rup = pdu.utente_id 
			where pdu.data_cancellazione is null
			and prupr.data_cancellazione is null 
			and pdu.validita_fine is null
			and prupr.validita_fine is null),
		""";
	
	protected static String SELECT_RUA = """
			rua as (select pdu.nome, pdu.cognome, prupr.progetto_id, pdu.cf
			from pnrr_d_utente pdu
			join pnrr_r_utente_progetto_rua prupr on prupr.utente_id_rua = pdu.utente_id 
			where pdu.data_cancellazione is null
			and prupr.data_cancellazione is null 
			and pdu.validita_fine is null
			and prupr.validita_fine is null ),
		""";
	
	protected static String SELECT_ULTIMA_MODIFICA = """
			ultima_modifica as (select max(prdr.data_modifica) as data_modifica, pdu.cognome, pdu.nome , prrd.rilevazione_id
			from pnrr_r_domanda_risposta prdr
			join pnrr_r_rilevazione_domanda prrd on prrd.r_rilevazione_domanda_id = prdr.r_rilevazione_domanda_id
			join pnrr_r_utente_rilevazione prur on prur.rilevazione_id = prrd.rilevazione_id 
			join pnrr_d_utente pdu on prur.utente_id_rup = pdu.utente_id
			where prdr.data_cancellazione is null
			and prdr.validita_fine is null
			and prrd.data_cancellazione is null
			and prrd.validita_fine is null
			and prur.data_cancellazione is null
			and prur.validita_fine is null
			and pdu.data_cancellazione is null 
			and pdu.validita_fine is null
			group by prrd.rilevazione_id, pdu.cognome, pdu.nome),
		""";
	
	protected static String SELECT_ULTIMA_VERIFICA_ASR = """
			ultima_verifica as (select max(prrv.data_modifica) as data_modifica, pdu.cognome, pdu.nome, prrd.rilevazione_id
			from pnrr_r_domanda_risposta prdr
			join pnrr_r_rilevazione_domanda prrd on prrd.r_rilevazione_domanda_id = prdr.r_rilevazione_domanda_id
			join pnrr_r_risposte_verifica prrv on prrv.r_domanda_risposta_id = prdr.r_domanda_risposta_id 
			join pnrr_r_utente_risposte_verifica prurv on prurv.r_risposte_verifica_id = prrv.r_risposte_verifica_id
			join pnrr_d_utente pdu on prurv.utente_id = pdu.utente_id
			where prdr.data_cancellazione is null
			and prdr.validita_fine is null
			and prrd.data_cancellazione is null
			and prrd.validita_fine is null
			and prrv.data_cancellazione is null
			and prrv.validita_fine is null
			and prurv.data_cancellazione is null
			and prurv.validita_fine is null
			and pdu.data_cancellazione is null
			and pdu.validita_fine is null
			group by prrd.rilevazione_id, pdu.cognome, pdu.nome)
		""";
	
	protected static String SELECT_ULTIMA_VERIFICA_LIV2 = """
			ultima_verifica_liv2 as (
				WITH UltimaData AS (
				    SELECT 
				        prrd.rilevazione_id,
				        MAX(prrv.data_modifica) AS data_modifica
				    FROM pnrr_r_domanda_risposta prdr
				    JOIN pnrr_r_rilevazione_domanda prrd ON prrd.r_rilevazione_domanda_id = prdr.r_rilevazione_domanda_id
				    JOIN pnrr_r_risposte_verifica prrv ON prrv.r_domanda_risposta_id = prdr.r_domanda_risposta_id
				    JOIN pnrr_d_verifica_livello pdvl ON prrv.verifica_id = pdvl.verifica_livello_id
				    WHERE 
				        prrd.data_cancellazione IS NULL
				        AND prrd.validita_fine IS NULL
				        AND prrv.data_cancellazione IS NULL
				        AND prrv.validita_fine IS NULL
				        AND pdvl.verifica_livello_cod = :verifica2Cod
				    GROUP BY prrd.rilevazione_id
				)
				SELECT DISTINCT
				    prrd.rilevazione_id,
				    ud.data_modifica,
				    pdu.cognome, 
				    pdu.nome, 
				    prrv.verifica_id
				FROM pnrr_r_domanda_risposta prdr
				JOIN pnrr_r_rilevazione_domanda prrd ON prrd.r_rilevazione_domanda_id = prdr.r_rilevazione_domanda_id
				JOIN pnrr_r_risposte_verifica prrv ON prrv.r_domanda_risposta_id = prdr.r_domanda_risposta_id
				JOIN pnrr_d_verifica_livello pdvl ON prrv.verifica_id = pdvl.verifica_livello_id
				JOIN pnrr_r_utente_risposte_verifica prurv ON prurv.r_risposte_verifica_id = prrv.r_risposte_verifica_id
				JOIN pnrr_d_utente pdu ON prurv.utente_id = pdu.utente_id
				JOIN UltimaData ud ON prrd.rilevazione_id = ud.rilevazione_id AND prrv.data_modifica = ud.data_modifica
				WHERE 
				    prrd.data_cancellazione IS NULL
				    AND prrd.validita_fine IS NULL
				    AND prrv.data_cancellazione IS NULL
				    AND prrv.validita_fine IS NULL
				    AND prurv.data_cancellazione IS NULL
				    AND prurv.validita_fine IS NULL
				    AND pdu.data_cancellazione IS NULL
				    AND pdu.validita_fine IS NULL
				    AND pdvl.verifica_livello_cod = :verifica2Cod
				ORDER BY pdu.cognome, pdu.nome, prrd.rilevazione_id)
			""";
	
	protected static String SELECT_ULTIMA_VERIFICA_LIV3 = """
			ultima_verifica_liv3 as (
				WITH UltimaData AS (
				    SELECT 
				        prrd.rilevazione_id,
				        MAX(prrv.data_modifica) AS data_modifica
				    FROM pnrr_r_domanda_risposta prdr
				    JOIN pnrr_r_rilevazione_domanda prrd ON prrd.r_rilevazione_domanda_id = prdr.r_rilevazione_domanda_id
				    JOIN pnrr_r_risposte_verifica prrv ON prrv.r_domanda_risposta_id = prdr.r_domanda_risposta_id
				    JOIN pnrr_d_verifica_livello pdvl ON prrv.verifica_id = pdvl.verifica_livello_id
				    WHERE 
				        prrd.data_cancellazione IS NULL
				        AND prrd.validita_fine IS NULL
				        AND prrv.data_cancellazione IS NULL
				        AND prrv.validita_fine IS NULL
				        AND pdvl.verifica_livello_cod = :verifica3Cod
				    GROUP BY prrd.rilevazione_id
				)
				SELECT DISTINCT
				    prrd.rilevazione_id,
				    ud.data_modifica,
				    pdu.cognome, 
				    pdu.nome, 
				    prrv.verifica_id
				FROM pnrr_r_domanda_risposta prdr
				JOIN pnrr_r_rilevazione_domanda prrd ON prrd.r_rilevazione_domanda_id = prdr.r_rilevazione_domanda_id
				JOIN pnrr_r_risposte_verifica prrv ON prrv.r_domanda_risposta_id = prdr.r_domanda_risposta_id
				JOIN pnrr_d_verifica_livello pdvl ON prrv.verifica_id = pdvl.verifica_livello_id
				JOIN pnrr_r_utente_risposte_verifica prurv ON prurv.r_risposte_verifica_id = prrv.r_risposte_verifica_id
				JOIN pnrr_d_utente pdu ON prurv.utente_id = pdu.utente_id
				JOIN UltimaData ud ON prrd.rilevazione_id = ud.rilevazione_id AND prrv.data_modifica = ud.data_modifica
				WHERE 
				    prrd.data_cancellazione IS NULL
				    AND prrd.validita_fine IS NULL
				    AND prrv.data_cancellazione IS NULL
				    AND prrv.validita_fine IS NULL
				    AND prurv.data_cancellazione IS NULL
				    AND prurv.validita_fine IS NULL
				    AND pdu.data_cancellazione IS NULL
				    AND pdu.validita_fine IS NULL
				    AND pdvl.verifica_livello_cod = :verifica3Cod
				ORDER BY pdu.cognome, pdu.nome, prrd.rilevazione_id)
			""";
	
	protected static String GET_PROGETTI_SELECT = """
			select pdp.id_cup, pda.asl_cod, pda.asl_azienda_desc, pdm.misura_desc, 
			string_agg(concat(rua.nome, '-', rua.cognome), ',') as rua, 
			string_agg(concat(rup.nome, '-', rup.cognome), ',') as rup,
			pdc.checklist_desc, pdsr.stato_rilevazione_desc, 
			concat(ultima_modifica.cognome, ' ', ultima_modifica.nome) as ultima_modifica_utente, 
			ultima_modifica.data_modifica as ultima_modifica_data,
			count(*) OVER() AS total_count, 
		""";
	
	protected static String GET_PROGETTI_SELECT_VERIFICA_ASR = """
			concat(ultima_verifica.cognome, ' ', ultima_verifica.nome) as ultima_verifica_utente, 
			ultima_verifica.data_modifica as ultima_verifica_data, 
			case 
				when ultima_verifica.data_modifica > ultima_modifica.data_modifica
				then true
				else false
			end as modifica_recente
		""";
	
	protected static String GET_PROGETTI_SELECT_VERIFICA_RP_SUPERUSER = """
			case 
				when coalesce(ultima_verifica_liv2.data_modifica, '1900-01-01') > coalesce(ultima_verifica_liv3.data_modifica, '1900-01-01')
				then
					concat(ultima_verifica_liv2.cognome, ' ', ultima_verifica_liv2.nome) 
				else 
					concat(ultima_verifica_liv3.cognome, ' ', ultima_verifica_liv3.nome) 
			end as ultima_verifica_utente,
			case 
				when coalesce(ultima_verifica_liv2.data_modifica, '1900-01-01') > coalesce(ultima_verifica_liv3.data_modifica, '1900-01-01')
				then
					ultima_verifica_liv2.data_modifica 
				else 
					ultima_verifica_liv3.data_modifica 
			end as ultima_verifica_data,
			case 
				when coalesce(ultima_verifica_liv2.data_modifica, '1900-01-01') > coalesce(ultima_verifica_liv3.data_modifica, '1900-01-01')
				then
					case
						when ultima_modifica.data_modifica > ultima_verifica_liv2.data_modifica
						then true
						else false
					end 
				else 
					case
						when ultima_modifica.data_modifica > ultima_verifica_liv3.data_modifica
						then true
						else false
					end 
			end as modifica_recente 
		""";
	
	protected static String GET_PROGETTI_SELECT_VERIFICA_RP_LIV2 = """
			concat(ultima_verifica_liv2.cognome, ' ', ultima_verifica_liv2.nome) as ultima_verifica_utente, 
			ultima_verifica_liv2.data_modifica as ultima_verifica_data, 
			case 
				when ultima_modifica.data_modifica > ultima_verifica_liv2.data_modifica 
				then true
				else false
			end as modifica_recente
		""";
	
	protected static String GET_PROGETTI_SELECT_VERIFICA_RP_LIV3 = """
			concat(ultima_verifica_liv3.cognome, ' ', ultima_verifica_liv3.nome) as ultima_verifica_utente, 
			ultima_verifica_liv3.data_modifica as ultima_verifica_data, 
			case 
				when ultima_modifica.data_modifica > ultima_verifica_liv3.data_modifica 
				then true
				else false
			end as modifica_recente
		""";
	
	protected static String GET_PROGETTI_FROM = """
			from pnrr_d_progetto pdp  
			join pnrr_d_misura pdm on pdm.misura_id = pdp.misura_id
			join rup on pdp.progetto_id = rup.progetto_id
			join rua on pdp.progetto_id = rua.progetto_id
			join pnrr_d_asl pda on pdp.asl_id = pda.asl_id
			join pnrr_r_checklist_progetto prcp on pdp.progetto_id = prcp.progetto_id 
			join pnrr_d_checklist pdc on pdc.checklist_id = prcp.checklist_id 
			join pnrr_t_rilevazione ptr on ptr.r_checklist_progetto_id = prcp.r_checklist_progetto_id 
			join pnrr_r_stato_rilevazione prsr on prsr.rilevazione_id = ptr.rilevazione_id 
			join pnrr_d_stato_rilevazione pdsr on pdsr.stato_rilevazione_id = prsr.stato_rilevazione_id 
			left join ultima_modifica on ultima_modifica.rilevazione_id = ptr.rilevazione_id 
		""";
	
	protected static String GET_PROGETTI_FROM_VERIFICA_ASR = """
			left join ultima_verifica on ultima_verifica.rilevazione_id = ptr.rilevazione_id 
		""";
	
	protected static String GET_PROGETTI_FROM_VERIFICA_RP_LIV2 = """
			left join ultima_verifica_liv2 on ultima_verifica_liv2.rilevazione_id = ptr.rilevazione_id 
		""";
	
	protected static String GET_PROGETTI_FROM_VERIFICA_RP_LIV3 = """
			left join ultima_verifica_liv3 on ultima_verifica_liv3.rilevazione_id = ptr.rilevazione_id 
		""";
	
	protected static String GET_PROGETTI_WHERE = """
			where pdp.data_cancellazione is null 
			and pdp.validita_fine is null
			and pdm.data_cancellazione is null 
			and pdm.validita_fine is null
			and prcp.data_cancellazione is null 
			and prcp.validita_fine is null
			and pdc.data_cancellazione is null 
			and pdc.validita_fine is null
			and ptr.data_cancellazione is null 
			and ptr.validita_fine is null
			and prsr.data_cancellazione is null 
			and prsr.validita_fine is null
			and pdsr.data_cancellazione is null 
			and pdsr.validita_fine is null
			and pda.data_cancellazione is null 
			and pda.validita_fine is null
		""";
	
	protected static String GET_PROGETTI_GROUP_BY = """
			group by pdp.id_cup, pda.asl_cod, pda.asl_azienda_desc, pdm.misura_desc, pdc.checklist_desc, 
			pdsr.stato_rilevazione_desc, ultima_modifica_utente, ultima_modifica_data, 
		""";
	
	protected static String GET_PROGETTI_GROUP_BY_ASR_AND_RP_LIV = """
			ultima_verifica_utente, ultima_verifica_data
		""";
	
	protected static String GET_PROGETTI_GROUP_BY_RP_SUPERUSER = """
			ultima_verifica_liv2.data_modifica, ultima_verifica_liv3.data_modifica,
			ultima_verifica_liv2.cognome, ultima_verifica_liv3.cognome,
			ultima_verifica_liv2.nome, ultima_verifica_liv3.nome
		""";

	protected static final String SELECT_RILEVAZIONE_ID = """
			select ptr.rilevazione_id
			from pnrr_d_progetto pdp
			join pnrr_r_checklist_progetto prcp on pdp.progetto_id = prcp.progetto_id 
			join pnrr_t_rilevazione ptr on ptr.r_checklist_progetto_id = prcp.r_checklist_progetto_id 
			where pdp.id_cup = :cup
			and pdp.validita_fine is null
			and pdp.data_cancellazione is null
			and prcp.validita_fine is null
			and prcp.data_cancellazione is null
			and ptr.validita_fine is null
			and ptr.data_cancellazione is null
		""";
	
	protected static final String SELECT_STATO_RILEVAZIONE_ID = """
			select pdsr.stato_rilevazione_id
			from pnrr_d_stato_rilevazione pdsr
			where pdsr.stato_rilevazione_cod = :stato
			and pdsr.validita_fine is null
			and pdsr.data_cancellazione is null
		""";
	
	protected static final String SELECT_UTENTE_ID = """
			select pdu.utente_id
			from pnrr_d_utente pdu
			where pdu.cf = :cf
			and pdu.validita_fine is null
			and pdu.data_cancellazione is null
		""";
	
	protected static final String SELECT_PROGETTO_DETTAGLIO = """
			with rup as (select pdu.nome, pdu.cognome, prupr.progetto_id
				from pnrr_d_utente pdu
				join pnrr_r_utente_progetto_rup prupr on prupr.utente_id_rup = pdu.utente_id 
				where pdu.data_cancellazione is null
				and prupr.data_cancellazione is null 
				and pdu.validita_fine is null
				and prupr.validita_fine is null),
			ultima_modifica as (select max(prdr.data_modifica) as data_modifica, pdu.cognome, pdu.nome , prrd.rilevazione_id
				from pnrr_r_domanda_risposta prdr
				join pnrr_r_rilevazione_domanda prrd on prrd.r_rilevazione_domanda_id = prdr.r_rilevazione_domanda_id
				join pnrr_r_utente_rilevazione prur on prur.rilevazione_id = prrd.rilevazione_id 
				join pnrr_d_utente pdu on prur.utente_id_rup = pdu.utente_id
				where prdr.data_cancellazione is null
				and prdr.validita_fine is null
				and prrd.data_cancellazione is null
				and prrd.validita_fine is null
				and prur.data_cancellazione is null
				and prur.validita_fine is null
				and pdu.data_cancellazione is null 
				and pdu.validita_fine is null
				group by prrd.rilevazione_id, pdu.cognome, pdu.nome),
			ultima_verifica_liv2 as (
                WITH UltimaData AS (
                    SELECT 
                        prrd.rilevazione_id,
                        MAX(prrv.data_modifica) AS data_modifica
                    FROM pnrr_r_domanda_risposta prdr
                    JOIN pnrr_r_rilevazione_domanda prrd ON prrd.r_rilevazione_domanda_id = prdr.r_rilevazione_domanda_id
                    JOIN pnrr_r_risposte_verifica prrv ON prrv.r_domanda_risposta_id = prdr.r_domanda_risposta_id
                    JOIN pnrr_d_verifica_livello pdvl ON prrv.verifica_id = pdvl.verifica_livello_id
                    WHERE 
                        prrd.data_cancellazione IS NULL
                        AND prrd.validita_fine IS NULL
                        AND prrv.data_cancellazione IS NULL
                        AND prrv.validita_fine IS NULL
                        AND pdvl.verifica_livello_cod = :verifica2Cod
                    GROUP BY prrd.rilevazione_id
                )
                SELECT DISTINCT
                    prrd.rilevazione_id,
                    ud.data_modifica,
                    pdu.cognome, 
                    pdu.nome, 
                    prrv.verifica_id
                FROM pnrr_r_domanda_risposta prdr
                JOIN pnrr_r_rilevazione_domanda prrd ON prrd.r_rilevazione_domanda_id = prdr.r_rilevazione_domanda_id
                JOIN pnrr_r_risposte_verifica prrv ON prrv.r_domanda_risposta_id = prdr.r_domanda_risposta_id
                JOIN pnrr_d_verifica_livello pdvl ON prrv.verifica_id = pdvl.verifica_livello_id
                JOIN pnrr_r_utente_risposte_verifica prurv ON prurv.r_risposte_verifica_id = prrv.r_risposte_verifica_id
                JOIN pnrr_d_utente pdu ON prurv.utente_id = pdu.utente_id
                JOIN UltimaData ud ON prrd.rilevazione_id = ud.rilevazione_id AND prrv.data_modifica = ud.data_modifica
                WHERE 
                    prrd.data_cancellazione IS NULL
                    AND prrd.validita_fine IS NULL
                    AND prrv.data_cancellazione IS NULL
                    AND prrv.validita_fine IS NULL
                    AND prurv.data_cancellazione IS NULL
                    AND prurv.validita_fine IS NULL
                    AND pdu.data_cancellazione IS NULL
                    AND pdu.validita_fine IS NULL
                    AND pdvl.verifica_livello_cod = :verifica2Cod
                ORDER BY pdu.cognome, pdu.nome, prrd.rilevazione_id),
            ultima_verifica_liv3 as (
                WITH UltimaData AS (
                    SELECT 
                        prrd.rilevazione_id,
                        MAX(prrv.data_modifica) AS data_modifica
                    FROM pnrr_r_domanda_risposta prdr
                    JOIN pnrr_r_rilevazione_domanda prrd ON prrd.r_rilevazione_domanda_id = prdr.r_rilevazione_domanda_id
                    JOIN pnrr_r_risposte_verifica prrv ON prrv.r_domanda_risposta_id = prdr.r_domanda_risposta_id
                    JOIN pnrr_d_verifica_livello pdvl ON prrv.verifica_id = pdvl.verifica_livello_id
                    WHERE 
                        prrd.data_cancellazione IS NULL
                        AND prrd.validita_fine IS NULL
                        AND prrv.data_cancellazione IS NULL
                        AND prrv.validita_fine IS NULL
                        AND pdvl.verifica_livello_cod = :verifica3Cod
                    GROUP BY prrd.rilevazione_id
                )
                SELECT DISTINCT
                    prrd.rilevazione_id,
                    ud.data_modifica,
                    pdu.cognome, 
                    pdu.nome, 
                    prrv.verifica_id
                FROM pnrr_r_domanda_risposta prdr
                JOIN pnrr_r_rilevazione_domanda prrd ON prrd.r_rilevazione_domanda_id = prdr.r_rilevazione_domanda_id
                JOIN pnrr_r_risposte_verifica prrv ON prrv.r_domanda_risposta_id = prdr.r_domanda_risposta_id
                JOIN pnrr_d_verifica_livello pdvl ON prrv.verifica_id = pdvl.verifica_livello_id
                JOIN pnrr_r_utente_risposte_verifica prurv ON prurv.r_risposte_verifica_id = prrv.r_risposte_verifica_id
                JOIN pnrr_d_utente pdu ON prurv.utente_id = pdu.utente_id
                JOIN UltimaData ud ON prrd.rilevazione_id = ud.rilevazione_id AND prrv.data_modifica = ud.data_modifica
                WHERE 
                    prrd.data_cancellazione IS NULL
                    AND prrd.validita_fine IS NULL
                    AND prrv.data_cancellazione IS NULL
                    AND prrv.validita_fine IS NULL
                    AND prurv.data_cancellazione IS NULL
                    AND prurv.validita_fine IS NULL
                    AND pdu.data_cancellazione IS NULL
                    AND pdu.validita_fine IS NULL
                    AND pdvl.verifica_livello_cod = :verifica3Cod
                ORDER BY pdu.cognome, pdu.nome, prrd.rilevazione_id)
			select pdm.misura_desc, pda.asl_cod, pda.asl_azienda_desc,
			pdc.checklist_desc, pdp.id_cup, pdp.modalita_attuativa, 
			string_agg(concat(rup.nome, '-', rup.cognome), ',') as rup,
			concat(ultima_modifica.cognome, ' ', ultima_modifica.nome) as ultima_modifica_utente, 
			ultima_modifica.data_modifica as ultima_modifica_data,
			concat(ultima_verifica_liv2.cognome, ' ', ultima_verifica_liv2.nome) as ultima_verifica_liv2_utente, 
			ultima_verifica_liv2.data_modifica as ultima_verifica_liv2_data,
			concat(ultima_verifica_liv3.cognome, ' ', ultima_verifica_liv3.nome) as ultima_verifica_liv3_utente, 
			ultima_verifica_liv3.data_modifica as ultima_verifica_liv3_data,
			case 
				when coalesce(ultima_verifica_liv2.data_modifica, '1900-01-01') > coalesce(ultima_verifica_liv3.data_modifica, '1900-01-01')
				then
					concat(ultima_verifica_liv2.cognome, ' ', ultima_verifica_liv2.nome) 
				else 
					concat(ultima_verifica_liv3.cognome, ' ', ultima_verifica_liv3.nome) 
			end as ultima_verifica_utente,
			case 
				when coalesce(ultima_verifica_liv2.data_modifica, '1900-01-01') > coalesce(ultima_verifica_liv3.data_modifica, '1900-01-01')
				then
					ultima_verifica_liv2.data_modifica 
				else 
					ultima_verifica_liv3.data_modifica 
			end as ultima_verifica_data
			from pnrr_d_progetto pdp
			join pnrr_d_asl pda on pdp.asl_id = pda.asl_id 
			join pnrr_d_misura pdm on pdm.misura_id = pdp.misura_id 
			join pnrr_r_checklist_progetto prcp on pdp.progetto_id = prcp.progetto_id 
			join pnrr_d_checklist pdc on pdc.checklist_id = prcp.checklist_id 
			join rup on rup.progetto_id = pdp.progetto_id 
			join pnrr_t_rilevazione ptr on ptr.r_checklist_progetto_id = prcp.r_checklist_progetto_id 
			left join ultima_modifica on ptr.rilevazione_id = ultima_modifica.rilevazione_id
			left join ultima_verifica_liv2 on ptr.rilevazione_id = ultima_verifica_liv2.rilevazione_id
			left join ultima_verifica_liv3 on ptr.rilevazione_id = ultima_verifica_liv3.rilevazione_id
			where pdp.id_cup = :cup
			group by pdm.misura_desc, pda.asl_cod, pda.asl_azienda_desc,
			pdc.checklist_desc, pdp.id_cup, pdp.modalita_attuativa,
			ultima_modifica_utente, ultima_modifica_data,
			ultima_verifica_liv2_utente, ultima_verifica_liv2_data,
			ultima_verifica_liv3_utente, ultima_verifica_liv3_data
		""";
	
	protected static final String SELECT_DATA_UTENTE_PROGETTO = """
			select max(prdr.data_modifica) as data_modifica, concat(pdu.nome, ' ', pdu.cognome) as utente_modifica 
			from pnrr_r_domanda_risposta prdr
			join pnrr_r_rilevazione_domanda prrd on prrd.r_rilevazione_domanda_id = prdr.r_rilevazione_domanda_id
			join pnrr_r_utente_rilevazione prur on prur.rilevazione_id = prrd.rilevazione_id 
			join pnrr_d_utente pdu on prur.utente_id_rup = pdu.utente_id
			where prdr.data_cancellazione is null
			and prdr.validita_fine is null
			and prrd.data_cancellazione is null
			and prrd.validita_fine is null
			and prur.data_cancellazione is null
			and prur.validita_fine is null
			and pdu.data_cancellazione is null 
			and pdu.validita_fine is null
			and prrd.rilevazione_id = :id
			group by pdu.cognome, pdu.nome
		""";
	
	protected static final String SELECT_CIG = """
			select pdc.cig_id, pdc.cig_cod, pddp.descrizione_procedura_cig_cod, pdc.progressivo_rilevazione
			from pnrr_d_cig pdc
			join pnrr_r_cig_procedure prcp ON prcp.cig_id = pdc.cig_id
			join pnrr_d_descrizione_procedura_cig pddp on pddp.descrizione_procedura_cig_id = prcp.descrizione_procedura_cig_id 
			join pnrr_t_rilevazione ptr on ptr.rilevazione_id = pdc.rilevazione_id 
			where pdc.data_cancellazione is null 
			and pdc.validita_fine is NULL
			and prcp.data_cancellazione is null 
			and prcp.validita_fine is null
			and pddp.data_cancellazione is null 
			and pddp.validita_fine is null
			and ptr.data_cancellazione is null 
			and ptr.validita_fine is null
			and ptr.rilevazione_id  = :rilevazioneId
		""";
	
	protected static final String SELECT_CIG_ID_BY_COD_AND_DESCRIZIONE = """
			select pdc.cig_id
			from pnrr_d_cig pdc
			join pnrr_r_cig_procedure prcp ON prcp.cig_id = pdc.cig_id
			join pnrr_d_descrizione_procedura_cig pddp on pddp.descrizione_procedura_cig_id = prcp.descrizione_procedura_cig_id 
			join pnrr_t_rilevazione ptr on ptr.rilevazione_id = pdc.rilevazione_id 
			where pdc.data_cancellazione is null 
			and pdc.validita_fine is NULL
			and prcp.data_cancellazione is null 
			and prcp.validita_fine is null
			and pddp.data_cancellazione is null 
			and pddp.validita_fine is null
			and ptr.data_cancellazione is null 
			and ptr.validita_fine is null
			and ptr.rilevazione_id  = :rilevazioneId
			and pddp.descrizione_procedura_cig_cod = :descrizioneProceduraCigCod
			and pdc.cig_cod = :cigCod
		""";
	
	protected static final String SELECT_CHECKLIST_PROGETTO = """
			WITH rilevazione_domanda AS (
			    SELECT 
			        prrd.r_rilevazione_domanda_id, 
			        prrd.domanda_id, 
			        ptr.rilevazione_id
			    FROM 
			        pnrr_d_progetto pdp 
			    JOIN 
			        pnrr_r_checklist_progetto prcp ON pdp.progetto_id = prcp.progetto_id 
			    JOIN 
			        pnrr_t_rilevazione ptr ON prcp.r_checklist_progetto_id = ptr.r_checklist_progetto_id
			    JOIN 
			        pnrr_r_rilevazione_domanda prrd ON prrd.rilevazione_id = ptr.rilevazione_id
			    WHERE 
			        pdp.id_cup = :cupId 
			        AND pdp.data_cancellazione IS NULL 
			        AND pdp.validita_fine IS NULL 
			        AND prcp.data_cancellazione IS NULL 
			        AND prcp.validita_fine IS NULL 
			        AND ptr.data_cancellazione IS NULL 
			        AND ptr.validita_fine IS NULL 
			        AND prrd.data_cancellazione IS NULL 
			        AND prrd.validita_fine IS NULL
			) 
			,rilevazione_domanda_modifica_rup AS (
			    SELECT 
			        rd.r_rilevazione_domanda_id, 
			        rd.domanda_id,
			        prdr.r_domanda_risposta_id,
			        prdr.cig_id, 
			        prdr.nota, 
			        pdr.risposta_cod,
			        prdr.data_modifica as data_modifica_rup, 
			        pdu.cognome as cognome_rup, pdu.nome as nome_rup
			    FROM 
			        rilevazione_domanda rd
			    LEFT JOIN 
			        pnrr_r_domanda_risposta prdr ON rd.r_rilevazione_domanda_id = prdr.r_rilevazione_domanda_id
			        AND prdr.data_cancellazione IS NULL 
			        AND prdr.validita_fine IS NULL
			    LEFT JOIN 
			        pnrr_d_risposta pdr ON prdr.risposta_id = pdr.risposta_id
			        AND pdr.data_cancellazione IS NULL 
			        AND pdr.validita_fine IS null
			    LEFT JOIN 
					pnrr_d_utente pdu on prdr.utente_modifica = pdu.cf
					AND pdu.data_cancellazione IS NULL 
			        AND pdu.validita_fine IS null
		)
			, ultima_verifica_liv2 as (
				select max(prrv.data_modifica) as data_modifica, pdu.cognome, pdu.nome, rilevazione_domanda_modifica_rup.domanda_id, prrv.verifica_id, 
					pdvv.verifica_cod,
			        prrv.nota as nota_verifica,
			        pdvl.verifica_livello_cod,
			        rilevazione_domanda_modifica_rup.cig_id 
					from rilevazione_domanda_modifica_rup 
					join pnrr_r_risposte_verifica prrv on prrv.r_domanda_risposta_id = rilevazione_domanda_modifica_rup.r_domanda_risposta_id 
					join pnrr_d_verifica_livello pdvl on prrv.verifica_id = pdvl.verifica_livello_id
					join pnrr_r_utente_risposte_verifica prurv on prurv.r_risposte_verifica_id = prrv.r_risposte_verifica_id
					join pnrr_d_utente pdu on prurv.utente_id = pdu.utente_id
					JOIN  pnrr_d_valori_verifica pdvv on prrv.valori_verifica_id = pdvv.valori_verifica_id
					where prrv.data_cancellazione is null
					and prrv.validita_fine is null
					and prurv.data_cancellazione is null
					and prurv.validita_fine is null
					and pdu.data_cancellazione is null
					and pdu.validita_fine is null
					and pdvv.data_cancellazione IS null
					and pdvv.validita_fine IS NULL
					and pdvl.verifica_livello_cod = :verificaLivello2Cod
					group by rilevazione_domanda_modifica_rup.domanda_id, pdu.cognome, pdu.nome, prrv.verifica_id,pdvv.verifica_cod,
					        prrv.nota,
					        pdvl.verifica_livello_cod, rilevazione_domanda_modifica_rup.cig_id)
			, ultima_verifica_liv3 as (select max(prrv.data_modifica) as data_modifica, pdu.cognome, pdu.nome, rilevazione_domanda_modifica_rup.domanda_id, prrv.verifica_id,
					pdvv.verifica_cod,
			        prrv.nota as nota_verifica,
			        pdvl.verifica_livello_cod,
			        rilevazione_domanda_modifica_rup.cig_id 
					from rilevazione_domanda_modifica_rup 
					join pnrr_r_risposte_verifica prrv on prrv.r_domanda_risposta_id = rilevazione_domanda_modifica_rup.r_domanda_risposta_id 
					join pnrr_d_verifica_livello pdvl on prrv.verifica_id = pdvl.verifica_livello_id
					join pnrr_r_utente_risposte_verifica prurv on prurv.r_risposte_verifica_id = prrv.r_risposte_verifica_id
					join pnrr_d_utente pdu on prurv.utente_id = pdu.utente_id
					join pnrr_d_valori_verifica pdvv on prrv.valori_verifica_id = pdvv.valori_verifica_id
					where prrv.data_cancellazione is null
					and prrv.validita_fine is null
					and prurv.data_cancellazione is null
					and prurv.validita_fine is null
					and pdu.data_cancellazione is null
					and pdu.validita_fine is null
					and pdvv.data_cancellazione IS null
					and pdvv.validita_fine IS null
					and pdvl.verifica_livello_cod = :verificaLivello3Cod
					group by rilevazione_domanda_modifica_rup.domanda_id, pdu.cognome, pdu.nome, prrv.verifica_id,
					pdvv.verifica_cod,
					        prrv.nota,
					        pdvl.verifica_livello_cod,rilevazione_domanda_modifica_rup.cig_id )
			SELECT 
			    pdm.macrosezioni_cod, 
			    rilevazione_domanda_modifica_rup.cig_id,  
			    pds.numero_sezione, 
			    pds.sezione_cod, 
			    pdss.sotto_sezione_cod, 
			    rilevazione_domanda_modifica_rup.r_rilevazione_domanda_id,
			    pdddc.documentazione_da_caricare_cod, 
			    pdd.note, 
			    rilevazione_domanda_modifica_rup.risposta_cod, 
			    rilevazione_domanda_modifica_rup.nota,
			    string_agg(concat(rilevazione_domanda_modifica_rup.cognome_rup, ' ', rilevazione_domanda_modifica_rup.nome_rup), ',') as utente_modifica_rup,
			    rilevazione_domanda_modifica_rup.data_modifica_rup,
			    ultima_verifica_liv2.verifica_cod as verifica_cod_liv2,
			    ultima_verifica_liv2.nota_verifica as nota_verifica_liv2,
			    ultima_verifica_liv2.verifica_livello_cod as verifica_livello_cod_liv2,
			    string_agg(concat(ultima_verifica_liv2.cognome, ' ', ultima_verifica_liv2.nome), ',') as utente_verifica_liv2,
			    ultima_verifica_liv2.data_modifica as data_verifica_liv2,
			    case 
			    	when ultima_verifica_liv2.verifica_cod = :verificaCod and risposta_cod = :rispostaCod
			    	then true
			    	else false
			    end as is_verificato_liv2,
			    ultima_verifica_liv3.verifica_cod as verifica_cod_liv3,
			    ultima_verifica_liv3.nota_verifica as nota_verifica_liv3,
			    ultima_verifica_liv3.verifica_livello_cod as verifica_livello_cod_liv3,
			    string_agg(concat(ultima_verifica_liv3.cognome, ' ', ultima_verifica_liv3.nome), ',') as utente_verifica_liv3,
			    ultima_verifica_liv3.data_modifica as data_verifica_liv3,
			    case 
			    	when ultima_verifica_liv3.verifica_cod = :verificaCod and risposta_cod = :rispostaCod
			    	then true
			    	else false
			    end as is_verificato_liv3
			FROM 
			    pnrr_d_domanda pdd
			JOIN 
			    rilevazione_domanda_modifica_rup ON rilevazione_domanda_modifica_rup.domanda_id = pdd.domanda_id
			LEFT JOIN 
			    ultima_verifica_liv2 ON ultima_verifica_liv2.domanda_id = rilevazione_domanda_modifica_rup.domanda_id 
			    and ((ultima_verifica_liv2.cig_id is not null and ultima_verifica_liv2.cig_id = rilevazione_domanda_modifica_rup.cig_id) 
				    or ultima_verifica_liv2.cig_id is null)
			LEFT JOIN 
			    ultima_verifica_liv3 ON ultima_verifica_liv3.domanda_id = rilevazione_domanda_modifica_rup.domanda_id
			    and ((ultima_verifica_liv3.cig_id is not null and ultima_verifica_liv3.cig_id = rilevazione_domanda_modifica_rup.cig_id) 
				    or ultima_verifica_liv3.cig_id is null)
			JOIN 
			    pnrr_d_macrosezioni pdm ON pdm.macrosezioni_id = pdd.macrosezioni_id
			JOIN 
			    pnrr_d_sezione pds ON pds.sezione_id = pdd.sezione_id 
			JOIN 
			    pnrr_d_sotto_sezione pdss ON pdss.sotto_sezione_id = pdd.sotto_sezione_id 
			JOIN 
			    pnrr_d_documentazione_da_caricare pdddc ON pdddc.documentazione_da_caricare_id = pdd.documentazione_da_caricare_id
			WHERE 
			    pdd.data_cancellazione IS NULL 
			    AND pdd.validita_fine IS NULL
			    AND pdm.data_cancellazione IS NULL 
			    AND pdm.validita_fine IS NULL
			    AND pds.data_cancellazione IS NULL 
			    AND pds.validita_fine IS NULL
			    AND pdss.data_cancellazione IS NULL 
			    AND pdss.validita_fine IS NULL
			    AND pdddc.data_cancellazione IS NULL 
			    AND pdddc.validita_fine IS NULL	
			GROUP BY 
				pdd.numero_ordine,
				pdm.macrosezioni_cod, 
			    rilevazione_domanda_modifica_rup.cig_id,  
			    pds.numero_sezione, 
			    pds.sezione_cod, 
			    pdss.sotto_sezione_cod, 
			    rilevazione_domanda_modifica_rup.r_rilevazione_domanda_id,
			    pdddc.documentazione_da_caricare_cod, 
			    pdd.note, 
			    rilevazione_domanda_modifica_rup.risposta_cod, 
			    rilevazione_domanda_modifica_rup.nota,
			    rilevazione_domanda_modifica_rup.data_modifica_rup,
			    ultima_verifica_liv2.verifica_cod,
			    ultima_verifica_liv2.nota_verifica,
			    ultima_verifica_liv2.verifica_livello_cod,
			    ultima_verifica_liv2.data_modifica,
			    ultima_verifica_liv3.verifica_cod,
			    ultima_verifica_liv3.nota_verifica,
			    ultima_verifica_liv3.verifica_livello_cod,
			    ultima_verifica_liv3.data_modifica
			ORDER BY pdd.numero_ordine;
		""";
	
	protected static final String SELECT_STATO_PROGETTO = """
			select pdsr.stato_rilevazione_cod 
			from pnrr_d_progetto pdp 
			join pnrr_r_checklist_progetto prcp on pdp.progetto_id = prcp.progetto_id 
			join pnrr_t_rilevazione ptr on prcp.r_checklist_progetto_id = ptr.r_checklist_progetto_id 
			join pnrr_r_stato_rilevazione prsr on prsr.rilevazione_id = ptr.rilevazione_id 
			join pnrr_d_stato_rilevazione pdsr on prsr.stato_rilevazione_id = pdsr.stato_rilevazione_id 
			where pdp.id_cup = :cup
			and pdp.data_cancellazione is null
			and pdp.validita_fine is null
			and prcp.data_cancellazione is null
			and prcp.validita_fine is null
			and ptr.data_cancellazione is null
			and ptr.validita_fine is null
			and prsr.data_cancellazione is null
			and prsr.validita_fine is null
			and pdsr.data_cancellazione is null
			and pdsr.validita_fine is null
		""";
	
	protected static final String COUNT_AZIENDA_SANITARIA_PROGETTO = """
			select count(*)
			from pnrr_d_progetto pdp 
			join pnrr_d_asl pda on pdp.asl_id = pda.asl_id 
			where pdp.id_cup = :cup
			and pda.asl_cod = :aslCod
			and pdp.data_cancellazione is null
			and pdp.validita_fine is null
			and pda.data_cancellazione is null
			and pda.validita_fine is null
		""";
	
	protected static final String SELECT_PROGRESSIVO_PROGETTO = """
			select coalesce(max(pdc.progressivo_rilevazione) , 0)
			from pnrr_d_cig pdc 
			where rilevazione_id = :rilevazioneId
		""";
	
	protected static final String UPDATE_VALIDITA_FINE_STATO_RILEVAZIONE_PROGETTO = """
			update pnrr_r_stato_rilevazione
			set validita_fine = now(),
			data_modifica = now(),
			utente_modifica = :cf
			where rilevazione_id = :id
			and validita_fine is null;
		""";
	
	protected static final String INSERT_STATO_RILEVAZIONE_PROGETTO = """
			INSERT INTO pnrr_r_stato_rilevazione
			(stato_rilevazione_id, 
			rilevazione_id, 
			utente_creazione, utente_modifica)
			VALUES(:statoRilevazioneId, :rilevazioneId, :cf, :cf) 
		""";
	
	protected static final String UPDATE_VALIDITA_FINE_UTENTE_RILEVAZIONE_PROGETTO = """
			update pnrr_r_utente_rilevazione
			set validita_fine = now(),
			data_modifica = now(),
			utente_modifica = :cf
			where rilevazione_id = :id
			and validita_fine is null;
		""";
	
	protected static final String INSERT_UTENTE_RILEVAZIONE_PROGETTO = """
			INSERT INTO pnrr_r_utente_rilevazione
			(rilevazione_id, 
			utente_id_rup,
			utente_creazione, utente_modifica)
			VALUES(:rilevazioneId, :utenteIdRup, :cf, :cf) 
		""";
	
	protected static final String UPDATE_VALIDITA_FINE_UTENTE_VERIFICA_PROGETTO = """
			update pnrr_r_utente_risposte_verifica
			set validita_fine = now(),
			data_modifica = now(),
			utente_modifica = :cf
			where r_risposte_verifica_id = :rRisposteVerificaId
			and validita_fine is null;
		""";
	
	protected static final String INSERT_UTENTE_VERIFICA_PROGETTO = """
			INSERT INTO pnrr_r_utente_risposte_verifica
			(r_risposte_verifica_id,
			utente_id,
			utente_creazione, utente_modifica)
			VALUES(:rRisposteVerificaId, :utenteId, :cf, :cf) 
		""";
	
	protected static final String SELECT_DOMANDA_RISPOSTA_ID = """
			SELECT prdr.r_domanda_risposta_id 
			FROM pnrr_r_domanda_risposta prdr
			WHERE prdr.cig_id is NULL 
			AND prdr.r_rilevazione_domanda_id = :rRilevazioneDomandaId
			AND prdr.validita_fine is null
			AND prdr.data_cancellazione is null;
		""";
	
	protected static final String SELECT_DOMANDA_RISPOSTA_WITH_CIG_ID = """
			SELECT prdr.r_domanda_risposta_id 
			FROM pnrr_r_domanda_risposta prdr
			WHERE prdr.cig_id = :cigId 
			AND prdr.r_rilevazione_domanda_id = :rRilevazioneDomandaId
			AND prdr.validita_fine is null
			AND prdr.data_cancellazione is null;
		""";
	
	protected static final String SELECT_RISPOSTA_ID = """
			select pdr.risposta_id 
			from pnrr_d_risposta pdr 
			where pdr.risposta_cod = :rispostaCod 
		""";
	
	protected static final String SELECT_DESCRIZIONE_PROCEDURA_ID = """
			select pddp.descrizione_procedura_cig_id 
			from pnrr_d_descrizione_procedura_cig pddp 
			where pddp.descrizione_procedura_cig_cod = :descrizioneProceduraCigCod 
		""";
	
	protected static final String SELECT_DESCRIZIONE_PROCEDURA_CIG_ID = """
			select pddpc.descizione_procedura_cig_id 
			from pnrr_d_descizione_procedura_cig pddpc  
			where pddpc.descizione_procedura_cig_cod = :descrizioneProceduraCigCod
		""";
	
	protected static final String INSERT_RISPOSTE = """
			INSERT INTO pnrr_r_domanda_risposta
			(r_rilevazione_domanda_id, 
			risposta_id, 
			cig_id, 
			nota, 
			utente_creazione, utente_modifica)
			VALUES(:rRilevazioneDomandaId, :rispostaId, :cigId, 
			:nota, :cf, :cf) 
		""";
	
	protected static final String UPDATE_VALIDITA_FINE_RISPOSTA = """
			update pnrr_r_domanda_risposta 
			set validita_fine = now(),
			data_modifica = now(),
			utente_modifica = :cf
			where r_rilevazione_domanda_id = :id
			and validita_fine is null;
		""";
	
	protected static final String UPDATE_VALIDITA_FINE_RISPOSTA_WITH_CIG = """
			update pnrr_r_domanda_risposta 
			set validita_fine = now(),
			data_modifica = now(),
			utente_modifica = :cf
			where r_rilevazione_domanda_id = :id
			and cig_id = :cigId
			and validita_fine is null;
		""";
	
	protected static final String INSERT_CIG = """
			INSERT INTO pnrr_d_cig
			(rilevazione_id, 
			cig_cod, 
			cig_desc, 
			progressivo_rilevazione, utente_creazione, utente_modifica)
			VALUES(:rilevazioneId, :cigCod, :cigDesc, :progressivo, :cf, :cf);
		""";
	
	protected static final String UPDATE_VALIDITA_FINE_RISPOSTA_CIG = """
			update pnrr_r_domanda_risposta 
			set validita_fine = now(),
			data_modifica = now(),
			utente_modifica = :cf
			where cig_id = :cigId
			and validita_fine is null;
		""";
	
	protected static final String INSERT_PROCEDURE_CIG = """
			INSERT INTO pnrr_r_cig_procedure
			(descrizione_procedura_cig_id, cig_id, utente_creazione, utente_modifica)
			VALUES(:descrizioneProceduraCigId, :cigId, :cf, :cf);
		""";
	
	protected static final String UPDATE_VALIDITA_FINE_PROCEDURE_CIG = """
			update pnrr_r_cig_procedure
			set validita_fine = now(),
			data_modifica = now(),
			utente_modifica = :cf
			where cig_id = :cigId
			and validita_fine is null;
		""";
	
	protected static final String UPDATE_VALIDITA_FINE_CIG = """
			update pnrr_d_cig
			set validita_fine = now(),
			data_modifica = now(),
			utente_modifica = :cf
			where cig_id = :cigId
			and validita_fine is null;
		""";
	
	protected static final String UPDATE_VALIDITA_FINE_VERIFICA = """
			update pnrr_r_risposte_verifica 
			set validita_fine = now(),
			data_modifica = now(),
			utente_modifica = :cf
			where r_domanda_risposta_id = :rDomandaRispostaId
			and verifica_id = :verificaLivelloId
			and validita_fine is null;
		""";
	
	protected static final String INSERT_R_RISPOSTE_VERIFICA = """
			INSERT INTO pnrr_r_risposte_verifica
				(verifica_id, valori_verifica_id, 
				r_domanda_risposta_id, nota, 
				utente_creazione, utente_modifica)
			VALUES(:verificaId, :valoriVerificaId, 
				:rDomandaRispostaId, :nota, 
				:cf, :cf);
		""";
	
	protected static final String SELECT_VERIFICA_ID = """
			SELECT valori_verifica_id
			FROM pnrr_d_valori_verifica
			WHERE verifica_cod = :verificaCod;
		""";
	
	protected static final String SELECT_VERIFICA_LIVELLO_ID = """
			SELECT verifica_livello_id
			FROM pnrr_d_verifica_livello
			WHERE verifica_livello_cod = :verificaLivelloCod;
		""";
	
	protected static final String SELECT_RISPOSTE_CUP_OLD = """
			select prdr.r_rilevazione_domanda_id, prdr.risposta_id, prdr.nota, prdr.cig_id
			from pnrr_r_rilevazione_domanda prrd 
			join pnrr_r_domanda_risposta prdr on prrd.r_rilevazione_domanda_id = prdr.r_rilevazione_domanda_id 
			where prrd.rilevazione_id = :rilevazioneId
			and prdr.validita_fine is null 
			and prdr.data_cancellazione is null
			and cig_id is null
		""";
	
	protected static final String SELECT_RISPOSTE_CIG_OLD = """
			select prdr.r_rilevazione_domanda_id, prdr.risposta_id, prdr.nota, prdr.cig_id
			from pnrr_r_rilevazione_domanda prrd 
			join pnrr_r_domanda_risposta prdr on prrd.r_rilevazione_domanda_id = prdr.r_rilevazione_domanda_id 
			where prrd.rilevazione_id = :rilevazioneId
			and prdr.validita_fine is null 
			and prdr.data_cancellazione is null
			and cig_id = :cigId
		""";
	
	protected static final String SELECT_VERIFICHE_CUP_OLD = """
			select prdr.r_rilevazione_domanda_id, prrv.verifica_id, prrv.valori_verifica_id, prrv.nota, prdr.cig_id
			from pnrr_r_risposte_verifica prrv 
			join pnrr_r_domanda_risposta prdr on prrv.r_domanda_risposta_id = prdr.r_domanda_risposta_id 
			join pnrr_r_rilevazione_domanda prrd on prrd.r_rilevazione_domanda_id = prdr.r_rilevazione_domanda_id 
			where prrd.rilevazione_id = :rilevazioneId
			and prrv.verifica_id = :livelloId
			and prdr.validita_fine is null 
			and prdr.data_cancellazione is null
			and prrv.validita_fine is null 
			and prrv.data_cancellazione is null
			and cig_id is null
		""";
	
	protected static final String SELECT_VERIFICHE_CIG_OLD = """
			select prdr.r_rilevazione_domanda_id, prrv.verifica_id, prrv.valori_verifica_id, prrv.nota, prdr.cig_id
			from pnrr_r_risposte_verifica prrv 
			join pnrr_r_domanda_risposta prdr on prrv.r_domanda_risposta_id = prdr.r_domanda_risposta_id 
			join pnrr_r_rilevazione_domanda prrd on prrd.r_rilevazione_domanda_id = prdr.r_rilevazione_domanda_id 
			where prrd.rilevazione_id = :rilevazioneId
			and prrv.verifica_id = :livelloId
			and prdr.validita_fine is null 
			and prdr.data_cancellazione is null
			and prrv.validita_fine is null 
			and prrv.data_cancellazione is null
			and cig_id = :cigId
		""";
}
