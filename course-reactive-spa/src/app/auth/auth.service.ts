import { Injectable } from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {environment} from '../../environments/environment';
import {AuthResponseModel} from './auth-response.model';
import {AuthRequestModel} from './auth-request.model';
import {Subject} from 'rxjs';
import {Router} from '@angular/router';

const SIGN_UP = '/auth/signup';
const LOGIN = '/auth/login';

@Injectable({
  providedIn: 'root'
})
export class AuthService {

  private isAuthenticated = false;
  private token: string;
  private username: string;
  private authStatusListener = new Subject<boolean>();
  private tokenTimer: any;

  constructor(private httpClient: HttpClient,
              private router: Router) { }

  getAuthStatusListener() {
    return this.authStatusListener.asObservable();
  }

  getToken() {
    return this.token;
  }

  getIsAuth() {
    return this.isAuthenticated;
  }

  getUsername() {
    return this.username;
  }

  createUser(emailInput: string, passwordInput: string) {
    const authRequest: AuthRequestModel = {
      username: emailInput,
      password: passwordInput
    };
    this.httpClient.post<boolean>(environment.apiUrl + SIGN_UP, authRequest)
      .subscribe(
        () => {
          this.login(authRequest.username, authRequest.password);
        }, () => {
          this.authStatusListener.next(false);
        });
  }

  login(emailInput: string, passwordInput: string) {
    const authRequest: AuthRequestModel = {
      username: emailInput,
      password: passwordInput
    };
    this.httpClient.post<AuthResponseModel | any>(environment.apiUrl + LOGIN, authRequest)
      .subscribe(
        response => {
          if (response.token) {
            const expiresInDuration = response.expiresIn;
            this.setAuthTimer(expiresInDuration);
            this.token = response.token;
            this.isAuthenticated = true;
            this.authStatusListener.next(true);
            this.username = response.username;
            const now = new Date();
            const expirationDate = new Date(now.getTime() + expiresInDuration * 1000);
            this.saveAuthData(this.token, expirationDate, this.username);
            this.router.navigate(['/']);
          } else {
            console.log(response);
          }
        }, () => {
          this.authStatusListener.next(false);
        });
  }

  autoAuthUser() {
    const authInformation = this.getAuthData();
    if (!authInformation) {
      return;
    }
    const now = new Date();
    const expiresIn = authInformation.expirationDate.getTime() - now.getTime();
    if (expiresIn > 0) {
      this.token = authInformation.token;
      this.isAuthenticated = true;
      this.username = authInformation.username;
      this.setAuthTimer(expiresIn / 1000);
      this.authStatusListener.next(true);
    }
  }

  logout() {
    this.token = null;
    this.isAuthenticated = false;
    this.authStatusListener.next(false);
    clearTimeout(this.tokenTimer);
    this.clearAuthData();
    this.username = null;
    this.router.navigate(['/']);
  }

  private setAuthTimer(duration: number) {
    this.tokenTimer = setTimeout(() => {
      this.logout();
    }, duration * 1000);
  }

  private saveAuthData(token: string, expirationDate: Date, username: string) {
    localStorage.setItem('token', token);
    localStorage.setItem('expiration', expirationDate.toISOString());
    localStorage.setItem('username', username);
  }

  private clearAuthData() {
    localStorage.removeItem('token');
    localStorage.removeItem('expiration');
    localStorage.removeItem('username');
  }

  private getAuthData() {
    const tokenConst = localStorage.getItem('token');
    const expirationDate = localStorage.getItem('expiration');
    const usernameConst = localStorage.getItem('username');
    if (!tokenConst && !expirationDate) {
      return;
    }
    return {
      token: tokenConst,
      expirationDate: new Date(expirationDate),
      username: usernameConst
    };
  }
}
