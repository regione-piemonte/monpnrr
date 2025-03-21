/*
* SPDX-FileCopyrightText: (C) Copyright 2025 Regione Piemonte
*
* SPDX-License-Identifier: EUPL-1.2 
*/

import { ChangeDetectorRef, Component, ElementRef, OnInit, ViewChild } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { MatTabChangeEvent } from "@angular/material/tabs";
import { ActivatedRoute, Router } from '@angular/router';
import * as _ from 'lodash';
import { ToastrService } from 'ngx-toastr';
import { lastValueFrom } from 'rxjs';
import { Azioni, Client } from '../Client';
import { Stato } from '../DTO/CambioStatoDTO';
import { ChecklistProgetto } from '../DTO/checklistProgetto';
import { ChecklistProgettoList } from '../DTO/checklistProgettoList';
import { ChecklistRisposta, ChecklistRispostaAutocontrollo } from '../DTO/checklistRisposta';
import { ChecklistRispostaList } from '../DTO/checklistRispostaList';
import { Decodifica } from "../DTO/decodifica";
import { ProceduraCig } from "../DTO/proceduraCig";
import { ProceduraRisposteCig, ProceduraRisposteCigAutocontrollo } from '../DTO/proceduraRisposteCig';
import { ProgettoDettaglio } from '../DTO/progettoDettaglio';
import { ChecklistCupTableComponent } from './checklist-cup-table/checklist-cup-table.component';
import { VerificataCupTableComponent } from './verificata-cup-table/verificata-cup-table.component';
import { CHOISE_TABLE } from '../constants';

const CUP_TAB = 0;
const CIG_TAB = 1;

@Component({
  selector: 'checklist-page',
  templateUrl: './checklist-page.component.html',
  styleUrls: ['./checklist-page.component.css']
})
export class ChecklistPageComponent implements OnInit {

  @ViewChild('topOfPage') topOfPage!: ElementRef;

  @ViewChild('tablecup') cupTable!: ChecklistCupTableComponent;
  @ViewChild('tablecig') cigTable!: ChecklistCupTableComponent;
  @ViewChild('tablevercup') cupverTable!: VerificataCupTableComponent;
  @ViewChild('tablevercig') cigverTable!: VerificataCupTableComponent;
  isSpinEmitter: boolean = false;

  cupId: string = '';
  // modality: string = '';
  readOnly: boolean = false;
  readOnlyButton: boolean = false;

  showCigMode: boolean = false;
  editCigMode: boolean = false;


  asl: string | any = '';
  dettaglio: ProgettoDettaglio | null = null;

  choises: Decodifica[] = [];
  checklist: ChecklistProgettoList | null = null;
  checkListCUP: ChecklistProgetto | null = null;
  checkListCIG: ChecklistProgetto[] = [];
  checkListCIGInitial: ChecklistProgetto[] = [];
  cigCheckEmpty: ChecklistProgetto | null = null;
  cigClone: ChecklistProgetto | null = null;


  checkListCIGSelected: ChecklistProgetto | undefined | null = null;

  isError: boolean = false;
  isWarning: boolean = false;
  cupError: boolean = false;
  cigError: boolean = false;

  cigErroriProgressivo: number[] = [];

  cupErrorList: string[] = [];
  cigErrorList: string[] = [];
  errorText: string = '';

  isSuccess: boolean = false;
  isCompletata: boolean = false;

  stati: Stato[] = [];
  azioni: any;
  choiseEnum: any;
  lastProgressivo: number = 0;
  lastSaveEvent: string = '';
  choise: string = '';

  constructor(private cdr: ChangeDetectorRef, public router: Router, public client: Client, private toastr: ToastrService,
    public dialog: MatDialog, private route: ActivatedRoute, private tableComponent: ChecklistCupTableComponent) {
    this.cupId = this.route.snapshot.params['cup'];
    this.choise = this.route.snapshot.params['choise'];
    this.client.modality = this.route.snapshot.params['mode'];
    this.readOnly = this.client.modality == 'readOnly';
    this.readOnlyButton = this.client.modality == 'readOnly';
    this.azioni = Azioni;
    this.choiseEnum = CHOISE_TABLE;
  }

