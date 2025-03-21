/*
* SPDX-FileCopyrightText: (C) Copyright 2025 Regione Piemonte
*
* SPDX-License-Identifier: EUPL-1.2 
*/

import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { ToastrService } from 'ngx-toastr';
import { lastValueFrom } from 'rxjs';
import { Azioni, Client } from '../Client';
import { ACTION, ACTION_SEARCH, CHOISE_TABLE } from '../constants';
import { Stato } from '../DTO/CambioStatoDTO';
import { Decodifica } from '../DTO/decodifica';
import { Progetti } from '../DTO/Progetti';

const LOGIN_ERROR_PAGE = 'login-error-page';

@Component({
  selector: 'app-project-list',
  templateUrl: './project-list.component.html',
  styleUrls: ['./project-list.component.css']
})
export class ProjectListComponent implements OnInit {

  isSpinEmitter: boolean = false;
  isSpinEmitterTable: boolean = false;
  azioni: any;

  action = ACTION;
  action_search = ACTION_SEARCH;

  // listaCambiStato: Stato[] | null = null;
  // completataObj: Stato[] | null = null;
  listaProgetti: Progetti[] | null = null;
  // columns: string[] = ['CUP', 'Descrizione', '', '', 'RUA', 'Checklist', 'Stato', 'Ultima modifica', 'Ultima verifica', 'Modifica recente', 'Azioni', '', ''];
  // columns: string[] = ['CUP', 'Descrizione', '', '', 'RUA', 'Checklist', 'Stato', 'Ultima modifica', 'Azioni', '', ''];
  columns: string[] = [];
  aziendaNominativo: string = '';
  aziendaNominativoCodice: string = '';
  checkModificaCensita: boolean = false;

  // FILTRI DI RICERCA
  labelStyle: string = 'col-md-1 d-flex align-items-center justify-content-end text-rigth generic_font_13 w-100 ml-2';
  inputStyle: string = 'col-md-4 d-flex align-items-center justify-content-center text-center w-100';

  cupText: string | null = null;
  isValidInput: boolean = true;

  aziendaSanitariaVuoto: string | null = null;
  selectedAziendaSanitaria: Decodifica | null = null;

  rupVuoto: Decodifica | null = null;
  selectedRup: Decodifica | null = null;

  ruaVuoto: Decodifica | null = null;
  selectedRua: Decodifica | null = null;

  checklistVuoto: Decodifica | null = null;
  selectedChecklist: Decodifica | null = null;

  statoChecklistVuoto: Decodifica | null = null;
  selectedStatoChecklist: Decodifica | null = null;

  modificaDa: any;
  modificaA: any;
  verificaLivelloDueDa: any;
  verificaLivelloDueA: any;
  verificaLivelloTreDa: any;
  verificaLivelloTreA: any;

  modificaDaRup: any;
  modificaDaRupA: any;

  siLiv2: boolean = false;
  siLiv3: boolean = false;
  siModificaRup: boolean = false;

  page: number = 1;
  pageSize: number = 5;
  collectionSize: number = 0;
  descending: boolean = false;
  orderBy: string = 'cup';
  selectedColumn: string = '';

  // GESTIONE ERRORI
  errorText: string = '';
  isWarning: boolean = false;

  constructor(public client: Client, private toastr: ToastrService, private router: Router, private activeRoute: ActivatedRoute) {
    this.azioni = Azioni;
  }

