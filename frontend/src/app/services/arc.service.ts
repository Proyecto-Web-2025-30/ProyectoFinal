import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface Arc {
  id?: number;
  sourceType: string;
  sourceId: number;
  targetType: string;
  targetId: number;
}

@Injectable({
  providedIn: 'root'
})
export class ArcService {
  private apiUrl = 'http://localhost:8080/api/arcs';

  constructor(private http: HttpClient) {}

  createArc(processId: number, arc: Arc): Observable<Arc> {
    return this.http.post<Arc>(`${this.apiUrl}/process/${processId}`, arc);
  }

  updateArc(arcId: number, arc: Arc): Observable<Arc> {
    return this.http.put<Arc>(`${this.apiUrl}/${arcId}`, arc);
  }

  deleteArc(arcId: number): Observable<any> {
    return this.http.delete(`${this.apiUrl}/${arcId}`);
  }

  getArc(arcId: number): Observable<Arc> {
    return this.http.get<Arc>(`${this.apiUrl}/${arcId}`);
  }

  getArcsByProcess(processId: number): Observable<Arc[]> {
    return this.http.get<Arc[]>(`${this.apiUrl}/process/${processId}`);
  }
}
