/*
* SPDX-FileCopyrightText: (C) Copyright 2025 Regione Piemonte
*
* SPDX-License-Identifier: EUPL-1.2 
*/

import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { Client } from '../Client';
import {ProceduraCig} from "../DTO/proceduraCig";

const LOGIN_ERROR_PAGE = 'login-error-page';
@Component({
  selector: 'app-main-page',
  templateUrl: './main-page.component.html',
  styleUrls: ['./main-page.component.css']
})
export class MainPageComponent implements OnInit {

  nome: string = '';
  cognome: string = '';
  struttura: string = '';
  ruolo: string = '';
  asr: string = '';
  collocazione: string = '';

  isSpinEmitter: boolean = false;
  panelOpenState: boolean = true;
  loadDettaglioArpe: boolean = false;

  isFeatureEnabled: boolean = false;

  progressivoId: number = 4;


  cigList: ProceduraCig[] = [
    {
      descrizione_procedura_cig: 'Servizi',
      progressivo: 1,
      numero_cig: '1234567890'
    },
    {
      descrizione_procedura_cig: 'Accessori',
      progressivo: 2,
      numero_cig: '0987654321'
    },
    {
      descrizione_procedura_cig: 'Lavori',
      progressivo: 3,
      numero_cig: '1122334455'
    }
  ];

  constructor(public router: Router, public client: Client) { }

  ngOnInit(): void {
    if (!this.client.loggedUser && !this.client.devMode) {
      this.router.navigate([LOGIN_ERROR_PAGE], {skipLocationChange: true});
    }
  }

  newCig(data: ProceduraCig) {
    this.cigList.push({
      descrizione_procedura_cig: data.descrizione_procedura_cig,
      progressivo: this.progressivoId++,
      numero_cig: data.numero_cig
    });
  }

  deleteCig(data: ProceduraCig)
  {
    const index = this.cigList.findIndex(item => item === data);
    if (index !== -1) {
      this.cigList.splice(index, 1);
    } else {
      console.log('Item not found in the list.');
    }
  }

  showCig(data: ProceduraCig) {
    // console.log('Show CIG:' + data);
  }

  editCig(data: ProceduraCig) {
    // console.log('Edit CIG:' + data);
  }
}