  async ngOnInit() {
    this.isSpinEmitter = true;
    await this.getListaCambiStati();
    await this.getDettaglioProgetto();
    await this.getCheckListProgetto();
    await this.getRisposte();
    this.azioni = Azioni;
    this.isSpinEmitter = false;
  }

  async getCheckListProgetto() {
    await lastValueFrom(this.client.getCheckListProgetto(this.cupId))
      .then(data => {
        if (data) {
          this.checklist = data;
          this.checkListCIG = []; //quando ricarica deve cancellare
          this.lastProgressivo = data.progressivoCig;
          for (let elem of this.checklist.macrosezioni!) {
            if (elem.macrosezione === "CUP") {
              this.checkListCUP = elem;
            } else if (elem.macrosezione === "CIG") {
              if (!this.cigClone) {
                this.cigClone = elem;
              }
              if (elem.cig && elem.cig.progressivo) {
                this.checkListCIG.push(elem);
              } else {
                this.cigCheckEmpty = elem;
              }
            }
          }
          this.checkListCIGInitial = _.cloneDeep(this.checkListCIG);
          if (!this.cigCheckEmpty) {
            let fieldsToClean = ['esito', 'note_asr'];
            this.cigCheckEmpty = this.deepCloneAndCleanFields(this.cigClone, fieldsToClean);
          }
        }
      })
      .catch(
        error => {
          this.toastr.error('API error [' + 'getCheckListProgetto' + ']');
          this.isSpinEmitter = false;
        }
      );
  }

  async getDettaglioProgetto() {
    await lastValueFrom(this.client.getDettaglioProgetto(this.cupId))
      .then(data => {
        if (data) {
          this.dettaglio = data;
        }
      })
      .catch(
        error => {
          this.toastr.error('API error [' + 'getDettaglioProgetto' + ']');
          this.isSpinEmitter = false;
        }
      );
  }

  async getListaCambiStati() {
    await lastValueFrom(this.client.getListaCambiStati())
      .then(data => {
        if (data) {
          this.stati = data;
        }
      })
      .catch(
        error => {
          this.toastr.error('API error [' + 'getListaCambiStati' + ']');
          this.isSpinEmitter = false;
        }
      );
  }

  async getRisposte() {
    await lastValueFrom(this.client.getRisposte())
      .then(data => {
        if (data) {
          this.choises = data;
        }
      })
      .catch(
        error => {
          this.toastr.error('API error [' + 'getRisposte' + ']');
          this.isSpinEmitter = false;
        }
      );
  }

  /**
   * Ritorna alla pagina di ricerca
   */
  backToRicerca() {
    this.router.navigate(['../main-page/progetti'], { skipLocationChange: true });
  }

  /**
   * Ripristina i dati originali
   */
  async annulla() {
    this.isSpinEmitter = true;

    // CUP
    this.resetErrors();
    this.cupTable.refresh();
    this.lastSaveEvent = '';
    //CIG
    this.cigErroriProgressivo = [];
    this.checkListCIGSelected = null;
    this.checkListCIG = _.cloneDeep(this.checkListCIGInitial); // Restore initial list into swap list

    this.isSpinEmitter = false;
  }

