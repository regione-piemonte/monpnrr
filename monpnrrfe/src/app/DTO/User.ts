/*
* SPDX-FileCopyrightText: (C) Copyright 2025 Regione Piemonte
*
* SPDX-License-Identifier: EUPL-1.2 
*/

export interface User {
  richiedente: Richiedente;
  profili?: (ProfiliEntity)[] | null;
}
export interface Richiedente {
  codice_fiscale: string;
  nome: string;
  cognome: string;
  ruolo: string;
  collocazione: Collocazione;
}
export interface Collocazione {
  codice_collocazione: string;
  descrizione_collocazione: string;
  codice_azienda: string;
  descrizione_azienda: string;
}
export interface ProfiliEntity {
  codice: string;
  descrizione: string;
  azioni: Azione[];
}

export interface Azione {
  codice: string;
  descrizione: string;
}
