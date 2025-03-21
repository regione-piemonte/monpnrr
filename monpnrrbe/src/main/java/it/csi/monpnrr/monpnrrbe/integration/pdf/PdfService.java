/*
* SPDX-FileCopyrightText: (C) Copyright 2025 Regione Piemonte
*
* SPDX-License-Identifier: EUPL-1.2 
*/
package it.csi.monpnrr.monpnrrbe.integration.pdf;

import java.io.ByteArrayOutputStream;
import java.util.List;

import org.apache.logging.log4j.util.Strings;
import org.springframework.stereotype.Service;

import com.lowagie.text.Chunk;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

import it.csi.monpnrr.monpnrrbe.api.dto.ProgettoDettaglio;
import it.csi.monpnrr.monpnrrbe.api.impl.base.RESTBaseService;
import it.csi.monpnrr.monpnrrbe.exception.PdfException;
import it.csi.monpnrr.monpnrrbe.integration.dao.dto.ChecklistProgettoDto;
import it.csi.monpnrr.monpnrrbe.integration.dao.dto.ProceduraCigDto;
import it.csi.monpnrr.monpnrrbe.util.Constants;
import it.csi.monpnrr.monpnrrbe.util.enumeration.MacrosezioneEnum;
import it.csi.monpnrr.monpnrrbe.util.enumeration.ProfiloEnum;
import it.csi.monpnrr.monpnrrbe.util.enumeration.VerificaLivelloEnum;
import it.csi.monpnrr.monpnrrbe.util.enumeration.VerificaRilevazioneEnum;

@Service
public class PdfService extends RESTBaseService {
	
	public byte[] createPdfChecklist(List<ChecklistProgettoDto> progettiDto, List<ProceduraCigDto> procedureCigDto, 
			ProgettoDettaglio progettoDettaglio, String stato, ProfiloEnum profilo) throws PdfException {
		String metodo = new Object() {}.getClass().getEnclosingMethod().getName();
		ByteArrayOutputStream bo = new ByteArrayOutputStream();
		
		try {
			boolean isRp = ProfiloEnum.isRp(profilo);
			String livello = getLivelloFromProfilo(profilo);
			
			Document document = new Document(PageSize.A4.rotate());
			PdfWriter.getInstance(document,  bo);
			document.open();
			creaIntestazione(document, progettoDettaglio, stato, isRp, livello);
			creaTabellaCup(document, progettiDto, isRp, livello);
			document.newPage();
			creaTabellaCig(document, progettiDto, procedureCigDto, isRp, livello);
			document.close();
		} catch (Exception e) {
			logError(metodo, e.getMessage());
			throw new PdfException(); 
		}
		
		return bo.toByteArray();
	}
	
	private void creaIntestazione(Document document, ProgettoDettaglio progettoDettaglio, String stato, Boolean isRp, String livello) {
		creaTitolo(Constants.PDF_CHECKLIST + progettoDettaglio.getChecklist(), document);
		aggiungiNewLine(document);
		creaFraseDettaglio(Constants.AZIENDA_SANITARIA, progettoDettaglio.getAzienda().getDescrizione(), document);
		creaFraseDettaglio(Constants.INTERVENTO, progettoDettaglio.getIntervento(), document);
		creaFraseDettaglio(Constants.CUP, progettoDettaglio.getCup(), document);
		creaFraseDettaglio(Constants.STATO, stato, document);
		creaFraseDettaglio(Constants.MODALITA_ATTUATIVA, progettoDettaglio.getModalitaAttuativa(), document);
		
		if(progettoDettaglio.getUltimaModifica() != null) {
			creaFraseDettaglio(Constants.DATA_ULTIMA_MODIFICA, progettoDettaglio.getUltimaModifica().getData(), document);
			creaFraseDettaglio(Constants.UTENTE_ULTIMA_MODIFICA, progettoDettaglio.getUltimaModifica().getUtente(), document);
		}
		
		if(isRp) {
			if(VerificaLivelloEnum.VERIFICA_LIVELLO_3.getCode().equalsIgnoreCase(livello)) {
				if(progettoDettaglio.getUltimaVerificaLiv2() != null) {
					String data = progettoDettaglio.getUltimaVerificaLiv2().getData() == null ? Strings.EMPTY : 
						progettoDettaglio.getUltimaVerificaLiv2().getData();
					String utente = progettoDettaglio.getUltimaVerificaLiv2().getUtente() == null ? Strings.EMPTY : 
						progettoDettaglio.getUltimaVerificaLiv2().getUtente();
					creaFraseDettaglio(Constants.DATA_ULTIMO_AUTOCONTROLLO, data, document);
					creaFraseDettaglio(Constants.UTENTE_ULTIMO_AUTOCONTROLLO, utente, document);
				}
			} else if(Strings.isEmpty(livello)) { 
				if(progettoDettaglio.getUltimaVerificaLiv2() != null) {
					String data = progettoDettaglio.getUltimaVerificaLiv2().getData() == null ? Strings.EMPTY : 
						progettoDettaglio.getUltimaVerificaLiv2().getData();
					String utente = progettoDettaglio.getUltimaVerificaLiv2().getUtente() == null ? Strings.EMPTY : 
						progettoDettaglio.getUltimaVerificaLiv2().getUtente();
					creaFraseDettaglio(Constants.DATA_ULTIMO_AUTOCONTROLLO_LIV2, data, document);
					creaFraseDettaglio(Constants.UTENTE_ULTIMO_AUTOCONTROLLO_LIV2, utente, document);
				}
				if(progettoDettaglio.getUltimaVerificaLiv3() != null) {
					String data = progettoDettaglio.getUltimaVerificaLiv3().getData() == null ? Strings.EMPTY : 
						progettoDettaglio.getUltimaVerificaLiv3().getData();
					String utente = progettoDettaglio.getUltimaVerificaLiv3().getUtente() == null ? Strings.EMPTY : 
						progettoDettaglio.getUltimaVerificaLiv3().getUtente();
					creaFraseDettaglio(Constants.DATA_ULTIMO_AUTOCONTROLLO_LIV3, data, document);
					creaFraseDettaglio(Constants.UTENTE_ULTIMO_AUTOCONTROLLO_LIV3, utente, document);
				}
			}
		}
		
		aggiungiNewLine(document);
	}
	
