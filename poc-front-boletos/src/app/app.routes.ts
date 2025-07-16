import { Routes } from '@angular/router';
import { HomeComponent } from './home/home.component';
import { TestesComponent } from './testes/testes.component';
import { UploadMultiplosComponent } from './upload-multiplos/upload-multiplos.component';

export const routes: Routes = [
  { path: '', component: HomeComponent },
  { path: 'testes', component: TestesComponent },
  { path: 'upload-multiplos', component: UploadMultiplosComponent },
  { path: '**', redirectTo: '' }
];
