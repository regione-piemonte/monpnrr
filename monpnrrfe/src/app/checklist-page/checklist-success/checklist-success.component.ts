/*
* SPDX-FileCopyrightText: (C) Copyright 2025 Regione Piemonte
*
* SPDX-License-Identifier: EUPL-1.2 
*/

import { animate, style, transition, trigger } from '@angular/animations';
import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';


@Component({
  selector: 'checklist-success',
  templateUrl: './checklist-success.component.html',
  styleUrls: ['./checklist-success.component.css'],
  animations:[
    trigger('fade', [
      transition(":enter", [
        style({ opacity: 0 }),
        animate("1000ms 1000ms", style({ opacity: 1 }))
      ]),
      transition(":leave", [animate(1000, style({ opacity: 0 }))])
    ])
  ]
})
export class ChecklistSuccess implements OnInit {

  @Input() isCompletata: boolean = false;
  @Output() endEvent = new EventEmitter<any>();

  constructor() { }

  async ngOnInit() {
    setTimeout(() => {
      this.endEvent.emit(true);
    }, 5000);
  }

}
