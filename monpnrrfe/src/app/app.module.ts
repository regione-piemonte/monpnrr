/*
* SPDX-FileCopyrightText: (C) Copyright 2025 Regione Piemonte
*
* SPDX-License-Identifier: EUPL-1.2 
*/

import { LOCALE_ID, NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';

import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';

import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatListModule } from '@angular/material/list';
import { MatSidenavModule } from '@angular/material/sidenav';
import { MatToolbarModule } from '@angular/material/toolbar';

import { HTTP_INTERCEPTORS, HttpClientModule, HttpClientXsrfModule } from '@angular/common/http';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MomentDateAdapter } from '@angular/material-moment-adapter';
import { MatCardModule } from '@angular/material/card';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { DateAdapter, MAT_DATE_FORMATS, MAT_DATE_LOCALE, MatNativeDateModule } from '@angular/material/core';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatDialogModule } from '@angular/material/dialog';
import { MatExpansionModule } from '@angular/material/expansion';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatMenuModule } from '@angular/material/menu';
import { MatPaginatorModule } from '@angular/material/paginator';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatSelectModule } from '@angular/material/select';
import { MatTableModule } from '@angular/material/table';
import { MatTabsModule } from '@angular/material/tabs';
import { MatTooltipModule } from '@angular/material/tooltip';
import { NgbModule } from '@ng-bootstrap/ng-bootstrap';
import { NgxMatTimepickerModule } from 'ngx-mat-timepicker';
import { ToastrModule } from 'ngx-toastr';
import { RequestInterceptor } from 'src/interceptors/request-interceptor';
import { Client } from './Client';
import { ErrorLoginPageComponent } from './error-login-page/error-login-page.component';
import { FooterComponent } from './footer/footer.component';
import { HeaderComponent } from './header/header.component';
import { LoginPageComponent } from './login-page/login-page.component';
import { MainPageComponent } from './main-page/main-page.component';
import { registerLocaleData } from '@angular/common';
import localeIt from '@angular/common/locales/it';
import { MatAutocompleteModule } from '@angular/material/autocomplete';
import { ProjectListComponent } from './project-list/project-list.component';
import { ChecklistPageComponent } from './checklist-page/checklist-page.component';
import { ChecklistButtonGroupComponent } from './checklist-page/checklist-button-group/checklist-button-group.component';
import { ChecklistCupTableComponent } from './checklist-page/checklist-cup-table/checklist-cup-table.component';
import { DialogConfermaComponent } from './checklist-page/dialog-conferma/dialog-conferma.component';
import { ChecklistError } from './checklist-page/checklist-error/checklist-error.component';
import { ChecklistSuccess } from './checklist-page/checklist-success/checklist-success.component';
import { CigListComponent } from './cig-list/cig-list.component';
import { MatRadioModule } from '@angular/material/radio';
import { DialogVerificaComponent } from './checklist-page/dialog-verifica/dialog-verifica.component';
import { VerificataCupTableComponent } from './checklist-page/verificata-cup-table/verificata-cup-table.component';
import { DialogNoteComponent } from './checklist-page/dialog-note/dialog-note.component';
import { CsrfInterceptor } from 'src/interceptors/request-interceptor-csrf';

const MY_DATE_FORMATS = {
  parse: {
    dateInput: 'DD/MM/YYYY', // Formato di input
  },
  display: {
    dateInput: 'DD/MM/YYYY', // Formato di visualizzazione
    monthYearLabel: 'MMMM YYYY',
    dateA11yLabel: 'LL',
    monthYearA11yLabel: 'MMMM YYYY'
  }
};

// Registra il locale italiano
registerLocaleData(localeIt);

@NgModule({
  declarations: [
    AppComponent,
    HeaderComponent,
    FooterComponent,
    MainPageComponent,
    LoginPageComponent,
    ErrorLoginPageComponent,
    ProjectListComponent,
    CigListComponent,
    ChecklistPageComponent,
    ChecklistButtonGroupComponent,
    ChecklistCupTableComponent,
    VerificataCupTableComponent,
    DialogConfermaComponent,
    ChecklistError,
    ChecklistSuccess,
    DialogVerificaComponent,
    DialogNoteComponent,
  ],
  imports: [
    HttpClientModule,
    HttpClientXsrfModule.withOptions({
      cookieName: 'XSRF-TOKEN',
      headerName: 'X-XSRF-TOKEN',
    }),
    BrowserModule,
    AppRoutingModule,
    BrowserAnimationsModule,
    MatButtonModule,
    MatSidenavModule,
    MatToolbarModule,
    MatIconModule,
    MatListModule,
    MatCheckboxModule,
    MatMenuModule,
    FormsModule,
    ReactiveFormsModule,
    NgbModule,
    MatFormFieldModule,
    MatInputModule,
    MatSelectModule,
    MatCardModule,
    MatDatepickerModule,
    MatTabsModule,
    MatNativeDateModule,
    MatExpansionModule,
    MatDialogModule,
    MatTooltipModule,
    MatProgressSpinnerModule,
    MatTableModule,
    MatPaginatorModule,
    MatAutocompleteModule,
    MatRadioModule,
    BrowserAnimationsModule, // required animations module
    ToastrModule.forRoot({
      timeOut: 4000,
      positionClass: 'toast-bottom-center',
      preventDuplicates: true,
      // closeButton: true,
      progressBar: true,
      countDuplicates: true,
    }), // ToastrModule added
    NgxMatTimepickerModule
  ],
  providers: [
    Client,
    { provide: HTTP_INTERCEPTORS, useClass: RequestInterceptor, multi: true },
    { provide: HTTP_INTERCEPTORS, useClass: CsrfInterceptor, multi: true },
    { provide: DateAdapter, useClass: MomentDateAdapter },
    { provide: MAT_DATE_FORMATS, useValue: MY_DATE_FORMATS },
    { provide: MAT_DATE_LOCALE, useValue: 'it' }, // Imposta la lingua italiana
    { provide: LOCALE_ID, useValue: 'it' } // Usa 'it' per l'italiano
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }
