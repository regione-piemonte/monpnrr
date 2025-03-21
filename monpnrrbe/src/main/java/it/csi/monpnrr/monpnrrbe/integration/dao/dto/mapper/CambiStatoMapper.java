/*
* SPDX-FileCopyrightText: (C) Copyright 2025 Regione Piemonte
*
* SPDX-License-Identifier: EUPL-1.2 
*/
package it.csi.monpnrr.monpnrrbe.integration.dao.dto.mapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Component;

import it.csi.monpnrr.monpnrrbe.api.dto.CambiStato;
import it.csi.monpnrr.monpnrrbe.api.dto.Stato;
import it.csi.monpnrr.monpnrrbe.integration.dao.dto.CambiStatoDto;
import it.csi.monpnrr.monpnrrbe.util.log.AbstractLogger;

@Component
public class CambiStatoMapper extends AbstractLogger {
	
	public CambiStatoDto buildCambiStatoDto(String statoDaCod, String statoDaDesc, Integer statoDaOrdinamento, 
			String statoACod, String statoADesc, Integer statoAOrdinamento) {
		CambiStatoDto result = new CambiStatoDto(statoDaCod, statoDaDesc, statoDaOrdinamento, statoACod, statoADesc, statoAOrdinamento);
		return result;
	}
	
	public List<CambiStato> buildCambiStatoList(List<CambiStatoDto> cambiStatoDto) {
        List<CambiStato> cambiStato = new ArrayList<CambiStato>();
        for (CambiStatoDto cambioStatoDto : cambiStatoDto) {
            buildCambiStato(cambioStatoDto, cambiStato);
        }
        return cambiStato;
    }
	
	private Stato buildStatoFinale(CambiStatoDto cambioStatoDto) {
		Stato stato = new Stato();
		stato.setStatoCod(cambioStatoDto.statoFinaleCod());
		stato.setStatoDesc(cambioStatoDto.statoFinaleDesc());
		stato.setStatoOrdinamento(cambioStatoDto.statoFinaleOrdinamento());
		return stato;
	}
	
	private void buildCambiStato(CambiStatoDto cambioStatoDto, List<CambiStato> cambiStato) {
		CambiStato cambioStato = findStatoByCod(cambioStatoDto.statoInizialeCod(), cambiStato);
		if (cambioStato != null) {
			Stato stato = buildStatoFinale(cambioStatoDto);
			cambioStato.getAltriStati().add(stato);
	    } else {
	    	CambiStato cambioStatoNew = new CambiStato();
	    	cambioStatoNew.setStatoCod(cambioStatoDto.statoInizialeCod());
	    	cambioStatoNew.setStatoDesc(cambioStatoDto.statoInizialeDesc());
	    	cambioStatoNew.setStatoOrdinamento(cambioStatoDto.statoInizialeOrdinamento());
	    	
	    	List<Stato> altriStati = new ArrayList<Stato>();
	    	Stato stato = buildStatoFinale(cambioStatoDto);
	    	altriStati.add(stato);
	    	
	    	cambioStatoNew.setAltriStati(altriStati);
	    	cambiStato.add(cambioStatoNew);
	    }
	}
	
	private CambiStato findStatoByCod(String cambioStatoIniziale, List<CambiStato> cambiStato) {
		Optional<CambiStato> cambioStato = cambiStato.stream().filter(s -> s.getStatoCod().equalsIgnoreCase(cambioStatoIniziale)).findFirst();
		if(cambioStato.isPresent()) {
			return cambioStato.get();
		}
		
		return null;
	}
}
