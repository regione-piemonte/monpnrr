/*
* SPDX-FileCopyrightText: (C) Copyright 2025 Regione Piemonte
*
* SPDX-License-Identifier: EUPL-1.2 
*/

import { Component, Injectable, Input, OnInit, ViewChild } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { MatTable } from '@angular/material/table';
import { Router } from '@angular/router';
import { ToastrService } from 'ngx-toastr';
import { ChecklistProgetto } from 'src/app/DTO/checklistProgetto';
import { Decodifica } from 'src/app/DTO/decodifica';
import { Azioni, Client } from '../../Client';
import { ESITO_TABLE } from 'src/app/constants';
import { DialogVerificaComponent } from '../dialog-verifica/dialog-verifica.component';
import { lastValueFrom } from 'rxjs';
import { DialogNoteComponent } from '../dialog-note/dialog-note.component';
import { UltimaModifica } from 'src/app/DTO/checklistProgettoRilevazione';


@Component({
  selector: 'verificata-cup-table',
  templateUrl: './verificata-cup-table.component.html',
  styleUrls: ['./verificata-cup-table.component.css']
})
@Injectable({
  providedIn: 'root',
})
export class VerificataCupTableComponent implements OnInit {

  @Input() readOnly: boolean = false;
  @Input() checklist: ChecklistProgetto | undefined | null = null;
  @ViewChild(MatTable) tablecup!: MatTable<any>;
  @Input() type: string = '';

  header1: string[] = ['puntiDiControllo', 'documentazione', 'note', 'esito', 'notaAsr', 'verifica'];
  header2: string[] = ['macroarea', 'sezione', 'sottosezione'];
  valueColumns: string[] = ['macroarea', 'sezione', 'sottosezione', 'documentazione', 'note', 'esito', 'notaAsr', 'verifica'];
  choises: Decodifica[] = [];
  dataSource: any;
  values: any[] = [];

  azioni: any;

  isSpinEmitter: boolean = false;
  esitoTable: any;
  constructor(public router: Router, public client: Client, private toastr: ToastrService, public dialog: MatDialog) {
    this.client.getRisposte().subscribe({
      next: (data) => {
        if (data) {
          this.choises = data;
        }
      },
      error: (err) => {
        this.toastr.error('API error [' + 'getRisposte' + ']');
      }
    });
    this.azioni = Azioni;
    this.esitoTable = ESITO_TABLE;
  }



  async ngOnInit() {
    this.isSpinEmitter = true;
    this.dataSource = this.checklist?.sezioni;
    this.values = this.costruisciValoriTabellaCUP();
    this.isSpinEmitter = false;
  }

  eliminaErroriContrassegnati() {
    this.isSpinEmitter = true;
    for (let element of this.values) {
      element.esito.error = false;
      element.notAsr.error = false;
    }
    this.isSpinEmitter = false;
  }

