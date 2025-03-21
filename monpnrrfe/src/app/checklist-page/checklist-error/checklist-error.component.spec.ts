/*
* SPDX-FileCopyrightText: (C) Copyright 2025 Regione Piemonte
*
* SPDX-License-Identifier: EUPL-1.2 
*/

import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ChecklistError } from './checklist-error.component';

describe('MainPageComponent', () => {
  let component: ChecklistError;
  let fixture: ComponentFixture<ChecklistError>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ChecklistError ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ChecklistError);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
