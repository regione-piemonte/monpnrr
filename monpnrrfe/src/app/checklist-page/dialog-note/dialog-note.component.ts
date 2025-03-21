/*
* SPDX-FileCopyrightText: (C) Copyright 2025 Regione Piemonte
*
* SPDX-License-Identifier: EUPL-1.2 
*/

import { Component, Inject, OnInit } from '@angular/core';
import { MAT_DIALOG_DATA } from '@angular/material/dialog';

@Component({
  selector: 'app-dialog-note',
  templateUrl: './dialog-note.component.html',
  styleUrls: ['./dialog-note.component.css']
})
export class DialogNoteComponent implements OnInit {

  note: string = '';

  constructor(@Inject(MAT_DIALOG_DATA) public data: any) {
    ({ note: this.note} = data);
  }

  ngOnInit(): void {
  }

}
