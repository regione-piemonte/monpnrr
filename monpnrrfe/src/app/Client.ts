/*
* SPDX-FileCopyrightText: (C) Copyright 2025 Regione Piemonte
*
* SPDX-License-Identifier: EUPL-1.2 
*/

import { HttpClient, HttpParams, HttpResponse } from "@angular/common/http";
import { Injectable } from "@angular/core";

import { Observable } from "rxjs";
import { environment } from "src/environments/environment";
import { Azienda } from "./DTO/azienda";
import { Stato } from "./DTO/CambioStatoDTO";
import { ChecklistProgettoList } from "./DTO/checklistProgettoList";
import { ChecklistRispostaList, ChecklistRispostaListAutocontrollo } from "./DTO/checklistRispostaList";
import { Decodifica } from "./DTO/decodifica";
import { Procedure } from "./DTO/Procedure";
import { Progetti } from "./DTO/Progetti";
import { ProgettoDettaglio } from "./DTO/progettoDettaglio";
import { ProfiliEntity, User } from "./DTO/User";
import { AutocontrolloSwap } from "./DTO/AutocontrolloDTO";
import { DateInit } from "./DTO/DateInitDTO";

type TokenType = {
  "x-access-token": string
}

const enum PathApi {
  login = '/login',
  // LISTA PROGETTI
  getListaProgetti = '/progetti',
  getListaCambiStati = '/decodifiche/cambiStato',
  getProgettoCsv = '/progetti/csv',
  getDateInit = '/decodifiche/dateModifica',
  // AZIENDE
  getAziende = '/aziende',
  // RISPOSTE
  getRisposte = '/decodifiche/risposte',
  getListaProcedure = '/decodifiche/procedure',
  // FILTRI RICERCA
  getFiltroChecklist = '/decodifiche/checklist',
  getFiltroAziendaSanitaria = '/decodifiche/aziendaSanitaria',
  getFiltroRua = '/decodifiche/rua',
  getFiltroRup = '/decodifiche/rup',
}

export enum Azioni {
  RUP_CUP_LETTURA = 'RUP-CUP-lettura',
  RUP_CUP_SCRITTURA = 'RUP-CUP-scrittura',
  VERIFICA_LIVELLO_DUE = 'RP-CUP-verifica-liv2',
  VERIFICA_LIVELLO_TRE = 'RP-CUP-verifica-liv3'
}

@Injectable({ providedIn: 'root' })
export class Client {

  // LOGIN AND HTTP CALLS
  // devMode: boolean = true;
  devModeLogin: boolean = false;

  devMode: boolean = false;
  // devModeLogin: boolean = true;

  private _baseUrl: string = environment.endpoint;
  private _logoutUrl: string = environment.logout;
  private _token: string = '';
  private _tokenJwt: any;
  private _tokenJwtProfili: any;
  private _isErrorLogin: any;

  private _tokenApplication: TokenType = {
    "x-access-token": ''
  };
  public loggedUser: User | null = null;
  public selectProfile: ProfiliEntity | null = null;

  dateInit: DateInit | null = null;

  // HOMEPAGE
  listaCambiStato: Stato[] | null = null;
  completataObj: Stato[] | null = null;

  // CHECKLIST
  modality: string = '';
  activeTab: number = 0;
  mancanzaCigError = '';

  // DECODIFICHE FILTRI
  listaDecodificaChecklist: Decodifica[] | null = null;
  listaDecodificaStatoChecklist: Decodifica[] | null = null;
  listaDecodificaAziendaSanitaria: Decodifica[] | null = null;
  listaDecodificaRua: Decodifica[] | null = null;
  listaDecodificaRup: Decodifica[] | null = null;

  choises: Decodifica[] | null = null;
  // AUTOCONTROLLO
  autocontrolloSwap: AutocontrolloSwap = {
    checkboxSwap: false,
    checkboxSwapCartaceo: false,
    notaSwap: null,
    notaSwapCartaceo: null
  };

  constructor(private http: HttpClient) { }

  addProfilePath(): string {
    return this.selectProfile ? this.selectProfile.codice : '';
  }

