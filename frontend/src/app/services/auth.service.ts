import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable, BehaviorSubject, throwError } from 'rxjs';
import { tap, map } from 'rxjs/operators';

export interface LoginRequest { username: string; password: string; }
export interface RegisterCompanyRequest {
  companyName: string; nit: string; contactEmail: string;
  adminUsername: string; adminEmail: string; adminPassword: string; adminFullName: string;
}
export interface User {
  userId: number; username: string; email: string; fullName: string;
  companyId: number; companyName: string;
}

@Injectable({ providedIn: 'root' })
export class AuthService {
  private apiUrl = 'http://localhost:8080/api';
  private currentUserSubject = new BehaviorSubject<User | null>(null);
  public currentUser$ = this.currentUserSubject.asObservable();

  constructor(private http: HttpClient) {
    const savedUser = localStorage.getItem('currentUser');
    if (savedUser) this.currentUserSubject.next(JSON.parse(savedUser));
  }

  private setToken(token: string): void {
    localStorage.setItem('access_token', token);
  }

  registerCompany(request: RegisterCompanyRequest): Observable<any> {
    return this.http.post(`${this.apiUrl}/companies/register`, request);
  }

  login(request: LoginRequest): Observable<any> {
    return this.http.post(`${this.apiUrl}/auth/login`, request).pipe(
      tap((response: any) => {
        // Guarda el JWT
        this.setToken(response.token);

        const user: User = {
          userId: response.userId,
          username: response.username,
          email: response.email,
          fullName: response.fullName,
          companyId: response.companyId,
          companyName: response.companyName
        };
        localStorage.setItem('currentUser', JSON.stringify(user));
        this.currentUserSubject.next(user);
      })
    );
  }

  logout(): void {
    localStorage.removeItem('access_token');   //  borramos el token al hacer logout
    localStorage.removeItem('currentUser');
    this.currentUserSubject.next(null);
  }

  getCurrentUser(): User | null {
    return this.currentUserSubject.value;
  }

  getToken(): string | null {                 //  lo usa el interceptor
    return localStorage.getItem('access_token');
  }

  /**
   * Llama al endpoint de refresco del backend usando el token actual
   * (aunque esté expirado) y guarda el nuevo token devuelto.
   */
  refreshToken(): Observable<string> {
    const token = this.getToken();
    if (!token) {
      return throwError(() => new Error('No access token stored'));
    }

    const headers = new HttpHeaders({
      Authorization: `Bearer ${token}`
    });

    return this.http.post(`${this.apiUrl}/auth/refresh`, {}, { headers }).pipe(
      tap((response: any) => {
        if (response?.token) {
          this.setToken(response.token);
        }
      }),
      map((response: any) => response.token as string)
    );
  }

  isLoggedIn(): boolean {
    return this.getToken() !== null;
  }
}