  /**
   * Costruisce le righe per la presentazione tabellare dei dati della checkList CUP
   */
  costruisciValoriTabellaCUP() {
    let n = 0;
    let values = []
    for (let i = 0; i < this.dataSource?.length; i++) {
      for (let j = 0; j < this.dataSource[i]?.sottosezioni?.length; j++) {
        for (let k = 0; k < this.dataSource[i]?.sottosezioni[j]?.rilevazioni?.length; k++) {
          //Primo elemento della sezione i
          if (j == 0 && k == 0) {
            values[n] = {
              num: { val: this.dataSource[i].numero, span: this.calcolaspan(i) },
              sez: { val: this.dataSource[i].nome, span: this.calcolaspan(i) },
              stsez: { val: this.dataSource[i].sottosezioni[j].nome, span: this.dataSource[i].sottosezioni[j].rilevazioni.length },
              doc: this.dataSource[i].sottosezioni[j].rilevazioni[k].nome,
              not: this.dataSource[i].sottosezioni[j].rilevazioni[k].note,
              id: this.dataSource[i].sottosezioni[j].rilevazioni[k].rilevazione_domanda_id,
              esito: { val: this.dataSource[i].sottosezioni[j].rilevazioni[k].esito, error: false },
              notAsr: { val: this.dataSource[i].sottosezioni[j].rilevazioni[k].note_asr, error: false },

              ultima_modifica: this.dataSource[i].sottosezioni[j].rilevazioni[k].ultima_modifica,

              esito_verifica_liv2: this.dataSource[i].sottosezioni[j].rilevazioni[k].esito_verifica_liv2 ? this.dataSource[i].sottosezioni[j].rilevazioni[k].esito_verifica_liv2 : false,
              nota_verifica_liv2: this.dataSource[i].sottosezioni[j].rilevazioni[k].nota_verifica_liv2,
              ultima_verifica_liv2: this.dataSource[i].sottosezioni[j].rilevazioni[k].ultima_verifica_liv2,
              verificato_liv2: this.dataSource[i].sottosezioni[j].rilevazioni[k].verificato_liv2,

              esito_verifica_liv3: this.dataSource[i].sottosezioni[j].rilevazioni[k].esito_verifica_liv3 ? this.dataSource[i].sottosezioni[j].rilevazioni[k].esito_verifica_liv3 : false,
              nota_verifica_liv3: this.dataSource[i].sottosezioni[j].rilevazioni[k].nota_verifica_liv3,
              ultima_verifica_liv3: this.dataSource[i].sottosezioni[j].rilevazioni[k].ultima_verifica_liv3,
              verificato_liv3: this.dataSource[i].sottosezioni[j].rilevazioni[k].verificato_liv3,
            }
            n++;
          } else {
            if (j > 0 && k == 0) {
              values[n] = {
                num: null,
                sez: null,
                stsez: { val: this.dataSource[i].sottosezioni[j].nome, span: this.dataSource[i].sottosezioni[j].rilevazioni.length },
                doc: this.dataSource[i].sottosezioni[j].rilevazioni[k].nome,
                not: this.dataSource[i].sottosezioni[j].rilevazioni[k].note,
                id: this.dataSource[i].sottosezioni[j].rilevazioni[k].rilevazione_domanda_id,
                esito: { val: this.dataSource[i].sottosezioni[j].rilevazioni[k].esito, error: false },
                notAsr: { val: this.dataSource[i].sottosezioni[j].rilevazioni[k].note_asr, error: false },

                ultima_modifica: this.dataSource[i].sottosezioni[j].rilevazioni[k].ultima_modifica,

                esito_verifica_liv2: this.dataSource[i].sottosezioni[j].rilevazioni[k].esito_verifica_liv2 ? this.dataSource[i].sottosezioni[j].rilevazioni[k].esito_verifica_liv2 : false,
                nota_verifica_liv2: this.dataSource[i].sottosezioni[j].rilevazioni[k].nota_verifica_liv2,
                ultima_verifica_liv2: this.dataSource[i].sottosezioni[j].rilevazioni[k].ultima_verifica_liv2,
                verificato_liv2: this.dataSource[i].sottosezioni[j].rilevazioni[k].verificato_liv2,

                esito_verifica_liv3: this.dataSource[i].sottosezioni[j].rilevazioni[k].esito_verifica_liv3 ? this.dataSource[i].sottosezioni[j].rilevazioni[k].esito_verifica_liv3 : false,
                nota_verifica_liv3: this.dataSource[i].sottosezioni[j].rilevazioni[k].nota_verifica_liv3,
                ultima_verifica_liv3: this.dataSource[i].sottosezioni[j].rilevazioni[k].ultima_verifica_liv3,
                verificato_liv3: this.dataSource[i].sottosezioni[j].rilevazioni[k].verificato_liv3,
              }
              n++;
            } else {
              values[n] = {
                num: null,
                sez: null,
                stsez: null,
                doc: this.dataSource[i].sottosezioni[j].rilevazioni[k].nome,
                not: this.dataSource[i].sottosezioni[j].rilevazioni[k].note,
                id: this.dataSource[i].sottosezioni[j].rilevazioni[k].rilevazione_domanda_id,
                esito: { val: this.dataSource[i].sottosezioni[j].rilevazioni[k].esito, error: false },
                notAsr: { val: this.dataSource[i].sottosezioni[j].rilevazioni[k].note_asr, error: false },

                ultima_modifica: this.dataSource[i].sottosezioni[j].rilevazioni[k].ultima_modifica,

                esito_verifica_liv2: this.dataSource[i].sottosezioni[j].rilevazioni[k].esito_verifica_liv2 ? this.dataSource[i].sottosezioni[j].rilevazioni[k].esito_verifica_liv2 : false,
                nota_verifica_liv2: this.dataSource[i].sottosezioni[j].rilevazioni[k].nota_verifica_liv2,
                ultima_verifica_liv2: this.dataSource[i].sottosezioni[j].rilevazioni[k].ultima_verifica_liv2,
                verificato_liv2: this.dataSource[i].sottosezioni[j].rilevazioni[k].verificato_liv2,

                esito_verifica_liv3: this.dataSource[i].sottosezioni[j].rilevazioni[k].esito_verifica_liv3 ? this.dataSource[i].sottosezioni[j].rilevazioni[k].esito_verifica_liv3 : false,
                nota_verifica_liv3: this.dataSource[i].sottosezioni[j].rilevazioni[k].nota_verifica_liv3,
                ultima_verifica_liv3: this.dataSource[i].sottosezioni[j].rilevazioni[k].ultima_verifica_liv3,
                verificato_liv3: this.dataSource[i].sottosezioni[j].rilevazioni[k].verificato_liv3,
              }
              n++;
            }
          }
        }

      }
    }
    return values;
  }

