/*
* SPDX-FileCopyrightText: (C) Copyright 2025 Regione Piemonte
*
* SPDX-License-Identifier: EUPL-1.2 
*/

import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { jwtDecode } from "jwt-decode";
import { Client } from '../Client';
import { lastValueFrom } from 'rxjs';
import { HeaderComponent } from '../header/header.component';
import { ToastrService } from 'ngx-toastr';
import { ProfiliEntity } from '../DTO/User';

const MAIN_PAGE = 'main-page';
const PROJECT_LIST = 'main-page/progetti';
const LOGIN_ERROR_PAGE = 'login-error-page';

@Component({
  selector: 'app-login-page',
  templateUrl: './login-page.component.html',
  styleUrls: ['./login-page.component.css']
})
export class LoginPageComponent implements OnInit {

  isSpinEmitter: boolean = false;
  selectedProfile: ProfiliEntity | null = null;

  constructor(public client: Client, private router: Router, private header: HeaderComponent, private toastr: ToastrService) { }

  async ngOnInit() {
    this.isSpinEmitter = true;
    if (this.client.devMode) {
      this.client.loggedUser = {
        richiedente: {
          codice_fiscale: 'ZZZZZZ00Z01L219A',
          cognome: 'COGNOME',
          collocazione: {
            codice_collocazione: "010301",
            descrizione_collocazione: "A.S.L.",
            codice_azienda: "010301",
            descrizione_azienda: "A.S.L."
          },
          nome: 'NOME',
          ruolo: 'OAM'
        },
        profili: [
          {
            codice: "RUP-CUP-checklist",
            descrizione: "Utente responsabile di un progetto (CUP) e della compilazione della relativa Checklist",
            azioni: [
              {
                codice: "RP-CUP-lettura",
                descrizione: "visualizza i progetti in carico a tutta la Regione e le relative Checklist"
              },
              {
                codice: "ricerca-CUP",
                descrizione: "indica che è disponibile la ricerca per CUP"
              },
              {
                codice: "ricerca-CL",
                descrizione: "indica che è disponibile la ricerca per Checklist"
              },
              {
                codice: "ricerca-SCL",
                descrizione: "indica che è disponibile la ricerca per stato della Chechlist"
              },
              {
                codice: "ricerca-modCL",
                descrizione: "indica che è disponibile la ricerca per data di modifica della Checklist"
              },
              {
                codice: "ricerca-AS",
                descrizione: "indica che è disponibile la ricerca per AS"
              },
              {
                codice: "ricerca-RUP",
                descrizione: "indica che è disponibile la ricerca per RUP"
              },
              {
                codice: "ricerca-RUA",
                descrizione: "indica che è disponibile la ricerca per RUA"
              },
              {
                codice: "ricerca-verL2",
                descrizione: "indica che è disponibile la ricerca per data di verifica di livello 2"
              },
              {
                codice: "ricerca-verL3",
                descrizione: "indica che è disponibile la ricerca per data di verifica di livello 3"
              }
            ]
          }
        ]
      };
      if (this.client.loggedUser.profili) {
        this.client.selectProfile = this.client.loggedUser.profili[0];
      }
      this.router.navigate([PROJECT_LIST], { skipLocationChange: true });
    } else {
      if (this.client.token && this.client.token !== '') {
        let params = {
          Token: this.client.token
        }
        const loginObservable = this.client.login(params);
        await lastValueFrom(loginObservable)
          .then(data => {
            if (data) {
              this.client.loggedUser = data; // Access the body (response data)
              this.header.ngOnInit();
              if (this.client.loggedUser.profili) {
                if (this.client.loggedUser.profili.length === 1) {
                  this.client.selectProfile = this.client.loggedUser.profili[0];
                  this.router.navigate([PROJECT_LIST], { skipLocationChange: true });
                }
              } else {
                this.router.navigate([LOGIN_ERROR_PAGE], { skipLocationChange: true });
              }
            } else {
              this.router.navigate([LOGIN_ERROR_PAGE], { skipLocationChange: true });
            }
          })
          .catch(
            error => {
              this.isSpinEmitter = false;
              this.toastr.error('API error [' + 'login' + ']');
              this.router.navigate([LOGIN_ERROR_PAGE], { skipLocationChange: true });
            }
          );
      } else {
        this.router.navigate([LOGIN_ERROR_PAGE], { skipLocationChange: true });
      }
    }
    this.isSpinEmitter = false;
  }

  getDecodedAccessToken(token: string): any {
    try {
      return jwtDecode(token);
    } catch (Error) {
      return null;
    }
  }

  profileChoice() {
    if (this.selectedProfile && this.selectedProfile.codice && this.client.loggedUser && this.client.loggedUser.profili) {
      const selectedProfile = this.client.loggedUser.profili.find((e) => e.codice === this.selectedProfile?.codice);
      if (selectedProfile) {
        this.client.selectProfile = selectedProfile;
        this.header.ngOnInit();
        this.router.navigate([PROJECT_LIST], { skipLocationChange: true });
      }
    }
  }
}