  // LOGIN -------------------------------------------------------------
  login(params: any): Observable<User> {
    //Converti i parametri in un oggetto HttpParams
    let httpParams = new HttpParams();
    if (params) {
      Object.keys(params).forEach((key) => {
        httpParams = httpParams.append(key, params[key]);
      });
    }
    const url = this.baseUrl + PathApi.login;
    return this.http.get<User>(url, { params: httpParams });
  }

  checkAzioni(codAzione: string): boolean {
    if (this.loggedUser && this.loggedUser.profili && this.loggedUser.profili.length > 0) {
      if (this.selectProfile && this.selectProfile.azioni.length > 0) {
        return this.selectProfile.azioni.some((e) => e.codice === codAzione);
      } else {
        return false;
      }
    } else {
      return false;
    }
  }

  checkFiltriDiRicerca() {
    if (this.loggedUser && this.loggedUser.profili && this.loggedUser.profili.length > 0) {
      if (this.selectProfile && this.selectProfile.azioni.length > 0) {
        let flag: boolean = false;
        this.selectProfile.azioni.forEach((e) => {
          if (e.codice.includes('ricerca')) {
            flag = true;
          }
        });
        return flag;
      } else {
        return false;
      }
    } else {
      return false;
    }
  }

  // LISTA PROGETTI
  getListaProgetti(params: any): Observable<Progetti[]> {
    let httpParams = new HttpParams();
    if (params) {
      Object.keys(params).forEach((key) => {
        httpParams = httpParams.append(key, params[key]);
      });
    }
    const url = this.baseUrl + PathApi.getListaProgetti + '/' + this.addProfilePath();
    return this.http.get<Progetti[]>(url, { params: httpParams });
  }

  getProgettoPDF(cupNumber: string, params: any): Observable<HttpResponse<Blob>> {
    let httpParams = new HttpParams();
    if (params) {
      Object.keys(params).forEach((key) => {
        httpParams = httpParams.append(key, params[key]);
      });
    }
    const url = this.baseUrl + `/progetti/${cupNumber}/pdf` + '/' + this.addProfilePath();
    return this.http.get(url, { params: httpParams, responseType: 'blob', observe: 'response' });
  }
  getListaCambiStati(): Observable<Stato[]> {
    let httpParams = new HttpParams();
    const url = this.baseUrl + PathApi.getListaCambiStati + '/' + this.addProfilePath();
    return this.http.get<Stato[]>(url, { params: httpParams });
  }
  getProgettoCsv(params: any): Observable<HttpResponse<Blob>> {
    let httpParams = new HttpParams();
    if (params) {
      Object.keys(params).forEach((key) => {
        httpParams = httpParams.append(key, params[key]);
      });
    }
    const url = this.baseUrl + PathApi.getProgettoCsv + '/' + this.addProfilePath();
    return this.http.get(url, { params: httpParams, responseType: 'blob', observe: 'response' });
  }
  getDateInit(): Observable<DateInit> {
    let httpParams = new HttpParams();
    const url = this.baseUrl + PathApi.getDateInit + '/' + this.addProfilePath();
    return this.http.get<DateInit>(url, { params: httpParams });
  }

  // AZIENDE
  getAziende(): Observable<Azienda> {
    let httpParams = new HttpParams();
    const url = this.baseUrl + PathApi.getAziende + '/' + this.addProfilePath();
    return this.http.get<Azienda>(url, { params: httpParams });
  }

  // RISPOSTE
  getRisposte(): Observable<Decodifica[]> {
    let httpParams = new HttpParams();
    const url = this.baseUrl + PathApi.getRisposte + '/' + this.addProfilePath();
    return this.http.get<Decodifica[]>(url, { params: httpParams });
  }

  // DETTAGLIO PROGETTO
  getDettaglioProgetto(progetto: string): Observable<ProgettoDettaglio> {
    let httpParams = new HttpParams();
    const url = this.baseUrl + `/progetti/${progetto}/dettaglio` + '/' + this.addProfilePath();
    return this.http.get<ProgettoDettaglio>(url, { params: httpParams });
  }