  /**
   * Salva la checklist a seguito della verifica di eventuali errori
   * @param event stato di salvataggio della checklist
   */
  async salva(event: any) {
    this.scrollToTop();

    this.isSpinEmitter = true;
    this.cigErroriProgressivo = [];
    this.resetErrors();
    this.lastSaveEvent = event;
    //Verifica stato di salvataggio ammesso
    if (!this.checkStato(event)) {
      this.isError = true;
      this.errorText = `Non è possibile completare l'operazione di salvataggio:
        Lo stato attuale della checklist è <b>${this.checklist?.stato}</b>. </br> È consentito il salvataggio in: <ul>`;
      let st = this.stati.find(e => e.statoCod == this.checklist?.stato);
      for (let als of st?.altriStati?.sort((a, b) => a.statoOrdinamento - b.statoOrdinamento)!) {
        this.errorText = this.errorText.concat(`<li><b> ${als.statoDesc} </b></li>`);
      }
      this.errorText = this.errorText.concat(`</ul>`);
      this.isSpinEmitter = false;
      return;
    }

    //CUP
    let errorCup = this.cupTable.checkError(event, this.checklist?.stato!);
    // CIG
    let errorCig: string[] = [];
    if (this.cigTable) {
      errorCig = this.cigTable.checkError(event, this.checklist?.stato!);
      if (this.cigTable.checklist?.cig?.progressivo && errorCig.length > 0)
        this.cigErroriProgressivo.push(this.cigTable.checklist?.cig?.progressivo);
    }
    for (let c of this.checkListCIG) {
      if (!this.cigTable || this.cigTable.checklist?.cig?.progressivo != c.cig?.progressivo) {
        const error: string[] = this.checkError(c, event, this.checklist?.stato!);
        if (c.cig?.progressivo && error.length > 0) {
          this.cigErroriProgressivo.push(c.cig?.progressivo);
        }
        errorCig = errorCig.concat(error);
      }
    }
    const uniqueArray: string[] = errorCig.filter((item, index) => {
      return errorCig.indexOf(item) === index;
    });
    // CONTA ERRORI
    if (errorCup.length > 0) {
      this.isError = true;
      this.cupError = true;
      this.cupErrorList = errorCup;
    }
    if (errorCig?.length > 0) {
      this.isError = true;
      this.cigError = true;
      this.cigErrorList = uniqueArray;
    }

    // Verifico che al salvataggio in COMPLETATA ci sia almeno una coppia di CUP-CIG
    if (['COMPILATA', 'COMPLETATA'].includes(event as string)) {
      if (this.checkListCIG.length === 0) {
        this.isError = true;
        this.cigError = true;
        this.client.mancanzaCigError = 'Occorre inserire almeno un <strong>codice CIG</strong> con relativa <strong>procedura</strong>, e valorizzare gli esiti delle relative domande.';
        this.isSpinEmitter = false;
        return;
      }
    }

    // Check se sono state eliminate CIG dalla tabella delle CIG
    if (this.checkListCIG.length === this.checkListCIGInitial.length) {
      //Verifica controllo variazioni checklist (solo per salvataggio nello stesso stato o da COMPILATA torna a BOZZA)
      if (event == this.checklist?.stato || (event == 'BOZZA' && this.checklist?.stato == 'COMPILATA')) {
        if (this.cupTable.findChanges().length === 0 && this.findChangesChecklist() === 0) {
          this.isError = true;
          this.isWarning = true;
          this.errorText = 'Non è possibile completare l\'operazione di salvataggio: </br> nessuna variazione apportata alla Checklist.';
          this.isSpinEmitter = false;
          return;
        }
      }
    }

    // Se non errori, salvataggio
    if (!this.isError) {
      let request: ChecklistRispostaList = {
        stato: event,
        cup: this.buildCupRequest(),
        cig: this.buildCigRequest()
      };
      await lastValueFrom(this.client.putCheckList(this.cupId, request))
        .catch(
          error => {
            this.toastr.error('API error [' + 'putCheckList' + ']');
            this.isSpinEmitter = false;
            return;
          }
        );
      this.isSuccess = true;
      this.checkListCIGSelected = null;
      await this.ngOnInit();
      if (event == 'COMPLETATA') {
        this.client.modality = 'readOnly';
        this.isCompletata = true;
        this.readOnly = true;
        this.showCigMode = true;
        this.editCigMode = false;
        this.readOnlyButton = true;
        this.router.navigate([`../main-page/checklist/`, this.cupId, 'readOnly'], { skipLocationChange: true });
      }
      this.isSpinEmitter = false;
    }
    this.isSpinEmitter = false;
  }


