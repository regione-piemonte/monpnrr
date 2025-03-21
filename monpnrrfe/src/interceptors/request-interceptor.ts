/*
* SPDX-FileCopyrightText: (C) Copyright 2025 Regione Piemonte
*
* SPDX-License-Identifier: EUPL-1.2 
*/

import {
  HttpEvent,
  HttpHandler,
  HttpHeaders,
  HttpInterceptor,
  HttpRequest,
} from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { Client } from 'src/app/Client';
import * as uuid from 'uuid';

@Injectable({ providedIn: 'root' })
export class RequestInterceptor implements HttpInterceptor {

  constructor(private client: Client) { }

  intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    const authReq = req.clone(this.client.devMode ? {
      headers: new HttpHeaders({
        'X-Request-Id': uuid.v4(),
        'Shib-Identita-CodiceFiscale': 'ZZZZZZ00Z01L219A',
        'X-Codice-Servizio': 'MONPNRR',
        'X-Forwarded-For': '127.0.0.1',
      }),
    } : this.client.devModeLogin ? {
      headers: new HttpHeaders({
        'X-Request-Id': uuid.v4(),
        'Shib-Identita-CodiceFiscale': 'ZZZZZZ00Z01L219A',
        'X-Codice-Servizio': 'MONPNRR',
        'X-Forwarded-For': '10.0.0.0'
      }),
    } : {
      headers: new HttpHeaders({
        'X-Request-Id': uuid.v4(),
        'X-Codice-Servizio': 'MONPNRR',
      }),
    });

    return next.handle(authReq);
  }
}
