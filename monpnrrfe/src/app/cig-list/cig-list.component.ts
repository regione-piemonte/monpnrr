/*
* SPDX-FileCopyrightText: (C) Copyright 2025 Regione Piemonte
*
* SPDX-License-Identifier: EUPL-1.2 
*/

import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from "@angular/forms";
import { MatDialog } from "@angular/material/dialog";
import { ToastrService } from "ngx-toastr";
import { lastValueFrom } from "rxjs";
import { Azioni, Client } from "../Client";
import { Stato } from "../DTO/CambioStatoDTO";
import { Procedure } from "../DTO/Procedure";
import { ChecklistProgetto } from "../DTO/checklistProgetto";
import { ProceduraCig } from "../DTO/proceduraCig";
import { DialogConfermaComponent } from "../checklist-page/dialog-conferma/dialog-conferma.component";


@Component({
  selector: 'app-cig-list',
  templateUrl: './cig-list.component.html',
  styleUrls: ['./cig-list.component.css']
})
export class CigListComponent implements OnInit {

  @Input() cigErroriProgressivo: number[] | null = null;
  @Input() cigList: ChecklistProgetto[] | null = null;
  @Input() stato: string = '';
  @Input() readOnly: boolean = false;

  @Output() newCIG = new EventEmitter<ProceduraCig>();
  @Output() deleteCIG = new EventEmitter<ProceduraCig>();
  @Output() showCIG = new EventEmitter<ProceduraCig>();
  @Output() editCIG = new EventEmitter<ProceduraCig>();
  @Output() editCIGAutocontrollo = new EventEmitter<ProceduraCig>();

  listaCambiStato: Stato[] | null = null;
  completataObj: Stato[] | null = null;
  myForm: FormGroup;
  listaProcedure: Procedure[] | null = null;
  azioni: any;

  constructor(public dialog: MatDialog, private fb: FormBuilder, public client: Client, private toastr: ToastrService) {
    this.myForm = this.fb.group({
      numerocig: ['', [
        Validators.required
      ]],
      descrizione: ['', [
        Validators.required
      ]]
    });
    this.azioni = Azioni;
  }


  async ngOnInit() {
    if (this.client.listaCambiStato) {
      this.client.completataObj = this.client.listaCambiStato.filter((e) => e.statoCod === 'COMPLETATA');
    }
    await this.getListaProcedure();
    if (this.readOnly) {
      this.myForm.get('descrizione')?.disable();
      this.myForm.get('numerocig')?.disable();
    }
  }

  async getListaProcedure() {
    await lastValueFrom(this.client.getProcedure())
      .then(data => {
        if (data) {
          this.listaProcedure = data;
          this.listaProcedure.sort((a, b) => a.descrizione.localeCompare(b.descrizione));
        }
      })
      .catch(error => {
        this.toastr.error('API error [' + 'getListaProcedure' + ']');
      });
  }

  async getListaCambiStati() {
    await lastValueFrom(this.client.getListaCambiStati())
      .then(data => {
        if (data) {
          this.listaCambiStato = data;
        }
      })
      .catch(
        error => {
          this.toastr.error('API error [' + 'getListaCambiStati' + ']');
        }
      );
  }

  addCig() {
    if (this.myForm.valid) {

      const numerocig = this.myForm.get('numerocig')?.value;
      const descrizione = this.myForm.get('descrizione')?.value;

      let error = '';

      if (this.cigList?.find(c => c.cig?.numero_cig === numerocig)) {
        error = `Numero CIG ${numerocig} già presente`;
      }

      if (this.cigList?.find(c => c.cig?.descrizione_procedura_cig === descrizione)) {
        if (error)
          error = `Numero CIG ${numerocig} e Procedura ${descrizione} già presenti`;
        // else
        //   error = `Procedura  ${descrizione} già presente`;
      }

      if (error) {
        this.toastr.error(error);
        return;
      }

      this.newCIG.emit({
        descrizione_procedura_cig: descrizione,
        progressivo: 0,
        numero_cig: numerocig
      });

      this.myForm.get('numerocig')?.patchValue('');
      this.myForm.get('descrizione')?.patchValue('');
    }
  }

  deleteCig(cig: ProceduraCig) {

    const dialogRef = this.dialog.open(DialogConfermaComponent, {
      data: {
        title: 'Richiesta di conferma',
        text:
          `Hai selezionato la cancellazione del seguente codice CIG,
procedura: <b>${cig.numero_cig}</b>,<b>${cig.descrizione_procedura_cig}</b>
Tutti gli esiti e le note associate che hai indicato verranno eliminati.
Confermi di voler procedere all'eliminazione?`
      }
    });

    dialogRef.afterClosed().subscribe((result: any) => {
      if (result) {
        this.deleteCIG.emit(cig);
      }
    });

  }

  showCig(cig: ProceduraCig) {
    this.showCIG.emit(cig);
  }

  editCig(cig: ProceduraCig) {
    this.editCIG.emit(cig);
  }

  editCigAutocontrollo(cig: ProceduraCig) {
    this.editCIGAutocontrollo.emit(cig);
  }

  checkModificaStatoCompletata(stato: string): boolean {
    let listaStatiFiltered: Stato[] = [];
    if (this.client.listaCambiStato) {
      if (this.client.completataObj && this.client.completataObj.length === 1 && this.client.completataObj[0].statoOrdinamento) {
        const ordinamento = this.client.completataObj[0].statoOrdinamento;
        listaStatiFiltered = this.client.listaCambiStato.filter((e) => e.statoOrdinamento >= ordinamento);
      }
    }
    for (let s of listaStatiFiltered) {
      if (s.statoCod === stato) {
        return true;
      }
    }
    return false;
  }

  checkError(progressivo: number | undefined) {
    return this.cigErroriProgressivo?.some((e) => e === progressivo);
  }


  checkExistVerifica(cig: ChecklistProgetto){
    let existVerifica = false;
    cig.sezioni?.forEach(sezione => {
      sezione.sottosezioni?.forEach(sottosezione => {
        sottosezione.rilevazioni?.forEach(rilevazione => {
          if(rilevazione.esito_verifica_liv2 || rilevazione.esito_verifica_liv3){
            existVerifica = true;
          }
        })
      })
    })
    return existVerifica;
  }

}
