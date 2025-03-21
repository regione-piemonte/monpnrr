/*
* SPDX-FileCopyrightText: (C) Copyright 2025 Regione Piemonte
*
* SPDX-License-Identifier: EUPL-1.2 
*/

import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CigListComponent } from './cig-list.component';

describe('CigListComponent', () => {
  let component: CigListComponent;
  let fixture: ComponentFixture<CigListComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ CigListComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(CigListComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
