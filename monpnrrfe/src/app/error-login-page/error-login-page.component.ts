/*
* SPDX-FileCopyrightText: (C) Copyright 2025 Regione Piemonte
*
* SPDX-License-Identifier: EUPL-1.2 
*/

import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { Client } from '../Client';

@Component({
  selector: 'app-error-login-page',
  templateUrl: './error-login-page.component.html',
  styleUrls: ['./error-login-page.component.css']
})
export class ErrorLoginPageComponent implements OnInit {

  error: any;

  constructor(private client: Client) { }

  ngOnInit(): void {
    this.client.isErrorLogin = true;
  }

}