	private void creaTabellaCup(Document document, List<ChecklistProgettoDto> checklistProgettiDto, boolean isRp, String livello) 
			throws DocumentException {
		creaSottoTitolo(Constants.DOCUMENTAZIONE_CUP, document);
		aggiungiLeading(document, Constants.PDF_LEADING_SPACE);
		
		PdfPTable tabella = creaTabella(isRp, livello);
		
		tabella.setWidthPercentage(Constants.PDF_WIDTH_TABLE);
		
		creaIntestazioni(tabella, isRp, livello);
		List<ChecklistProgettoDto> checklistCup = checklistProgettiDto.stream()
				.filter(c -> c.macroSezioneCod().equals(MacrosezioneEnum.CUP.getCode())).toList();
		
		aggiungiContenuto(0, checklistCup, isRp, livello, tabella, document);
		
		aggiungiNewLine(document);
	}
	
	private void creaTabellaCig(Document document, List<ChecklistProgettoDto> checklistProgettiDto, 
			List<ProceduraCigDto> procedureCigDto, boolean isRp, String livello) throws DocumentException {
		if(procedureCigDto.size() == 0) {
			return;
		}

		creaSottoTitolo(Constants.DOCUMENTAZIONE_CIG, document);
		
		List<ChecklistProgettoDto> checklistCig = checklistProgettiDto.stream()
				.filter(c -> c.macroSezioneCod().equals(MacrosezioneEnum.CIG.getCode())).toList();
		
		
		for(ProceduraCigDto proceduraCigDto : procedureCigDto) {
			String titolo = Constants.CODICE_CIG + proceduraCigDto.cigCod() + Constants.PROCEDURA + proceduraCigDto.descrizioneProceduraCigCod();
			aggiungiLeading(document, Constants.PDF_LEADING_SPACE);
			creaSottoTitolo(titolo, document);
			aggiungiLeading(document, Constants.PDF_LEADING_SPACE);
			
			PdfPTable tabella = creaTabella(isRp, livello);
			tabella.setWidthPercentage(Constants.PDF_WIDTH_TABLE);
			creaIntestazioni(tabella, isRp, livello);
			aggiungiContenuto(proceduraCigDto.cigId(), checklistCig, isRp, livello, tabella, document);
			document.newPage();
		}
	}
	
	private PdfPTable creaTabella(boolean isRp, String livello) {
		PdfPTable tabella;
		if(isRp) {
			if(VerificaLivelloEnum.VERIFICA_LIVELLO_2.getCode().equals(livello)) {
				tabella = new PdfPTable(Constants.PDF_RELATIVE_WIDTHS_RP_LIV2);
			} else if (VerificaLivelloEnum.VERIFICA_LIVELLO_3.getCode().equals(livello)) {
				tabella = new PdfPTable(Constants.PDF_RELATIVE_WIDTHS_RP_LIV3);
			}else {
				tabella = new PdfPTable(Constants.PDF_RELATIVE_WIDTHS_RP_SUPERUSER);
			}
		} else {
			tabella = new PdfPTable(Constants.PDF_RELATIVE_WIDTHS_ASR);
		}
		
		return tabella;
	}
	