  async ngOnInit() {
    if (!this.client.loggedUser && !this.client.devMode) {
      this.router.navigate([LOGIN_ERROR_PAGE], { skipLocationChange: true });
    }

    await this.getDateInit();
    if (this.client.dateInit) {
      if (this.client.dateInit.modificaRupA) {
        let part = this.client.dateInit.modificaRupA.split('-');
        let date = {
          year: parseInt(part[0], 10),
          month: parseInt(part[1], 10),
          day: parseInt(part[2], 10)
        }
        this.modificaA = date;
      }
      if (this.client.dateInit.modificaRupDa) {
        let part = this.client.dateInit.modificaRupDa.split('-');
        let date = {
          year: parseInt(part[0], 10),
          month: parseInt(part[1], 10),
          day: parseInt(part[2], 10)
        }
        this.modificaDa = date;
      }
      if (this.client.dateInit.modificaAutocontrolloLiv2A) {
        this.siLiv2 = true;
        let part = this.client.dateInit.modificaAutocontrolloLiv2A.split('-');
        let date = {
          year: parseInt(part[0], 10),
          month: parseInt(part[1], 10),
          day: parseInt(part[2], 10)
        }
        this.verificaLivelloDueA = date;
      }
      if (this.client.dateInit.modificaAutocontrolloLiv2Da) {
        this.siLiv2 = true;
        let part = this.client.dateInit.modificaAutocontrolloLiv2Da.split('-');
        let date = {
          year: parseInt(part[0], 10),
          month: parseInt(part[1], 10),
          day: parseInt(part[2], 10)
        }
        this.verificaLivelloDueDa = date;
      }
      if (this.client.dateInit.modificaAutocontrolloLiv3A) {
        this.siLiv3 = true;
        let part = this.client.dateInit.modificaAutocontrolloLiv3A.split('-');
        let date = {
          year: parseInt(part[0], 10),
          month: parseInt(part[1], 10),
          day: parseInt(part[2], 10)
        }
        this.verificaLivelloTreA = date;
      }
      if (this.client.dateInit.modificaAutocontrolloLiv3Da) {
        this.siLiv3 = true;
        let part = this.client.dateInit.modificaAutocontrolloLiv3Da.split('-');
        let date = {
          year: parseInt(part[0], 10),
          month: parseInt(part[1], 10),
          day: parseInt(part[2], 10)
        }
        this.verificaLivelloTreDa = date;
      }
    }

    if (this.client.selectProfile) {
      this.isSpinEmitter = true;
      this.aziendaNominativo = this.client.loggedUser ? this.client.loggedUser.richiedente.collocazione.descrizione_azienda : '';
      this.aziendaNominativoCodice = this.client.loggedUser ? this.client.loggedUser.richiedente.collocazione.codice_azienda : '';

      if (['RUA-AS-checklist', 'RUP-CUP-checklist'].includes(this.client.selectProfile.codice)) {
        if (!this.selectedAziendaSanitaria) {
          this.selectedAziendaSanitaria = { codice: this.aziendaNominativoCodice.slice(-3) };
        } else {
          this.selectedAziendaSanitaria.codice = this.aziendaNominativoCodice.slice(-3);
        }
      }
      // Inserisco headers dinamici ------------------------
      this.columns.push('CUP');
      this.columns.push('Descrizione');
      this.columns.push('');
      this.columns.push('');
      if (['RP-checklist', 'RP-verifica-liv2', 'RP-verifica-liv3'].includes(this.client.selectProfile.codice)) {
        this.columns.push('AS');
        this.columns.push('RUA');
        this.columns.push('RUP');
      } else {
        this.columns.push('RUA');
      }
      this.columns.push('Checklist');
      this.columns.push('Stato');
      this.columns.push('Ultima modifica');
      if (['RP-checklist', 'RP-verifica-liv2', 'RP-verifica-liv3'].includes(this.client.selectProfile.codice)) {
        this.columns.push('Autocontrollo');
        this.columns.push('Modifica recente');
      }
      this.columns.push('Azioni');
      this.columns.push('');
      this.columns.push('');
      // ----------------------------------------------------
      await this.getListaProgetti();
      await this.getListaCambiStati();
      if (this.client.listaCambiStato) {
        this.client.completataObj = this.client.listaCambiStato.filter((e) => e.statoCod === 'COMPLETATA');
      }
      this.listaProgetti ? this.checkModificaCensita = this.listaProgetti.some((e) => e.modifica_recente === true) : this.checkModificaCensita = false;
      this.client.activeTab = 0; // Resetto la modalita' CUP tab selzionata
      // GETS FOR DECODIFICHE FILTRI
      await this.getFiltroChecklist();
      await this.getFiltroAziendaSanitaria();
      await this.getFiltroRua();
      await this.getFiltroRup();
      this.isSpinEmitter = false;
    } else {
      this.toastr.error('API error [' + 'selezione del profilo' + ']');
    }
  }

