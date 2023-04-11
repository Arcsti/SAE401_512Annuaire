import { GroupsGestionComponent } from './controlPanel/groups-gestion/groups-gestion.component';
import { MembersGestionComponent } from './controlPanel/membersGestion/membersGestion.component';
import { ControlPanelComponent } from './controlPanel/controlPanel.component';
import { LoginComponent } from './login/login.component';
import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { HomePageComponent } from './home-page/home-page.component';
import { ResultComponent } from './result/result.component';
import { InfosComponent } from './infos/infos.component';
const routes: Routes = [
  {
    path: '',
    redirectTo: '/home',
    pathMatch: 'full',
  },
  { path: 'home', component: HomePageComponent, },
  {
    path: 'login',
    component: LoginComponent,
  },
  {
    path: 'controlPanel',
    component: ControlPanelComponent
  },
  {
    path: 'controlPanel/members',
    component: MembersGestionComponent,
  }, {
    path: 'controlPanel/groups',
    component: GroupsGestionComponent,
  }, {
    path: 'infos',
    component: InfosComponent,
  }
];

@NgModule({
  imports: [RouterModule.forRoot(routes, { useHash: true })],
  exports: [RouterModule],
})
export class AppRoutingModule {

}
