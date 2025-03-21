/*
* SPDX-FileCopyrightText: (C) Copyright 2025 Regione Piemonte
*
* SPDX-License-Identifier: EUPL-1.2 
*/

import { AfterViewInit, Component, Injectable, OnInit, ViewChild } from '@angular/core';
import { MatSidenav } from '@angular/material/sidenav';
import { Router } from '@angular/router';
import { ToastrService } from 'ngx-toastr';
import { Client } from '../Client';
import { ErrorHandlerService } from '../main-page/ErrorHandlerService';

export type MenuItem = {
  icon: string;
  label: string;
  labelTwo?: string;
  route?: string;
  href?: string;
}
const LOGIN = 'login';

@Component({
  selector: 'app-header',
  templateUrl: './header.component.html',
  styleUrls: ['./header.component.css']
})
@Injectable({
  providedIn: 'root',
})
export class HeaderComponent implements OnInit, AfterViewInit {

  @ViewChild('sidenav') sidenav!: MatSidenav;

  menuItems: MenuItem[] = [];

  opened: boolean = false;

  nome: string = '';
  cognome: string = '';
  struttura: string = '';
  ruolo: string = '';

  constructor(public client: Client, private toastr: ToastrService, public router: Router, private errorHandlerService: ErrorHandlerService) { }

  ngAfterViewInit(): void {
  }

  async ngOnInit() {

    this.menuItems = [
      {
        icon: 'bi bi-house',
        label: 'Home',
        route: 'main-page',
        href: ''
      },
      {
        icon: 'bi bi-house',
        label: 'Elenco progetti',
        route: 'main-page/progetti',
        href: ''
      }
    ];

    if (this.client.devMode) {
      this.nome = "VANESSA";
      this.cognome = "VENTUNO";
      this.struttura = "A.S.L. VERCELLI";
      this.ruolo = 'OAM';
    } else {
      if (this.client.loggedUser) {
        this.nome = this.client.loggedUser.richiedente.nome ? this.client.loggedUser.richiedente.nome : '';
        this.cognome = this.client.loggedUser.richiedente.cognome ? this.client.loggedUser.richiedente.cognome : '';
        this.struttura = this.client.loggedUser.richiedente.collocazione.descrizione_azienda ? this.client.loggedUser.richiedente.collocazione.descrizione_azienda : '';
        this.ruolo = this.client.loggedUser.richiedente.ruolo ? this.client.loggedUser.richiedente.ruolo : '';
      } else {
        this.nome = "";
        this.cognome = "";
        this.struttura = "";
        this.ruolo = '';
      }
    }

  }


  getCurrentRoute() {
    if (this.router.url === '/main-page') {
      return 'Pagina principale';
    } else if (this.router.url === '/main-page/progetti') {
      return 'Elenco Progetti';
    } else if (this.router.url.includes('/main-page/checklist/')) {
      if (this.client.selectProfile && ['RP-verifica-liv2', 'RP-verifica-liv3'].includes(this.client.selectProfile.codice)) {
        return 'Autocontrollo';
      }
      return 'Checklist';
    } else {
      return 'Pagina di login';
    }
  }

  makeLinkMenu() {
  }

  async goLogout() {
    window.location.replace(this.client.logoutUrl);
  }

  goToPaginaPersonale() {

  }


  makeDescriptionProfile(profile: string) {
    switch (profile) {
      case 'RP-verifica-liv2':
        return 'Autocontrollo - (livello 2)';
      case 'RP-verifica-liv3':
        return 'Autocontrollo - (livello 3)';
      case 'RUP-CUP-checklist':
        return 'RUP (Responsabile di progetto)';
      case 'RUA-AS-checklist':
        return 'RUA (Responsabile per l\'Azienda Sanitaria)';
      case 'RP-checklist':
        return 'Regione Piemonte';
      default:
        return '';
    }
  }
}