  async getListaProgetti() {
    const params = {
      cup: this.cupText ? this.cupText : null,
      checklist: this.selectedChecklist ? this.selectedChecklist.codice : null,
      stato: this.selectedStatoChecklist ? this.selectedStatoChecklist.codice : null,
      modificaRupDa: this.modificaDa ? `${this.modificaDa.year}-${this.modificaDa.month}-${this.modificaDa.day}` : null,
      modificaRupA: this.modificaA ? `${this.modificaA.year}-${this.modificaA.month}-${this.modificaA.day}` : null,
      aziendaSanitaria: this.selectedAziendaSanitaria ? this.selectedAziendaSanitaria.codice : null,
      rua: this.selectedRua ? this.selectedRua.codice : null,
      rup: this.selectedRup ? this.selectedRup.codice : null,
      modificaVerifica2Da: this.verificaLivelloDueDa ? `${this.verificaLivelloDueDa.year}-${this.verificaLivelloDueDa.month}-${this.verificaLivelloDueDa.day}` : null,
      modificaVerifica2A: this.verificaLivelloDueA ? `${this.verificaLivelloDueA.year}-${this.verificaLivelloDueA.month}-${this.verificaLivelloDueA.day}` : null,
      modificaVerifica3Da: this.verificaLivelloTreDa ? `${this.verificaLivelloTreDa.year}-${this.verificaLivelloTreDa.month}-${this.verificaLivelloTreDa.day}` : null,
      modificaVerifica3A: this.verificaLivelloTreA ? `${this.verificaLivelloTreA.year}-${this.verificaLivelloTreA.month}-${this.verificaLivelloTreA.day}` : null,
      modificaRupDopoVerificaDa: this.modificaDaRup ? `${this.modificaDaRup.year}-${this.modificaDaRup.month}-${this.modificaDaRup.day}` : null,
      modificaRupDopoVerificaA: this.modificaDaRupA ? `${this.modificaDaRupA.year}-${this.modificaDaRupA.month}-${this.modificaDaRupA.day}` : null,
      verificatoLiv2: this.siLiv2,
      verificatoLiv3: this.siLiv3,
      modificatoRupDopoVerifica: this.siModificaRup,
      orderBy: this.orderBy,
      descending: this.descending,
      pageNumber: this.page,
      rowPerPage: this.pageSize,
    }
    await lastValueFrom(this.client.getListaProgetti(params))
      .then(data => {
        if (data) {
          this.listaProgetti = data;
          if (this.listaProgetti.length > 0 && this.listaProgetti[0].totalCount) {
            this.collectionSize = this.listaProgetti[0].totalCount;
          }
        }
      })
      .catch(
        error => {
          this.toastr.error('API error [' + 'getListaProgetti' + ']');
          this.listaProgetti = null;
          this.collectionSize = 0;
          this.isSpinEmitter = false;
        }
      );
  }

  async getDateInit() {
    if (!this.client.dateInit) {
      await lastValueFrom(this.client.getDateInit())
        .then(data => {
          if (data) {
            this.client.dateInit = data;
          }
        })
        .catch(
          error => {
            this.toastr.error('API error [' + 'getDateInit' + ']');
            this.isSpinEmitter = false;
          }
        );
    }
  }

  async getListaCambiStati() {
    if (!this.client.listaCambiStato) {
      await lastValueFrom(this.client.getListaCambiStati())
        .then(data => {
          if (data) {
            this.client.listaCambiStato = data;
            this.client.listaDecodificaStatoChecklist = this.client.listaCambiStato.map((e) => ({
              codice: e.statoCod ? e.statoCod : '',
              descrizione: e.statoDesc ? e.statoDesc : ''
            }));
          }
        })
        .catch(
          error => {
            this.toastr.error('API error [' + 'getListaCambiStati' + ']');
            this.isSpinEmitter = false;
          }
        );
    }
  }

  async getFiltroChecklist() {
    if (!this.client.listaDecodificaChecklist) {
      await lastValueFrom(this.client.getFiltroChecklist())
        .then(data => {
          if (data) {
            this.client.listaDecodificaChecklist = data;
          }
        })
        .catch(
          error => {
            this.toastr.error('API error [' + 'getFiltroChecklist' + ']');
            this.isSpinEmitter = false;
          }
        );
    }
  }
  async getFiltroAziendaSanitaria() {
    if (!this.client.listaDecodificaAziendaSanitaria) {
      await lastValueFrom(this.client.getFiltroAziendaSanitaria())
        .then(data => {
          if (data) {
            this.client.listaDecodificaAziendaSanitaria = data;
          }
        })
        .catch(
          error => {
            this.toastr.error('API error [' + 'getFiltroAziendaSanitaria' + ']');
            this.isSpinEmitter = false;
          }
        );
    }
  }
  async getFiltroRua() {
    if (!this.client.listaDecodificaRua) {
      await lastValueFrom(this.client.getFiltroRua())
        .then(data => {
          if (data) {
            this.client.listaDecodificaRua = data;
          }
        })
        .catch(
          error => {
            this.toastr.error('API error [' + 'getFiltroRua' + ']');
            this.isSpinEmitter = false;
          }
        );
    }
  }
  async getFiltroRup() {
    if (!this.client.listaDecodificaRup) {
      await lastValueFrom(this.client.getFiltroRup())
        .then(data => {
          if (data) {
            this.client.listaDecodificaRup = data;
          }
        })
        .catch(
          error => {
            this.toastr.error('API error [' + 'getFiltroRup' + ']');
            this.isSpinEmitter = false;
          }
        );
    }
  }