  /**
   * Verifica se lo stato di salvataggio è ammesso per lo stato attuale della checklist
   * @param stato stato di salvataggio della checklistannu
   * @returns
  */
  checkStato(stato: string) {
    return (this.stati.find(e => e.statoCod == this.checklist?.stato)?.altriStati?.some(e => e.statoCod == stato));
  }

  findChangesChecklist(): number {
    if (this.cigTable) {
      if (this.cigTable.findChanges().length > 0)
        return this.cigTable.findChanges().length;
    }
    const s = this.findDifferences(this.checkListCIGInitial, this.checkListCIG, 'note_asr');
    //const same: boolean = this.checkListCIGInitial.length === this.checkListCIG.length;
    return s.differences.length + s.inSecondOnly.length + s.inFirstOnly.length;
  }


  /**
   * Costruisce la request di salvataggio checklist CUP
   * @returns request salvataggio checklist CUP
   */
  buildCupRequest(): Array<ChecklistRisposta> {
    let cupReq: Array<ChecklistRisposta> = [];
    for (let elem of this.cupTable.values) {
      let cup: ChecklistRisposta = {
        rilevazione_domanda_id: elem.id,
        esito: elem.esito.val,
        note_asr: elem.notAsr.val ? elem.notAsr.val.trim() : elem.notAsr.val
      }
      cupReq.push(cup);
    }

    return cupReq;
  }

  /**
  * Costruisce la request di salvataggio checklist CIG
  * @returns request salvataggio checklist CIG
  */
  buildCigRequest(): Array<ProceduraRisposteCig> {

    let risposta: Array<ProceduraRisposteCig> = [];
    let cigReq1: Array<ChecklistRisposta> = [];

    for (let cigdb of this.checkListCIG) {
      if (cigdb.cig === this.cigTable?.checklist?.cig) {
        for (let elem of this.cigTable.values) {
          let cup: ChecklistRisposta = {
            rilevazione_domanda_id: elem.id,
            esito: elem.esito.val,
            note_asr: elem.notAsr.val ? elem.notAsr.val.trim() : elem.notAsr.val
          }
          cigReq1.push(cup);
        }
        const prc: ProceduraRisposteCig = {
          descrizione_procedura_cig: this.cigTable.checklist?.cig?.descrizione_procedura_cig,
          progressivo: this.cigTable.checklist?.cig?.progressivo,
          numero_cig: this.cigTable.checklist?.cig?.numero_cig,
          risposte: cigReq1
        };
        risposta.push(prc);
      } else {
        let cigReq2: Array<ChecklistRisposta> = [];
        for (let sez of cigdb.sezioni!) {
          for (let sottosez of sez.sottosezioni!) {
            for (let ril of sottosez.rilevazioni!) {
              let cup: ChecklistRisposta = {
                rilevazione_domanda_id: ril.rilevazione_domanda_id,
                esito: ril.esito,
                note_asr: ril.note_asr
              }
              cigReq2.push(cup);
            }
          }
        }
        const proceduraRisposteCig: ProceduraRisposteCig = {
          descrizione_procedura_cig: cigdb.cig?.descrizione_procedura_cig,
          progressivo: cigdb.cig?.progressivo,
          numero_cig: cigdb.cig?.numero_cig,
          risposte: cigReq2
        };
        cigReq2 = [];
        risposta.push(proceduraRisposteCig);
      }
    }
    return risposta;
  }

  async annullaAutocontrollo(event: any) {
    /**
     * mode può essere: VERIFICA_3 o VERIFICA_2
     */
    this.isSpinEmitter = true;

    // CUP
    this.cupverTable.refresh();
    this.lastSaveEvent = '';
    //CIG
    this.checkListCIGSelected = null;
    this.checkListCIG = _.cloneDeep(this.checkListCIGInitial); // Restore initial list into swap list
    this.cupverTable.refresh();

    this.isSpinEmitter = false;
  }

