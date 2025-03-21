/*
* SPDX-FileCopyrightText: (C) Copyright 2025 Regione Piemonte
*
* SPDX-License-Identifier: EUPL-1.2 
*/

import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { DialogConfermaComponent } from '../dialog-conferma/dialog-conferma.component';
import { Azioni, Client } from 'src/app/Client';
import { CHOISE_TABLE } from 'src/app/constants';
import { lastValueFrom } from 'rxjs';


@Component({
  selector: 'checklist-button-group',
  templateUrl: './checklist-button-group.component.html',
  styleUrls: ['./checklist-button-group.component.css']
})
export class ChecklistButtonGroupComponent implements OnInit {

  @Input() showTitle: boolean = false;
  @Input() readOnly: boolean = false;
  @Input() statoChecklist: string = '';
  @Input() choice: string = '';
  @Output() annullaEvent = new EventEmitter<any>();
  @Output() salvaEvent = new EventEmitter<any>();
  @Output() annullaEventAutocontrollo = new EventEmitter<any>();
  @Output() salvaEventAutocontrollo = new EventEmitter<any>();

  azioni: any;
  choiseEnum: any;

  constructor(public dialog: MatDialog, public client: Client) {
    this.azioni = Azioni;
    this.choiseEnum = CHOISE_TABLE;
  }

  async ngOnInit() {

  }

  annulla() {
    const dialogRef = this.dialog.open(DialogConfermaComponent, {data: {
      title: 'Richiesta di conferma',
      text: this.statoChecklist == 'DA COMPILARE' ?
      'Hai selezionato <b>"ANNULLA"</b>: tutti i dati inseriti verranno eliminati.\n\nConfermi di voler procedere all\'eliminazione?' :
      'Hai selezionato <b>"ANNULLA"</b>: tutti i dati modificati verranno eliminati.\n\nConfermi di voler procedere al ripristino dei dati originali?'
    }});

    dialogRef.afterClosed().subscribe(result => {
      if(result) {
        this.annullaEvent.emit(true);
      }
    });
  }

  salva(stato: string) {
    const dialogRef = this.dialog.open(DialogConfermaComponent, {data: {
      title: `Salvataggio in ${stato}`,
      text: stato == 'BOZZA' ? `Hai selezionato <b>"SALVA IN ${stato}"</b>. \n\nConfermi di voler procedere al salvataggio del lavoro?` :
            stato == 'COMPILATA' ? `Hai selezionato <b>"SALVA ${stato}"</b>. \n\nConfermi di voler procedere al salvataggio del lavoro?` :
            `Hai selezionato <b>"SALVA ${stato}"</b>: \nse confermi, la Checklist verrà consolidata e non potrai modificarla ulteriormente. \n\nConfermi di voler procedere al salvataggio del lavoro?`
    }});

    dialogRef.afterClosed().subscribe(result => {
      if(result) {
        this.salvaEvent.emit(stato);
      }
    });
  }

  async salvaAutocontrollo(mode: string) {
    const dialogRef = this.dialog.open(DialogConfermaComponent, {data: {
      title: `Salvataggio autocontrollo`,
      text: 'Confermi di voler procedere al salvataggio del lavoro?'
    }});
    const result = await lastValueFrom(dialogRef.afterClosed());
    if (result) {
      this.salvaEventAutocontrollo.emit(mode);
    }
  }

  async annullaAutocontrollo(mode: string) {
    const dialogRef = this.dialog.open(DialogConfermaComponent, {data: {
      title: 'Richiesta di conferma',
      text: 'Hai selezionato <b>"ANNULLA"</b>: tutti i dati modificati verranno eliminati.\n\nConfermi di voler procedere al ripristino dei dati originali?'
    }});
    const result = await lastValueFrom(dialogRef.afterClosed());
    if (result) {
      this.annullaEventAutocontrollo.emit(mode);
    }
  }

}