  showDettaglio(cup: string | null) {
    this.router.navigate(['../checklist', cup, 'readOnly', CHOISE_TABLE.CHECKLIST], { relativeTo: this.activeRoute, skipLocationChange: true })
  }

  showModifica(cup: string | null) {
    this.router.navigate(['../checklist', cup, 'edit', CHOISE_TABLE.CHECKLIST], { relativeTo: this.activeRoute, skipLocationChange: true })
  }
  showVerificaLivelloDue(cup: string | null) {
    this.router.navigate(['../checklist', cup, 'edit', CHOISE_TABLE.VERIFICA_2], { relativeTo: this.activeRoute, skipLocationChange: true })
  }
  showVerificaLivelloTre(cup: string | null) {
    this.router.navigate(['../checklist', cup, 'edit', CHOISE_TABLE.VERIFICA_3], { relativeTo: this.activeRoute, skipLocationChange: true })
  }

  async downloadPdf(cupNumber: string | null) {
    this.isSpinEmitter = true;
    if (!cupNumber) {
      this.isSpinEmitter = false;
      this.toastr.error('API error [' + 'downloadPdf' + ']');
      return;
    }
    if (this.client.loggedUser) {
      let params = {
        aziendaSanitaria: this.client.loggedUser.richiedente.collocazione.codice_azienda.slice(-3)
      }
      await lastValueFrom(this.client.getProgettoPDF(cupNumber, params))
        .then(blob => {
          const headers = blob.headers;
          if (blob.headers) {
            const fileNameHeader = headers.get('Content-Disposition');
            if (fileNameHeader) {
              if (blob.body) {
                let fileURL = URL.createObjectURL(blob.body);
                const a = document.createElement('a');
                document.body.appendChild(a);
                a.style.display = 'none';
                a.href = fileURL;
                a.download = fileNameHeader;
                a.click();
                window.URL.revokeObjectURL(fileURL);
                document.body.removeChild(a);
              }
            }
          }
          this.isSpinEmitter = false;
        })
        .catch(
          error => {
            this.isSpinEmitter = false;
            this.toastr.error('API error [' + 'downloadPdf' + ']');
          }
        );
    }
  }

  checkModificaStatoCompletata(progetto: Progetti): boolean {
    let listaStatiFiltered: Stato[] = [];
    if (this.client.listaCambiStato) {
      if (this.client.completataObj && this.client.completataObj.length === 1 && this.client.completataObj[0].statoOrdinamento) {
        const ordinamento = this.client.completataObj[0].statoOrdinamento;
        listaStatiFiltered = this.client.listaCambiStato.filter((e) => e.statoOrdinamento >= ordinamento);
      }
    }
    for (let s of listaStatiFiltered) {
      if (s.statoDesc === progetto.checklist?.stato) {
        return true;
      }
    }
    return false;
  }

  async downloadCsv() {
    this.isSpinEmitter = true;
    const params = {
      cup: this.cupText ? this.cupText : null,
      checklist: this.selectedChecklist ? this.selectedChecklist.codice : null,
      stato: this.selectedStatoChecklist ? this.selectedStatoChecklist.codice : null,
      modificaRupDa: this.modificaDa ? `${this.modificaDa.year}-${this.modificaDa.month}-${this.modificaDa.day}` : null,
      modificaRupA: this.modificaA ? `${this.modificaA.year}-${this.modificaA.month}-${this.modificaA.day}` : null,
      aziendaSanitaria: this.selectedAziendaSanitaria ? this.selectedAziendaSanitaria.codice : null,
      rua: this.selectedRua ? this.selectedRua.codice : null,
      rup: this.selectedRup ? this.selectedRup : null,
      modificaVerifica2Da: this.verificaLivelloDueDa ? `${this.verificaLivelloDueDa.year}-${this.verificaLivelloDueDa.month}-${this.verificaLivelloDueDa.day}` : null,
      modificaVerifica2A: this.verificaLivelloDueA ? `${this.verificaLivelloDueA.year}-${this.verificaLivelloDueA.month}-${this.verificaLivelloDueA.day}` : null,
      modificaVerifica3Da: this.verificaLivelloTreDa ? `${this.verificaLivelloTreDa.year}-${this.verificaLivelloTreDa.month}-${this.verificaLivelloTreDa.day}` : null,
      modificaVerifica3A: this.verificaLivelloTreA ? `${this.verificaLivelloTreA.year}-${this.verificaLivelloTreA.month}-${this.verificaLivelloTreA.day}` : null,
      modificaRupDopoVerificaDa: this.modificaDaRup ? `${this.modificaDaRup.year}-${this.modificaDaRup.month}-${this.modificaDaRup.day}` : null,
      modificaRupDopoVerificaA: this.modificaDaRupA ? `${this.modificaDaRupA.year}-${this.modificaDaRupA.month}-${this.modificaDaRupA.day}` : null,
      verificatoLiv2: this.siLiv2,
      verificatoLiv3: this.siLiv3,
      modificatoRupDopoVerifica: this.siModificaRup
    }
    await lastValueFrom(this.client.getProgettoCsv(params))
      .then(blob => {
        const headers = blob.headers;
        if (blob.headers) {
          const fileNameHeader = headers.get('Content-Disposition');
          if (fileNameHeader) {
            if (blob.body) {
              let fileURL = URL.createObjectURL(blob.body);
              const a = document.createElement('a');
              document.body.appendChild(a);
              a.style.display = 'none';
              a.href = fileURL;
              a.download = fileNameHeader;
              a.click();
              window.URL.revokeObjectURL(fileURL);
              document.body.removeChild(a);
            }
          }
        }
        this.isSpinEmitter = false;
      })
      .catch(
        error => {
          this.isSpinEmitter = false;
          this.toastr.error('API error [' + 'downloadCsv' + ']');
        }
      );
  }