  async salvaAutocontrollo(event: any) {
    /**
     * mode può essere: VERIFICA_3 o VERIFICA_2
    */
    this.isSpinEmitter = true;
    let payload = {
      cup: this.buildCupRequestAutocontrollo(event),
      cig: this.buildCigRequestAutocontrollo(event)
    }
    await lastValueFrom(this.client.putCheckListAutocontrollo(this.cupId, payload))
      .catch(
        error => {
          this.toastr.error('API error [' + 'putCheckListAutocontrollo' + ']');
          this.isSpinEmitter = false;
          return;
        }
      );
    this.isSuccess = true;
    this.checkListCIGSelected = null;
    await this.ngOnInit();
    this.isSpinEmitter = false;
  }

  /**
   * Costruisce la request di salvataggio checklist CUP Autocontrollo
   * @returns request salvataggio checklist CUP
  */
  buildCupRequestAutocontrollo(mode: string): Array<ChecklistRispostaAutocontrollo> {
    let cupReqAutocontrollo: Array<ChecklistRispostaAutocontrollo> = [];
    for (let elem of this.cupverTable.values) {
      if (mode === 'VERIFICA_2') {
        let cup: ChecklistRispostaAutocontrollo = {
          rilevazione_domanda_id: elem.id,
          esito: elem.esito_verifica_liv2,
          note_rp: elem.nota_verifica_liv2 ? elem.nota_verifica_liv2.trim() : null
        }
        cupReqAutocontrollo.push(cup);
      }
      if (mode === 'VERIFICA_3') {
        let cup: ChecklistRispostaAutocontrollo = {
          rilevazione_domanda_id: elem.id,
          esito: elem.esito_verifica_liv3,
          note_rp: elem.nota_verifica_liv3 ? elem.nota_verifica_liv3.trim() : null
        }
        cupReqAutocontrollo.push(cup);
      }
    }
    return cupReqAutocontrollo;
  }

  /**
  * Costruisce la request di salvataggio checklist CIG Autocontrollo
  * @returns request salvataggio checklist CIG
  */
  buildCigRequestAutocontrollo(mode: string): Array<ProceduraRisposteCigAutocontrollo> {

    let risposta: Array<ProceduraRisposteCigAutocontrollo> = [];
    let cigReq1: Array<ChecklistRispostaAutocontrollo> = [];

    for (let cigdb of this.checkListCIG) {
      if (cigdb.cig === this.cigverTable?.checklist?.cig) {
        for (let e of this.cigverTable.values) {
          if (mode === 'VERIFICA_2') {
            let cig: ChecklistRispostaAutocontrollo = {
              rilevazione_domanda_id: e.id,
              esito: e.esito_verifica_liv2,
              note_rp: e.nota_verifica_liv2 ? e.nota_verifica_liv2.trim() : null
            }
            cigReq1.push(cig);
          }
          if (mode === 'VERIFICA_3') {
            let cig: ChecklistRispostaAutocontrollo = {
              rilevazione_domanda_id: e.id,
              esito: e.esito_verifica_liv3,
              note_rp: e.nota_verifica_liv3 ? e.nota_verifica_liv3.trim() : null
            }
            cigReq1.push(cig);
          }
        }
        const prc: ProceduraRisposteCigAutocontrollo = {
          descrizione_procedura_cig: this.cigverTable.checklist?.cig?.descrizione_procedura_cig,
          progressivo: this.cigverTable.checklist?.cig?.progressivo,
          numero_cig: this.cigverTable.checklist?.cig?.numero_cig,
          risposte: cigReq1
        };
        risposta.push(prc);
        cigReq1 = [];
      } else {
        let cigReq2: Array<ChecklistRispostaAutocontrollo> = [];
        for (let sez of cigdb.sezioni!) {
          for (let sottosez of sez.sottosezioni!) {
            for (let ril of sottosez.rilevazioni!) {
              if (mode === 'VERIFICA_2') {
                let cig: ChecklistRispostaAutocontrollo = {
                  rilevazione_domanda_id: ril.rilevazione_domanda_id,
                  esito: ril.esito_verifica_liv2 ? ril.esito_verifica_liv2 : false,
                  note_rp: ril.nota_verifica_liv2 ? ril.nota_verifica_liv2.trim() : null
                }
                cigReq2.push(cig);
              }
              if (mode === 'VERIFICA_3') {
                let cig: ChecklistRispostaAutocontrollo = {
                  rilevazione_domanda_id: ril.rilevazione_domanda_id,
                  esito: ril.esito_verifica_liv3 ? ril.esito_verifica_liv3 : false,
                  note_rp: ril.nota_verifica_liv3 ? ril.nota_verifica_liv3.trim() : null
                }
                cigReq2.push(cig);
              }
            }
          }
        }
        const proceduraRisposteCig: ProceduraRisposteCigAutocontrollo = {
          descrizione_procedura_cig: cigdb.cig?.descrizione_procedura_cig,
          progressivo: cigdb.cig?.progressivo,
          numero_cig: cigdb.cig?.numero_cig,
          risposte: cigReq2
        };
        cigReq2 = [];
        risposta.push(proceduraRisposteCig);
      }
    }
    return risposta;
  }

