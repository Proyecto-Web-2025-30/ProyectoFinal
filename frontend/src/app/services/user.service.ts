import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface CreateUserRequest {
  username: string;
  email: string;
  password: string;
  fullName: string;
  userRole: string;
}

export interface AppUser {
  id: number;
  username: string;
  email: string;
  fullName: string;
}

@Injectable({
  providedIn: 'root'
})
export class UserService {
  private apiUrl = 'http://localhost:8080/api/users';

  constructor(private http: HttpClient) {}

  createUser(companyId: number, user: CreateUserRequest): Observable<any> {
    return this.http.post(`${this.apiUrl}/company/${companyId}`, user);
  }

  getUsersByCompany(companyId: number): Observable<AppUser[]> {
    return this.http.get<AppUser[]>(`${this.apiUrl}/company/${companyId}`);
  }
}
