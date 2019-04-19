import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import {PostCreateComponent} from './posts/post-create/post-create.component';
import {PostListComponent} from './posts/post-list/post-list.component';
import {AuthGuard} from './auth/auth-guard';

const routes: Routes = [
  {path: '', component: PostListComponent},
  {path: 'create', component: PostCreateComponent, canActivate: [AuthGuard]},
  {path: 'edit/:postId', component: PostCreateComponent, canActivate: [AuthGuard]},
  // LAZY LOADING: loadChildren field has path to module (without .ts extension)
  // THEN -> # <- ('separator') after which goes the Class Name of same module.ts file
  // Routes, for example, become /auth/login or /auth/signup
  // Then remove module import in app.module.ts so it isn't loaded eagerly anymore
  {path: 'auth', loadChildren: './auth/auth.module#AuthModule'}
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule],
  providers: [AuthGuard]
})
export class AppRoutingModule { }
