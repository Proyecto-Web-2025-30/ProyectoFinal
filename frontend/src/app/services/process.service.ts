import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface Process {
  id?: number;
  name: string;
  description: string;
  category: string;
  status: 'DRAFT' | 'PUBLISHED';
  active?: boolean;
  createdAt?: string;
  updatedAt?: string;
}

@Injectable({
  providedIn: 'root'
})
export class ProcessService {
  private apiUrl = 'http://localhost:8080/api/processes';

  constructor(private http: HttpClient) {}

  createProcess(companyId: number, process: Process): Observable<Process> {
    return this.http.post<Process>(`${this.apiUrl}/company/${companyId}`, process);
  }

  updateProcess(processId: number, process: Process): Observable<Process> {
    return this.http.put<Process>(`${this.apiUrl}/${processId}`, process);
  }

  deleteProcess(processId: number): Observable<any> {
    return this.http.delete(`${this.apiUrl}/${processId}`);
  }

  getProcess(processId: number): Observable<Process> {
    return this.http.get<Process>(`${this.apiUrl}/${processId}`);
  }

  getProcessesByCompany(companyId: number, category?: string): Observable<Process[]> {
    let url = `${this.apiUrl}/company/${companyId}`;
    if (category) {
      url += `?category=${category}`;
    }
    return this.http.get<Process[]>(url);
  }
}
