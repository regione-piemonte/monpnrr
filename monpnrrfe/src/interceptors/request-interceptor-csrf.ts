/*
* SPDX-FileCopyrightText: (C) Copyright 2025 Regione Piemonte
*
* SPDX-License-Identifier: EUPL-1.2 
*/

import { HttpEvent, HttpHandler, HttpInterceptor, HttpRequest, HttpXsrfTokenExtractor } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

// @Injectable()
// export class CsrfInterceptor implements HttpInterceptor {

//   intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
//     // Funzione per leggere un cookie
//     function getCookie(name: string): string {
//       const matches = document.cookie.match(new RegExp(
//         '(?:^|; )' + name.replace(/([\.$?*|{}\(\)\[\]\\\/\+^])/g, '\\$1') + '=([^;]*)'
//       ));
//       return matches ? decodeURIComponent(matches[1]) : '';
//     }

//     // Leggi il token CSRF dal cookie
//     const csrfToken = getCookie('XSRF-TOKEN');

//     // Clona la richiesta e aggiungi l'intestazione CSRF se il token è presente
//     const clonedRequest = req.clone({
//       headers: req.headers.set('X-XSRF-TOKEN', csrfToken)
//     });

//     // Passa la richiesta clonata con il token al prossimo handler
//     return next.handle(clonedRequest);
//   }
// }

@Injectable()
export class CsrfInterceptor implements HttpInterceptor {
  constructor(private tokenExtractor: HttpXsrfTokenExtractor) {
  }

  intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    const cookieheaderName = 'X-XSRF-TOKEN';

    let csrfToken = this.tokenExtractor.getToken() as string;
    if (csrfToken !== null && !req.headers.has(cookieheaderName)) {
      req = req.clone({ headers: req.headers.set(cookieheaderName, csrfToken), withCredentials: true });
    }
    return next.handle(req);
  }
}


// @Injectable()
// export class CsrfInterceptor implements HttpInterceptor {
//   private csrfToken: string | null = null;

//   constructor(private http: HttpClient) {
//     this.loadCsrfToken();
//   }

//   intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
//     if (this.csrfToken) {
//       const cloned = req.clone({
//         headers: req.headers.set('X-XSRF-TOKEN', this.csrfToken)
//       });
//       return next.handle(cloned);
//     } else {
//       return next.handle(req);
//     }
//   }

//   private loadCsrfToken(): void {
//     this.http.get('/csrf-token', { observe: 'response' }).pipe(
//       tap(response => {
//         const csrfToken = response.headers.get('X-CSRF-TOKEN');
//         if (csrfToken) {
//           this.csrfToken = csrfToken;
//         }
//       })
//     ).subscribe();
//   }
// }
