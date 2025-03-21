/*
* SPDX-FileCopyrightText: (C) Copyright 2025 Regione Piemonte
*
* SPDX-License-Identifier: EUPL-1.2 
*/

import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { Client } from './Client';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent implements OnInit {
  title = 'monpnrr';

  constructor(private client: Client, private route: ActivatedRoute, private router: Router) {

  }


  async ngOnInit() {
    /**
     * Geting query params from first call
     */
    this.route.queryParams.subscribe(param => {
      if (param['token'] !== undefined) {
        this.client.token = param['token'];
      }
    });

  }

}
