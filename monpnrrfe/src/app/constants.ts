/*
* SPDX-FileCopyrightText: (C) Copyright 2025 Regione Piemonte
*
* SPDX-License-Identifier: EUPL-1.2 
*/

export const ACTION = {
  LETTURA: 'lettura',
  SCRITTURA: 'RUP-CUP-scrittura',
}

/**
 * Azioni per il profilo scelto da utilizzare nei filtri di ricerca nella homepage
 */
export const ACTION_SEARCH = {
  RICERCA_CUP: 'ricerca-CUP', // indica che è disponibile la ricerca per CUP
  RICERCA_CHECKLIST: 'ricerca-CL',  // indica che è disponibile la ricerca per Checklist
  RICERCA_CHECKLIST_STATO: 'ricerca-SCL',  // indica che è disponibile la ricerca per stato della Chechlist
  RICERCA_CHECKLIST_DATA_MODIFICA: 'ricerca-modCL',  // indica che è disponibile la ricerca per data di modifica della Checklist
  RICERCA_CHECKLIST_DATA_MODIFICA_RUP: 'ricerca-modCLRUP',  // indica che è disponibile la ricerca per data di modifica della Checklist da un RUP
  RICERCA_AZIENDA_SANITARIA: 'ricerca-AS',  // indica che è disponibile la ricerca per AS
  RICERCA_RUP: 'ricerca-RUP',  // indica che è disponibile la ricerca per RUP
  RICERCA_RUA: 'ricerca-RUA',  // indica che è disponibile la ricerca per RUA
  RICERCA_VERIFICA_LIV_DUE: 'ricerca-verL2',  // indica che è disponibile la ricerca per data di verifica di livello 2
  RICERCA_VERIFICA_LIV_TRE: 'ricerca-verL3',  // indica che è disponibile la ricerca per data di verifica di livello 3
}

export const CHOISE_TABLE = {
  CHECKLIST: 'checklist', // indica la scelta della checklist
  VERIFICA_2: 'verifica_2',  // indica la scelta della verifica livello 2
  VERIFICA_3: 'verifica_3',  // indica la scelta della verifica livello 3
  TITOLO_VERIFICA_2: 'Autocontrollo di Livello 2',  // indica il titolo se la scelta della verifica livello 2
  TITOLO_VERIFICA_3: 'Autocontrollo di Livello 3', // indica il titolo se  la scelta della verifica livello 3
}

export const ESITO_TABLE = {
  SI: 'SI', // indica la scelta SI
  NO: 'NO',  // indica la scelta NO
  NON_APPLICABILE: 'NON_APPLICABILE',  // indica la scelta NON_APPLICABILE
  NON_APPLICABILE_PER_AVANZAMENTO: 'NON_APPLICABILE_PER_AVANZAMENTO', // indica la scelta NON_APPLICABILE_PER_AVANZAMENTO
  CARICATA: 'Caricata', // indica la scelta SI per verificata
  NON_CARICATA: 'Non Caricata', // indica la scelta NO per verificata
  NON_APPLICABILE_VERIFICATA: 'Non applicabile',  // indica la scelta NON_APPLICABILE per verificata
  NON_APPLICABILE_PER_AVANZAMENTO_VERIFICATA: 'Non ancora applicabile', // indica la scelta NON_APPLICABILE_PER_AVANZAMENTO per verificata
  NON_COMPILATO: '*** Non indicato ***', // indica la non scelta
}