  // CHECKLIST
  getCheckListProgetto(progetto: string): Observable<ChecklistProgettoList> {
    let httpParams = new HttpParams();
    const url = this.baseUrl + `/progetti/${progetto}/checklist` + '/' + this.addProfilePath();
    return this.http.get<ChecklistProgettoList>(url, { params: httpParams });
  }

  // SALVA CHECKLIST
  putCheckList(progetto: string, checkList: ChecklistRispostaList): Observable<string> {
    let httpParams = new HttpParams();
    const url = this.baseUrl + `/progetti/${progetto}/checklist` + '/' + this.addProfilePath();
    return this.http.put<string>(url, checkList, { params: httpParams });
  }

  getProcedure(): Observable<Procedure[]> {
    let httpParams = new HttpParams();
    const url = this.baseUrl + PathApi.getListaProcedure + '/' + this.addProfilePath();
    return this.http.get<Procedure[]>(url, { params: httpParams });
  }

  // DECODIFICHE FILTRI DI RICERCA
  getFiltroChecklist(): Observable<Decodifica[]> {
    let httpParams = new HttpParams();
    const url = this.baseUrl + PathApi.getFiltroChecklist + '/' + this.addProfilePath();
    return this.http.get<Decodifica[]>(url, { params: httpParams });
  }
  getFiltroAziendaSanitaria(): Observable<Decodifica[]> {
    let httpParams = new HttpParams();
    const url = this.baseUrl + PathApi.getFiltroAziendaSanitaria + '/' + this.addProfilePath();
    return this.http.get<Decodifica[]>(url, { params: httpParams });
  }
  getFiltroRua(): Observable<Decodifica[]> {
    let httpParams = new HttpParams();
    const url = this.baseUrl + PathApi.getFiltroRua + '/' + this.addProfilePath();
    return this.http.get<Decodifica[]>(url, { params: httpParams });
  }
  getFiltroRup(): Observable<Decodifica[]> {
    let httpParams = new HttpParams();
    const url = this.baseUrl + PathApi.getFiltroRup + '/' + this.addProfilePath();
    return this.http.get<Decodifica[]>(url, { params: httpParams });
  }

  // AUTOCONTONTROLLO
  putCheckListAutocontrollo(progetto: string, checkList: ChecklistRispostaListAutocontrollo): Observable<string> {
    let httpParams = new HttpParams();
    const url = this.baseUrl + `/progetti/${progetto}/checklist/verifica` + '/' + this.addProfilePath();
    return this.http.put<string>(url, checkList, { params: httpParams });
  }

  // GETTERS AND SETTERS -------------------------------------------------
  public get baseUrl(): string {
    return this._baseUrl;
  }
  public set baseUrl(value: string) {
    this._baseUrl = value;
  }

  public get token(): string {
    return this._token;
  }
  public set token(value: string) {
    this._token = value;
  }

  public get tokenApplication(): TokenType {
    return this._tokenApplication;
  }
  public set tokenApplication(value: TokenType) {
    this._tokenApplication = value;
  }

  public get tokenJwt(): any {
    return this._tokenJwt;
  }
  public set tokenJwt(value: any) {
    this._tokenJwt = value;
  }

  public get tokenJwtProfili(): any {
    return this._tokenJwtProfili;
  }
  public set tokenJwtProfili(value: any) {
    this._tokenJwtProfili = value;
  }

  public get isErrorLogin(): boolean {
    return this._isErrorLogin;
  }
  public set isErrorLogin(value: boolean) {
    this._isErrorLogin = value;
  }
  public get logoutUrl(): string {
    return this._logoutUrl;
  }
  public set logoutUrl(value: string) {
    this._logoutUrl = value;
  }

  gotoAssistenza() {
    window.open('https://assistenzapua.sistemapiemonte.it/#/MONPNRR', '_blank');
  }

  openUrlPdf() {
    const pdfUrl = 'assets/Manuale utilizzo Strumenti monitoraggio PNRR.pdf';

    // Crea un elemento <a> e simula un click per scaricare
    const link = document.createElement('a');
    link.href = pdfUrl;
    link.download = 'Manuale utilizzo Strumenti monitoraggio PNRR.pdf'; // Nome del file per il download
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);
  }

}
