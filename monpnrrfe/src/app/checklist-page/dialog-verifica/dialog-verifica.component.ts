/*
* SPDX-FileCopyrightText: (C) Copyright 2025 Regione Piemonte
*
* SPDX-License-Identifier: EUPL-1.2 
*/

import { Component, Inject, OnInit } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { Client } from 'src/app/Client';
import { UltimaModifica } from 'src/app/DTO/checklistProgettoRilevazione';

@Component({
  selector: 'app-dialog-verifica',
  templateUrl: './dialog-verifica.component.html',
  styleUrls: ['./dialog-verifica.component.css']
})
export class DialogVerificaComponent implements OnInit {

  isError: boolean = false;
  isValidInput: boolean = false;
  ultima_modifica: UltimaModifica;
  errors: string[] = [];

  ERROR_TEXT_NOTE: string = 'ATTENZIONE: il testo della nota deve contenere solo caratteri alfanumerici.';

  constructor(public client: Client, public dialogRef: MatDialogRef<DialogVerificaComponent>, @Inject(MAT_DIALOG_DATA) public data: any) {
    ({ ultima_modifica: this.ultima_modifica } = data);
  }

  ngOnInit(): void {
    this.isError = false;
  }

  salvaVerifica() {
    if (this.client.autocontrolloSwap.checkboxSwap) {
      if (!this.client.autocontrolloSwap.notaSwap || (this.client.autocontrolloSwap.notaSwap.trim() === '')) {
        this.isError = true;
        if (!this.errors.includes('ATTENZIONE: Non è possibile completare l\'operazione di salvataggio, è obbligatorio inserire una nota.')) {
          this.errors.push('ATTENZIONE: Non è possibile completare l\'operazione di salvataggio, è obbligatorio inserire una nota.');
        }
        return;
      }
    }
    if (this.client.autocontrolloSwap.checkboxSwapCartaceo) {
      if (!this.client.autocontrolloSwap.notaSwapCartaceo || (this.client.autocontrolloSwap.notaSwapCartaceo.trim() === '')) {
        this.isError = true;
        if (!this.errors.includes('ATTENZIONE: Non è possibile completare l\'operazione di salvataggio, è obbligatorio inserire una nota.')) {
          this.errors.push('ATTENZIONE: Non è possibile completare l\'operazione di salvataggio, è obbligatorio inserire una nota.');
        }
        return;
      }
    }
    this.dialogRef.close(true);
  }

  resetNote(mode: string) {
    switch (mode) {
      case 'liv2':
        this.client.autocontrolloSwap.notaSwap = null;
        this.isError = false;
        break;
      case 'liv3':
        this.client.autocontrolloSwap.notaSwapCartaceo = null;
        this.isError = false;
        break;
      default:
        break;
    }
  }

  validateNoteText(mode: string) {
    switch (mode) {
      case 'liv2':
        if (this.client.autocontrolloSwap.notaSwap && this.client.autocontrolloSwap.notaSwap !== '') {
          const regex = /^[a-zA-Z0-9àèéìòùÀÈÉÌÒÙ'’ ]*$/;
          this.isValidInput = regex.test(this.client.autocontrolloSwap.notaSwap);
          if (!this.isValidInput) {
            this.isError = true;
            if (!this.errors.includes(this.ERROR_TEXT_NOTE)) {
              this.errors.push(this.ERROR_TEXT_NOTE);
            }
            return;
          }
        } else if (this.client.autocontrolloSwap.notaSwap === '') {
          this.isValidInput = true;
        }
        break;
      case 'liv3':
        if (this.client.autocontrolloSwap.notaSwapCartaceo && this.client.autocontrolloSwap.notaSwapCartaceo !== '') {
          const regex = /^[a-zA-Z0-9àèéìòùÀÈÉÌÒÙ'’ ]*$/;
          this.isValidInput = regex.test(this.client.autocontrolloSwap.notaSwapCartaceo);
          if (!this.isValidInput) {
            this.isError = true;
            if (!this.errors.includes(this.ERROR_TEXT_NOTE)) {
              this.errors.push(this.ERROR_TEXT_NOTE);
            }
            return;
          }
        } else if (this.client.autocontrolloSwap.notaSwapCartaceo === '') {
          this.isValidInput = true;
        }
        break;
      default:
        break;
    }
    this.errors = this.errors.filter((e) => e !== this.ERROR_TEXT_NOTE);
    this.isError = false;
  }

}
