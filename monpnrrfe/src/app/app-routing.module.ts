/*
* SPDX-FileCopyrightText: (C) Copyright 2025 Regione Piemonte
*
* SPDX-License-Identifier: EUPL-1.2 
*/

import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { ErrorLoginPageComponent } from './error-login-page/error-login-page.component';
import { LoginPageComponent } from './login-page/login-page.component';
import { MainPageComponent } from './main-page/main-page.component';
import {ProjectListComponent} from "./project-list/project-list.component";
import { ChecklistPageComponent } from './checklist-page/checklist-page.component';

const routes: Routes = [
  { path: '', component: LoginPageComponent, pathMatch: 'full' },
  { path: 'login', component: LoginPageComponent },
  { path: 'login-error-page', component: ErrorLoginPageComponent },
  {
    path: 'main-page', component: MainPageComponent, children: [
      { path: 'progetti', component: ProjectListComponent },
      { path: 'checklist/:cup/:mode/:choise', component: ChecklistPageComponent },
    ]
  },
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