  /**
   * Ripristina le variabili interne di controllo
   */
  resetErrors() {
    this.isCompletata = false;
    this.isError = false;
    this.cupError = false;
    this.cigError = false;
    this.cupErrorList = [];
    this.cigErrorList = [];
    this.errorText = '';
    this.client.mancanzaCigError = '';
    this.isWarning = false;
  }

  newCig(data: ProceduraCig) {
    if (this.cigCheckEmpty) {
      this.lastProgressivo++;
      data.progressivo = this.lastProgressivo;
      const shallowCopy = this.deepCloneAndCleanFields(this.cigCheckEmpty, []);
      shallowCopy.cig = data;
      this.checkListCIG.push(shallowCopy);
    }
  }

  deleteCig(data: ProceduraCig) {
    this.checkListCIGSelected = null;
    const index = this.checkListCIG.findIndex(item => item.cig === data);
    if (index !== -1) {
      this.checkListCIG.splice(index, 1);
    } else {
    }
  }

  showCig(data: ProceduraCig) {
    this.showCigMode = true;
    this.editCigMode = false;
    this.readOnly = true; // Modalità readonly sul documento da compilare
    this.checkListCIGSelected = null; // Azzero il documento selezionato
    setTimeout(() => {
      // Cerco il documento che voglio mettere in selezione
      this.checkListCIGSelected = this.checkListCIG.find(t => t.cig === data);
    }, 50);
  }

  editCig(data: ProceduraCig) {
    this.showCigMode = false;
    this.editCigMode = true;
    this.salvaCigCorrente();
    this.readOnly = false; // Modalità readonly sul documento da compilare
    this.checkListCIGSelected = null; // Azzero il documento selezionato
    setTimeout(() => {
      // Cerco il documento che voglio mettere in selezione
      this.checkListCIGSelected = this.checkListCIG.find(t => t.cig === data);
      setTimeout(() => {
        if (this.lastSaveEvent)
          this.cigTable.checkError(this.lastSaveEvent, this.checklist?.stato!);
      }, 500);
    }, 50);
  }

  editCigAutocontrollo(data: ProceduraCig) {
    this.showCigMode = false;
    this.editCigMode = true;
    this.salvaCigCorrenteAutocontrollo();
    this.readOnly = false; // Modalità readonly sul documento da compilare
    this.checkListCIGSelected = null; // Azzero il documento selezionato
    setTimeout(() => {
      // Cerco il documento che voglio mettere in selezione
      this.checkListCIGSelected = this.checkListCIG.find(t => t.cig === data);
      setTimeout(() => {
        if (this.lastSaveEvent)
          this.cigTable.checkError(this.lastSaveEvent, this.checklist?.stato!);
      }, 500);
    }, 50);
  }