	private void creaIntestazioni(PdfPTable tabella, boolean isRp, String livello) {
		creaCella(Constants.N_SEZIONE_HEADER, tabella);
		creaCella(Constants.SEZIONE_HEADER, tabella);
		creaCella(Constants.SOTTOSEZIONE_HEADER, tabella);
		creaCella(Constants.DOCUMENTAZIONE_DA_CARICARE_PDF_HEADER, tabella);
		creaCella(Constants.NOTE_HEADER, tabella);
		creaCella(Constants.ESITO_HEADER, tabella);
		creaCella(Constants.NOTA_ASR_HEADER, tabella);
		
		if(isRp) {
			if(VerificaLivelloEnum.VERIFICA_LIVELLO_2.getCode().equals(livello)) {
				creaCella(Constants.DATA_ULTIMA_MODIFICA_HEADER, tabella);
				creaCella(Constants.UTENTE_ULTIMA_MODIFICA_HEADER, tabella);
				creaCella(Constants.AUTOCONTROLLO_LIVELLO_2_HEADER, tabella);
				creaCella(Constants.NOTA_AUTOCONTROLLO_LIVELLO_2_HEADER, tabella);
			} else if(VerificaLivelloEnum.VERIFICA_LIVELLO_3.getCode().equals(livello)) {
				creaCella(Constants.DATA_ULTIMA_MODIFICA_HEADER, tabella);
				creaCella(Constants.UTENTE_ULTIMA_MODIFICA_HEADER, tabella);
				creaCella(Constants.AUTOCONTROLLO_LIVELLO_2_HEADER, tabella);
				creaCella(Constants.NOTA_AUTOCONTROLLO_LIVELLO_2_HEADER, tabella);
				creaCella(Constants.DATA_AUTOCONTROLLO_LIVELLO_2_HEADER, tabella);
				creaCella(Constants.UTENTE_AUTOCONTROLLO_LIVELLO_2_HEADER, tabella);
				creaCella(Constants.AUTOCONTROLLO_LIVELLO_3_HEADER, tabella);
				creaCella(Constants.NOTA_AUTOCONTROLLO_LIVELLO_3_HEADER, tabella);
			} else {
				creaCella(Constants.DATA_ULTIMA_MODIFICA_HEADER, tabella);
				creaCella(Constants.UTENTE_ULTIMA_MODIFICA_HEADER, tabella);
				creaCella(Constants.AUTOCONTROLLO_LIVELLO_2_HEADER, tabella);
				creaCella(Constants.NOTA_AUTOCONTROLLO_LIVELLO_2_HEADER, tabella);
				creaCella(Constants.DATA_AUTOCONTROLLO_LIVELLO_2_HEADER, tabella);
				creaCella(Constants.UTENTE_AUTOCONTROLLO_LIVELLO_2_HEADER, tabella);
				creaCella(Constants.AUTOCONTROLLO_LIVELLO_3_HEADER, tabella);
				creaCella(Constants.NOTA_AUTOCONTROLLO_LIVELLO_3_HEADER, tabella);
				creaCella(Constants.DATA_AUTOCONTROLLO_LIVELLO_3_HEADER, tabella);
				creaCella(Constants.UTENTE_AUTOCONTROLLO_LIVELLO_3_HEADER, tabella);
			}
		}
	}
	
	private void creaCella(String contenuto, PdfPTable tabella) {
		PdfPCell cell = new PdfPCell(getPhraseForCell(contenuto));
		cell.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell.setGrayFill(0.8f);
		tabella.addCell(cell);
	}
	
	private void creaTitolo(String contenuto, Document document) {
		Font font = FontFactory.getFont(FontFactory.HELVETICA, 15, Font.BOLDITALIC); //Color
		Paragraph paragraph = new Paragraph(contenuto, font);
		document.add(paragraph);
	}
	
	private void creaSottoTitolo(String contenuto, Document document) {
		Font font = FontFactory.getFont(FontFactory.HELVETICA, 13, Font.BOLDITALIC); //Color
		Paragraph paragraph = new Paragraph(contenuto, font);
		document.add(paragraph);
	}
	
