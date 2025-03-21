/*
* SPDX-FileCopyrightText: (C) Copyright 2025 Regione Piemonte
*
* SPDX-License-Identifier: EUPL-1.2 
*/

import { ComponentFixture, TestBed } from '@angular/core/testing';

import { VerificataCupTableComponent } from './verificata-cup-table.component';

describe('MainPageComponent', () => {
  let component: VerificataCupTableComponent;
  let fixture: ComponentFixture<VerificataCupTableComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ VerificataCupTableComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(VerificataCupTableComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