  // FILTRI DI RICERCA
  eraseData(type: string) {
    switch (type) {
      case 'modificaDa':
        this.modificaDa = null;
        break;
      case 'modificaA':
        this.modificaA = null;
        break;
      case 'verificaLivelloDueDa':
        this.verificaLivelloDueDa = null;
        break;
      case 'verificaLivelloDueA':
        this.verificaLivelloDueA = null;
        break;
      case 'verificaLivelloTreDa':
        this.verificaLivelloTreDa = null;
        break;
      case 'verificaLivelloTreA':
        this.verificaLivelloTreA = null;
        break;
      case 'modificaDaRup':
        this.modificaDaRup = null;
        break;
      case 'modificaDaRupA':
        this.modificaDaRupA = null;
        break;
      default:
        break;
    }
  }

  async cerca() {
    this.isSpinEmitterTable = true;
    await this.getListaProgetti();
    this.isSpinEmitterTable = false;
  }

  async cercaButton() {
    this.isSpinEmitterTable = true;
    this.page = 1;
    this.pageSize = 5;
    this.collectionSize = 0;
    this.descending = false;
    this.orderBy = 'cup';
    this.selectedColumn = '';
    await this.getListaProgetti();
    this.isSpinEmitterTable = false;
  }

  validateCupText() {
    if (this.cupText && this.cupText !== '') {
      const regex = /^[a-zA-Z0-9]{0,15}$/;
      this.isValidInput = regex.test(this.cupText);
      if (!this.isValidInput) {
        this.isWarning = false;
        this.errorText = 'Attenzione: non è possibile completare l’operazione di ricerca. Il codice CUP inserito non è valido, deve contenere un massimo di 15 caratteri alfanumerici.';
      }
    } else if (this.cupText === '') {
      this.isValidInput = true;
    }
  }

  async onHeaderClick(column: string) {
    if (!['', 'Azioni'].includes(column)) {
      this.isSpinEmitterTable = true;
      this.descending = !this.descending;
      this.selectedColumn = column;
      switch (column) {
        case 'CUP':
          this.orderBy = 'cup';
          break;
        case 'Descrizione':
          this.orderBy = 'descrizione';
          break;
        case 'RUP':
          this.orderBy = 'rup';
          break;
        case 'AS':
          this.orderBy = 'asl';
          break;
        case 'RUA':
          this.orderBy = 'rua';
          break;
        case 'Stato':
          this.orderBy = 'stato';
          break;
        case 'Checklist':
          this.orderBy = 'checklist';
          break;
        case 'Ultima modifica':
          this.orderBy = 'ultimamodifica';
          break;
        case 'Autocontrollo':
          this.orderBy = 'autocontrollo';
          break;
        case 'Modifica recente':
          this.orderBy = 'modificarecente';
          break;
        default:
          this.orderBy = '';
          break;
      }
      await this.getListaProgetti();
      this.isSpinEmitterTable = false;
    }
  }

  siToggle(toggle: string) {
    switch (toggle) {
      case 'siLiv2':
        this.verificaLivelloDueDa = null;
        this.verificaLivelloDueA = null;
        break;
      case 'siLiv3':
        this.verificaLivelloTreDa = null;
        this.verificaLivelloTreA = null;
        break;
      case 'siModificaRup':
        this.modificaDaRup = null;
        this.modificaDaRupA = null;
        break;
      default:
        this.orderBy = '';
        break;
    }
  }

}