	private void creaFraseDettaglio(String intestazione, String contenuto, Document document) {
		Paragraph paragraph = new Paragraph();
		Font font = FontFactory.getFont(FontFactory.HELVETICA, 11, Font.NORMAL); //Color
		paragraph.add(addChunk(intestazione, font));
		font = FontFactory.getFont(FontFactory.HELVETICA, 13, Font.BOLD); //Color
		paragraph.add(addChunk(contenuto, font));
		document.add(paragraph);
	}
	
	private Chunk addChunk(String contenuto, Font font) {
		Chunk chunk = new Chunk(contenuto);
		chunk.setFont(font);
		return chunk;
	}
	
	private void aggiungiNewLine(Document document) {
		document.add(Chunk.NEWLINE);
	}
	
	private void aggiungiLeading(Document document, float leading) {
		document.add(new Paragraph(leading, Constants.SPACE));
	}
	
	private void aggiungiContenuto(Integer cigId, List<ChecklistProgettoDto> checklistProgettiDto, boolean isRp, String livello,
			PdfPTable tabella, Document document) {
		
		List<ChecklistProgettoDto> checklistCup = checklistProgettiDto.stream().filter(c -> c.cigId().equals(cigId)).toList();
		for(ChecklistProgettoDto checklist : checklistCup) {
			tabella.addCell(getPhraseForCell(checklist.numeroSezione()));
			tabella.addCell(getPhraseForCell(checklist.sezioneCod()));
			tabella.addCell(getPhraseForCell(checklist.sottoSezioneCod()));
			tabella.addCell(getPhraseForCell(checklist.documentazioneDaCaricareCod()));
			tabella.addCell(getPhraseForCell(checklist.note()));
			Phrase rispostaCod = checklist.rispostaCod() == null  
					? getPhraseForCell(Constants.NON_INDICATO) 
					: getPhraseForCell(checklist.rispostaCod());
			tabella.addCell(rispostaCod);
			tabella.addCell(getPhraseForCell(checklist.notaAsr()));
			
			if(isRp) {
				if(VerificaLivelloEnum.VERIFICA_LIVELLO_2.getCode().equals(livello)) {
					Phrase dataUltimaModifica = checklist.dataModificaRup() == null  
							? getPhraseForCell(Strings.EMPTY) 
							: getPhraseForCell(checklist.dataModificaRup());
					Phrase utenteUltimaModifica = checklist.utenteModificaRup() == null  
							? getPhraseForCell(Strings.EMPTY) 
							: getPhraseForCell(checklist.utenteModificaRup());
					Phrase verifica = checklist.verificaCodLiv2() == null  
							? getPhraseForCell(Constants.NON_INDICATO) 
							: getPhraseForCell(
									checklist.verificaCodLiv2().equals(VerificaRilevazioneEnum.OK.getCode()) ? 
											Constants.AUTOCONTROLLATO : Constants.NON_INDICATO);
					Phrase notaRp = checklist.noteVerificaLiv2() == null  
							? getPhraseForCell(Strings.EMPTY) 
							: getPhraseForCell(checklist.noteVerificaLiv2());
					
					tabella.addCell(dataUltimaModifica);
					tabella.addCell(utenteUltimaModifica);
					tabella.addCell(verifica);
					tabella.addCell(notaRp);
				} else if(VerificaLivelloEnum.VERIFICA_LIVELLO_3.getCode().equals(livello)) {
					Phrase dataUltimaModifica = checklist.dataModificaRup() == null  
							? getPhraseForCell(Strings.EMPTY) 
							: getPhraseForCell(checklist.dataModificaRup());
					Phrase utenteUltimaModifica = checklist.utenteModificaRup() == null  
							? getPhraseForCell(Strings.EMPTY) 
							: getPhraseForCell(checklist.utenteModificaRup());
					Phrase verificaLiv2 = checklist.verificaCodLiv2() == null  
							? getPhraseForCell(Constants.NON_INDICATO) 
							: getPhraseForCell(
									checklist.verificaCodLiv2().equals(VerificaRilevazioneEnum.OK.getCode()) ? 
											Constants.AUTOCONTROLLATO : Constants.NON_INDICATO);
					Phrase notaRpLiv2 = checklist.noteVerificaLiv2() == null  
							? getPhraseForCell(Strings.EMPTY) 
							: getPhraseForCell(checklist.noteVerificaLiv2());
					Phrase dataVerificaLiv2 = checklist.dataVerificaLiv2() == null  
							? getPhraseForCell(Strings.EMPTY) 
							: getPhraseForCell(checklist.dataVerificaLiv2());
					Phrase utenteVerificaLiv2 = checklist.utenteVerificaLiv2() == null  
							? getPhraseForCell(Strings.EMPTY) 
							: getPhraseForCell(checklist.utenteVerificaLiv2());
					Phrase verificaLiv3 = checklist.verificaCodLiv3() == null  
							? getPhraseForCell(Constants.NON_INDICATO) 
							: getPhraseForCell(
									checklist.verificaCodLiv3().equals(VerificaRilevazioneEnum.OK.getCode()) ? 
											Constants.AUTOCONTROLLATO : Constants.NON_INDICATO);
					Phrase notaRpLiv3 = checklist.noteVerificaLiv3() == null  
							? getPhraseForCell(Strings.EMPTY) 
							: getPhraseForCell(checklist.noteVerificaLiv3());
					
					tabella.addCell(dataUltimaModifica);
					tabella.addCell(utenteUltimaModifica);
					tabella.addCell(verificaLiv2);
					tabella.addCell(notaRpLiv2);
					tabella.addCell(dataVerificaLiv2);
					tabella.addCell(utenteVerificaLiv2);
					tabella.addCell(verificaLiv3);
					tabella.addCell(notaRpLiv3);
				} else {
					Phrase dataUltimaModifica = checklist.dataModificaRup() == null  
							? getPhraseForCell(Strings.EMPTY) 
							: getPhraseForCell(checklist.dataModificaRup());
					Phrase utenteUltimaModifica = checklist.utenteModificaRup() == null  
							? getPhraseForCell(Strings.EMPTY) 
							: getPhraseForCell(checklist.utenteModificaRup());
					Phrase verificaLiv2 = checklist.verificaCodLiv2() == null  
							? getPhraseForCell(Constants.NON_INDICATO) 
							: getPhraseForCell(
									checklist.verificaCodLiv2().equals(VerificaRilevazioneEnum.OK.getCode()) ? 
											Constants.AUTOCONTROLLATO : Constants.NON_INDICATO);
					Phrase notaRpLiv2 = checklist.noteVerificaLiv2() == null  
							? getPhraseForCell(Strings.EMPTY) 
							: getPhraseForCell(checklist.noteVerificaLiv2());
					Phrase dataVerificaLiv2 = checklist.dataVerificaLiv2() == null  
							? getPhraseForCell(Strings.EMPTY) 
							: getPhraseForCell(checklist.dataVerificaLiv2());
					Phrase utenteVerificaLiv2 = checklist.utenteVerificaLiv2() == null  
							? getPhraseForCell(Strings.EMPTY) 
							: getPhraseForCell(checklist.utenteVerificaLiv2());
					Phrase verificaLiv3 = checklist.verificaCodLiv3() == null  
							? getPhraseForCell(Constants.NON_INDICATO) 
							: getPhraseForCell(
									checklist.verificaCodLiv3().equals(VerificaRilevazioneEnum.OK.getCode()) ? 
											Constants.AUTOCONTROLLATO : Constants.NON_INDICATO);
					Phrase notaRpLiv3 = checklist.noteVerificaLiv3() == null  
							? getPhraseForCell(Strings.EMPTY) 
							: getPhraseForCell(checklist.noteVerificaLiv3());
					Phrase dataVerificaLiv3 = checklist.dataVerificaLiv3() == null  
							? getPhraseForCell(Strings.EMPTY) 
							: getPhraseForCell(checklist.dataVerificaLiv3());
					Phrase utenteVerificaLiv3 = checklist.utenteVerificaLiv3() == null  
							? getPhraseForCell(Strings.EMPTY) 
							: getPhraseForCell(checklist.utenteVerificaLiv3());
					
					tabella.addCell(dataUltimaModifica);
					tabella.addCell(utenteUltimaModifica);
					tabella.addCell(verificaLiv2);
					tabella.addCell(notaRpLiv2);
					tabella.addCell(dataVerificaLiv2);
					tabella.addCell(utenteVerificaLiv2);
					tabella.addCell(verificaLiv3);
					tabella.addCell(notaRpLiv3);
					tabella.addCell(dataVerificaLiv3);
					tabella.addCell(utenteVerificaLiv3);
				}
			}
		}

		document.add(tabella);
	}
	
	private Phrase getPhraseForCell(String contenuto) {
		Font font = FontFactory.getFont(FontFactory.HELVETICA, 9, Font.NORMAL);
		Phrase phrase = new Phrase(contenuto, font);
		return phrase;
	}
}