  /**
   * Calcola il rowspan per la presentazione tabellare delle rilevazioni presenti nelle sottosezioni
   * @param i indice della sezione
   * @returns rowspan calcolato in base alle rilevazioni presenti nelle sottosezioni
   */
  calcolaspan(i: number): number {
    let n = 0;
    for (let s of this.dataSource[i].sottosezioni) {
      n = n + s.rilevazioni.length;
    }
    return n;
  }

  /**
   * Azzera il campo noteAsr per valori SI e N.A.A.
   * @param selection esito selezionato
   * @param element elemento in cui valorizzare l'esito
   */
  onEsitoChange(element: any) {
    if (element.esito.val != this.choises[1].codice && element.esito.val != this.choises[2].codice) {
      element.notAsr.val = '';
    }
  }

  /**
   * Ripristina tutti i campi ai valori iniziali
   */
  refresh() {
    this.values = this.costruisciValoriTabellaCUP();
    this.tablecup.renderRows();
  }

  /**
   * Verifica la presenza di errori al salvataggio
   * @param stato stato della checklist al salvataggio
   * @param statoOld stato della checklist originale
   * @returns lista di eventuali errori
   */
  checkError(stato: string, statoOld: string) {
    let errorList: string[] = [];
    switch (stato) {
      case 'BOZZA': {
        let found = false;
        // Controllo che ci sia almeno un valore settato
        for (let element of this.values) {
          if (element.esito.val != null && element.esito.val != undefined) {
            found = true;
          }
        }
        if (!found) {
          errorList.push('Per poter salvare la Checklist occorre valorizzare almeno una risposta.');
        }
        this.eliminaErroriContrassegnati();
        // else if(statoOld != 'DA COMPILARE') {

        //     const changedObjects = this.findChanges();
        //     if(changedObjects.length == 0) {
        //         //Nessuna modifica rilevata rispetto alla situazione iniziale
        //         errorList.push('Nessuna variazione apportata alla Checklist.');
        //     }
        // }
        break;
      }
      case 'COMPILATA': {
        let error1: boolean = false;
        let error2: boolean = false;
        let error3: boolean = false;
        let regEx = /[a-zA-Z]/;
        for (let element of this.values) {
          if (element.esito.val == null || element.esito.val == undefined) {
            element.esito.error = true;
            error1 = true;
          } else {
            element.esito.error = false;
          }

          if (element.esito.val != null && element.esito.val != undefined && (element.esito.val == this.choises[1].codice || element.esito.val == this.choises[2].codice)) {
            if (element.notAsr.val == null || element.notAsr.val == undefined || element.notAsr.val.trim().length == 0) {
              element.notAsr.error = true;
              error2 = true;
            } else if (!regEx.test(element.notAsr.val ?? '')) {
              element.notAsr.error = true;
              error3 = true;
            } else {
              element.notAsr.error = false;
            }
          } else {
            element.notAsr.error = false;
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
        for (let element of this.values) {
          if (element.esito.val == null || element.esito.val == undefined || element.esito.val == this.choises[3].codice) {
            element.esito.error = true;
            error1 = true;
          } else {
            element.esito.error = false;
          }
          if (element.esito.val != null && element.esito.val != undefined && (element.esito.val == this.choises[1].codice || element.esito.val == this.choises[2].codice)) {
            if (element.notAsr.val == null || element.notAsr.val == undefined || element.notAsr.val.trim().length == 0) {
              element.notAsr.error = true;
              error2 = true;
            } else if (!regEx.test(element.notAsr.val ?? '')) {
              element.notAsr.error = true;
              error3 = true;
            } else {
              element.notAsr.error = false;
            }
          } else {
            element.notAsr.error = false;
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

    // Nel caso ci siano errori, render della tabella per far vedere le celle rosse
    if (errorList.length > 0) {
      this.tablecup.renderRows();
    }
    return errorList;
  }

  /**
   * Verifica se la checklist è stata modificata
   * @returns eventuali elementi modificati
   */
  findChanges() {
    let oldValues = this.costruisciValoriTabellaCUP();
    let newValues = this.values;

    const changedObjects = oldValues.filter(obj1 => {
      const obj2 = newValues.find(x => x.id === obj1.id);
      return (obj1.esito.val !== obj2.esito.val || obj1.notAsr.val !== obj2.notAsr.val);
    });

    return changedObjects;
  }

  async showDialogVerifica(element: any) {
    this.client.autocontrolloSwap = {
      checkboxSwap: element.esito_verifica_liv2,
      checkboxSwapCartaceo: element.esito_verifica_liv3,
      notaSwap: element.nota_verifica_liv2,
      notaSwapCartaceo: element.nota_verifica_liv3
    }
    let ultimaModificaToPass;
    if (this.client.selectProfile && ['RP-verifica-liv3'].includes(this.client.selectProfile.codice)) {
      ultimaModificaToPass = element.ultima_verifica_liv2;
    } else if (this.client.selectProfile && !['RP-verifica-liv3'].includes(this.client.selectProfile.codice)) {
      ultimaModificaToPass = element.ultima_modifica;
    } else {
      let utimaModificaElse: UltimaModifica = {
        data: null,
        utente: null
      }
      ultimaModificaToPass = utimaModificaElse;
    }
    let result: boolean = false;
    const dialogRef = this.dialog.open(DialogVerificaComponent, {
      minWidth: '100vh',
      minHeight: '20vh',
      data: { ultima_modifica: ultimaModificaToPass }
    });  // Apro la modal
    result = await lastValueFrom(dialogRef.afterClosed());
    if (result) {
      for (let i = 0; i < this.values.length; i++) {
        if (this.values[i].id === element.id) {
          // this.values[i].ultima_modifica = element.ultima_modifica;
          this.values[i].esito_verifica_liv2 = this.client.autocontrolloSwap.checkboxSwap;
          this.values[i].nota_verifica_liv2 = this.client.autocontrolloSwap.notaSwap;
          // this.values[i].ultima_verifica_liv2 = element.ultima_verifica_liv2;
          // this.values[i].verificato_liv2 = element.verificato_liv2;
          this.values[i].esito_verifica_liv3 = this.client.autocontrolloSwap.checkboxSwapCartaceo;
          this.values[i].nota_verifica_liv3 = this.client.autocontrolloSwap.notaSwapCartaceo;
          // this.values[i].ultima_verifica_liv3 = element.ultima_verifica_liv3;
          // this.values[i].verificato_liv3 = element.verificato_liv3;
        }
      }
    } else {
      this.client.autocontrolloSwap = {
        checkboxSwap: false,
        checkboxSwapCartaceo: false,
        notaSwap: null,
        notaSwapCartaceo: null
      }
    }
  }

  async showDialogNote(note: string) {
    let result: boolean = false;
    this.dialog.open(DialogNoteComponent, {
      data: { note: note }
    });  // Apro la modal
  }

  checkCircleRed(element: any): boolean {
    if (this.client.selectProfile && ['RP-verifica-liv2'].includes(this.client.selectProfile.codice)) {
      if (!element.esito_verifica_liv2) {
        return true;
      }
    } else if (this.client.selectProfile && ['RP-verifica-liv3'].includes(this.client.selectProfile.codice)) {
      if (!element.esito_verifica_liv3) {
        return true;
      }
    }
    return false;
  }
  checkCircleGreen(element: any): boolean {
    if (this.client.selectProfile && ['RP-verifica-liv2'].includes(this.client.selectProfile.codice)) {
      if (element.esito_verifica_liv2) {
        return true;
      }
    } else if (this.client.selectProfile && ['RP-verifica-liv3'].includes(this.client.selectProfile.codice)) {
      if (element.esito_verifica_liv3) {
        return true;
      }
    }
    return false;
  }

}
