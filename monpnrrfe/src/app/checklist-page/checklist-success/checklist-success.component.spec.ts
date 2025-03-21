/*
* SPDX-FileCopyrightText: (C) Copyright 2025 Regione Piemonte
*
* SPDX-License-Identifier: EUPL-1.2 
*/

import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ChecklistSuccess } from './checklist-success.component';

describe('MainPageComponent', () => {
  let component: ChecklistSuccess;
  let fixture: ComponentFixture<ChecklistSuccess>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ChecklistSuccess ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ChecklistSuccess);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