  deepCloneAndCleanFields<T>(obj: T, fieldsToClean: string[]): T {
    if (obj === null || typeof obj !== 'object') {
      return obj;
    }

    let clone: any = Array.isArray(obj) ? [] : {};

    for (let key in obj) {
      // @ts-ignore
      if (obj.hasOwnProperty(key)) {
        clone[key] = this.deepCloneAndCleanFields(obj[key], fieldsToClean);
      }
    }

    fieldsToClean.forEach(field => {
      let keys = field.split('.');
      if (keys.length === 1 && clone.hasOwnProperty(field)) {
        clone[field] = null;
      } else {
        let subObj = clone;
        for (let i = 0; i < keys.length - 1; i++) {
          if (subObj.hasOwnProperty(keys[i])) {
            subObj = subObj[keys[i]];
          } else {
            subObj = null;
            break;
          }
        }
        if (subObj && subObj.hasOwnProperty(keys[keys.length - 1])) {
          subObj[keys[keys.length - 1]] = null;
        }
      }
    });

    return clone;
  }

  private salvaCigCorrente() {
    for (let cigdb of this.checkListCIG) {
      if (cigdb.cig === this.cigTable?.checklist?.cig) {
        for (let elem of this.cigTable.values) {
          for (let sez of cigdb.sezioni!) {
            for (let sotto of sez.sottosezioni!) {
              for (let s of sotto.rilevazioni!) {
                if (s.rilevazione_domanda_id === elem.id) {
                  s.note_asr = elem.notAsr.val;
                  s.esito = elem.esito.val;
                }
              }
            }
          }
        }
      }
    }
  }

  private salvaCigCorrenteAutocontrollo() {
    for (let cigdb of this.checkListCIG) {
      if (cigdb.cig === this.cigverTable?.checklist?.cig) {
        for (let elem of this.cigverTable.values) {
          for (let sez of cigdb.sezioni!) {
            for (let sotto of sez.sottosezioni!) {
              for (let s of sotto.rilevazioni!) {
                if (s.rilevazione_domanda_id === elem.id) {
                  if (this.choise === this.choiseEnum.VERIFICA_2) {
                    s.esito_verifica_liv2 = elem.esito_verifica_liv2;
                    s.nota_verifica_liv2 = elem.nota_verifica_liv2;
                  }
                  if (this.choise === this.choiseEnum.VERIFICA_3) {
                    s.esito_verifica_liv3 = elem.esito_verifica_liv3;
                    s.nota_verifica_liv3 = elem.nota_verifica_liv3;
                  }
                }
              }
            }
          }
        }
      }
    }
  }


  async tabChanged($event: MatTabChangeEvent) {
    this.client.activeTab = $event.index;
    if ($event.index === CUP_TAB) {
      // Check della modalità di apertura della pagina di checklist
      this.client.modality === 'edit' ? this.readOnly = false : this.readOnly = true;
    } else if ($event.index === CIG_TAB) {
      if (this.client.modality === 'edit') { // Check della modalità di apertura della pagina di checklist
        if (this.showCigMode && !this.editCigMode) {
          this.readOnly = true;
        } else if (!this.showCigMode && this.editCigMode) {
          this.readOnly = false;
        } else {
          this.readOnly = false;
        }
      } else if (this.client.modality === 'readOnly') { // Check della modalità di apertura della pagina di checklist
        this.readOnly = true;
      }
    } else {
      this.readOnly = true;
    }

  }


