/*
* SPDX-FileCopyrightText: (C) Copyright 2025 Regione Piemonte
*
* SPDX-License-Identifier: EUPL-1.2 
*/

export interface Stato {
  statoCod: string | null;
  statoDesc: string | null;
  statoOrdinamento: number;
  altriStati?: (AltriStatiEntity)[] | null;
}
export interface AltriStatiEntity {
  statoCod: string;
  statoDesc: string;
  statoOrdinamento: number;
}
