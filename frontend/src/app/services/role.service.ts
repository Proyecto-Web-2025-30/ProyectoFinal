import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface Role {
  id?: number;
  name: string;
  description: string;
}

@Injectable({
  providedIn: 'root'
})
export class RoleService {
  private apiUrl = 'http://localhost:8080/api/roles';

  constructor(private http: HttpClient) {}

  createRole(companyId: number, role: Role): Observable<Role> {
    return this.http.post<Role>(`${this.apiUrl}/company/${companyId}`, role);
  }

  updateRole(roleId: number, role: Role): Observable<Role> {
    return this.http.put<Role>(`${this.apiUrl}/${roleId}`, role);
  }

  deleteRole(roleId: number): Observable<any> {
    return this.http.delete(`${this.apiUrl}/${roleId}`);
  }

  getRole(roleId: number): Observable<Role> {
    return this.http.get<Role>(`${this.apiUrl}/${roleId}`);
  }

  getRolesByCompany(companyId: number): Observable<Role[]> {
    return this.http.get<Role[]>(`${this.apiUrl}/company/${companyId}`);
  }
}