  checkError(c: ChecklistProgetto, stato: string, statoOld: string) {
    let errorList: string[] = [];
    let found = false;
    switch (stato) {
      case 'BOZZA': {
        if (c.sezioni) {
          for (let s of c.sezioni) {
            if (s.sottosezioni) {
              for (let sot of s.sottosezioni) {
                if (sot.rilevazioni) {
                  for (let ril of sot.rilevazioni) {
                    if (ril.esito !== null && ril.esito !== undefined) {
                      found = true;
                    }
                  }
                }
              }
            }
          }
          if (!found && errorList.length === 0) {
            errorList.push('Per poter salvare la Checklist occorre valorizzare almeno una risposta.');
          }
        }
      }
        break;
      case 'COMPILATA': {
        let error1: boolean = false;
        let error2: boolean = false;
        let error3: boolean = false;
        let regEx = /[a-zA-Z]/;

        if (c.sezioni) {
          for (let s of c.sezioni) {
            if (s.sottosezioni) {
              for (let sot of s.sottosezioni) {
                if (sot.rilevazioni) {
                  for (let ril of sot.rilevazioni) {
                    if (ril.esito == null && ril.esito == undefined)
                      error1 = true;
                    if (ril.esito != null && ril.esito != undefined && (ril.esito == this.choises[1].codice || ril.esito == this.choises[2].codice)) {
                      if (ril.note_asr == null || ril.note_asr == undefined || ril.note_asr.trim().length == 0) {
                        error2 = true;
                      } else if (!regEx.test(ril.note_asr ?? '')) {
                        error3 = true;
                      }
                    }
                  }
                }
              }
            }
          }
        }
        if (error1) {
          errorList.push('Per poter salvare la Checklist come <b>Compilata</b>, è necessario selezionare un esito per tutte le domande che la compongono');
        }

        if (error2) {
          errorList.push('Il campo nota è <b>obbligatorio</b> in caso di esito <b>no</b> o <b>non applicabile</b>.');
        }

        if (error3) {
          errorList.push('Il campo <b>nota</b> deve contenere un testo valido.');
        }
        break;
      }
      case 'COMPLETATA': {
        let error1: boolean = false;
        let error2: boolean = false;
        let error3: boolean = false;
        let regEx = /[a-zA-Z]/;

        if (c.sezioni) {
          for (let s of c.sezioni) {
            if (s.sottosezioni) {
              for (let sot of s.sottosezioni) {
                if (sot.rilevazioni) {
                  for (let ril of sot.rilevazioni) {
                    if (ril.esito == null && ril.esito == undefined || ril.esito == this.choises[3].codice)
                      error1 = true;
                    if (ril.esito != null && ril.esito != undefined && (ril.esito == this.choises[1].codice || ril.esito == this.choises[2].codice)) {
                      if (ril.note_asr == null || ril.note_asr.trim().length == 0) {
                        error2 = true;
                      } else if (!regEx.test(ril.note_asr ?? '')) {
                        error3 = true;
                      }
                    }
                  }
                }
              }
            }
          }
        }


        if (error1) {
          errorList.push('Per completare la Checklist occorre aver compilato tutti le risposte con ESITO <b>“si”</b>, <b>“no”</b> oppure <b>“non applicabile”</b>');
        }

        if (error2) {
          errorList.push('Il campo nota è <b>obbligatorio</b> in caso di esito <b>no</b> o <b>non applicabile</b>.');
        }

        if (error3) {
          errorList.push('Il campo <b>nota</b> deve contenere un testo valido.');
        }
        break;
      }
    }

    return errorList;
  }

  //find diff

  findDifferences(arr1: any[], arr2: any[], key: string) {
    const map1 = arr1.reduce((acc, item) => {
      acc[item[key]] = item;
      return acc;
    }, {});

    const map2 = arr2.reduce((acc, item) => {
      acc[item[key]] = item;
      return acc;
    }, {});

    const inFirstOnly = [];
    const inSecondOnly = [];
    const differences = [];

    for (const id in map1) {
      if (!map2[id]) {
        inFirstOnly.push(map1[id]);
      } else if (JSON.stringify(map1[id]) !== JSON.stringify(map2[id])) {
        differences.push({ item1: map1[id], item2: map2[id] });
      }
    }

    for (const id in map2) {
      if (!map1[id]) {
        inSecondOnly.push(map2[id]);
      }
    }

    return { inFirstOnly, inSecondOnly, differences };
  }

  // Metodo per scorrere all'inizio della pagina
  scrollToTop() {
    this.topOfPage.nativeElement.scrollIntoView({ behavior: 'smooth', block: 'start' });
  }

}
