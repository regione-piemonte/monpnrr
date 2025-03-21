/*
* SPDX-FileCopyrightText: (C) Copyright 2025 Regione Piemonte
*
* SPDX-License-Identifier: EUPL-1.2 
*/

export interface Progetti {
  totalCount: number,
  cup: string | null | null;
  descrizione: string | null;
  rup?: (RupEntity)[] | null;
  rua?: (RuaEntity)[] | null;
  azienda?: AziendaEntity | null;
  checklist: Checklist | null;
  ultima_modifica: UltimaModifica | null;
  ultima_verifica: UltimaVerifica | null;
  modifica_recente: boolean | null;
}

export interface AziendaEntity {
  codice: string | null;
  descrizione: string | null;
}
export interface RupEntity {
  nome: string | null;
  cognome: string | null;
}
export interface RuaEntity {
  nome: string | null;
  cognome: string | null;
}
export interface Checklist {
  codice: string | null;
  stato: string | null;
}
export interface UltimaModifica {
  utente: string | null;
  data: string | null;
}
export interface UltimaVerifica {
  utente: string | null;
  data: string | null;
}
