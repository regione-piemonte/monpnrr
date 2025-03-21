/*
* SPDX-FileCopyrightText: (C) Copyright 2025 Regione Piemonte
*
* SPDX-License-Identifier: EUPL-1.2 
*/
package it.csi.monpnrr.monpnrrbe.integration.dao;

import java.sql.Types;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Service;

import it.csi.monpnrr.monpnrrbe.exception.DatabaseException;
import it.csi.monpnrr.monpnrrbe.integration.dao.dto.CsiLogAudit;

@Service
public class CSILogAuditDao {

	@Value("${db.encryption.key}")
	private String encryptionKey;

	private static final String INSERT_CSI_LOG_AUDIT = """
			insert into csi_log_audit(
			data_ora,
			id_app,
			ip_address,
			utente,
			operazione,
			ogg_oper,
			key_oper,
			uuid,
			request_payload,
			response_payload,
			esito_chiamata)
			values (
			now(),
			:id_app,
			:ip_address,
			:utente,
			:operazione,
			:ogg_oper,
			:key_oper,
			:uuid,
			pgp_sym_encrypt(:request_payload::text, :encryption_key::text,'compress-algo=2'::text),
			pgp_sym_encrypt(:response_payload::text, :encryption_key::text,'compress-algo=2'::text),
			:esito_chiamata )
			""";

	@Autowired
	private NamedParameterJdbcTemplate jdbcTemplate;

	public long saveAudit(CsiLogAudit audit) throws DatabaseException {
		KeyHolder keyHolder = new GeneratedKeyHolder();
		MapSqlParameterSource params = new MapSqlParameterSource();
		params.addValue("id_app", audit.getIdApp(), Types.VARCHAR);
		params.addValue("ip_address", audit.getIpAddress(), Types.VARCHAR);
		params.addValue("utente", audit.getUtente(), Types.VARCHAR);
		params.addValue("operazione", audit.getOperazione(), Types.VARCHAR);
		params.addValue("ogg_oper", audit.getOggOper(), Types.VARCHAR);
		params.addValue("key_oper", audit.getKeyOper(), Types.VARCHAR);
		params.addValue("uuid", audit.getUuid(), Types.VARCHAR);
		params.addValue("request_payload", audit.getRequestPayload(), Types.VARCHAR);
		params.addValue("response_payload", audit.getResponsePayload(), Types.VARCHAR);
		params.addValue("esito_chiamata", audit.getEsitoChiamata(), Types.INTEGER);

		params.addValue("encryption_key", encryptionKey, Types.VARCHAR);

		jdbcTemplate.update(INSERT_CSI_LOG_AUDIT, params, keyHolder, new String[] { "audit_id" });
		Number key = keyHolder.getKey();
		if(key == null) {
			throw new DatabaseException("Error inserting into csi_log_audit table");
		}
		
		return key.longValue();
	}

}
