/*
* SPDX-FileCopyrightText: (C) Copyright 2025 Regione Piemonte
*
* SPDX-License-Identifier: EUPL-1.2 
*/

import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { Client } from 'src/app/Client';


@Component({
  selector: 'checklist-error',
  templateUrl: './checklist-error.component.html',
  styleUrls: ['./checklist-error.component.css']
})
export class ChecklistError implements OnInit {

  @Input() erroriCup: string[] = [];
  @Input() erroriCig: string[] = [];
  @Input() errorText: string = '';
  @Input() isWarning: boolean = false;


  constructor(public client: Client) { }



  async ngOnInit() {

  }



}
