import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface Gateway {
  id?: number;
  name: string;
  gatewayType: 'EXCLUSIVE' | 'PARALLEL' | 'INCLUSIVE';
  conditions: string;
  x?: number;
  y?: number;
}

@Injectable({
  providedIn: 'root'
})
export class GatewayService {
  private apiUrl = 'http://localhost:8080/api/gateways';

  constructor(private http: HttpClient) {}

  createGateway(processId: number, gateway: Gateway): Observable<Gateway> {
    console.log(gateway.x);
    console.log(gateway.y);
    return this.http.post<Gateway>(`${this.apiUrl}/process/${processId}`, gateway);
  }

  updateGateway(gatewayId: number, gateway: Gateway): Observable<Gateway> {
    console.log(gateway.x);
    console.log(gateway.y);
    return this.http.put<Gateway>(`${this.apiUrl}/${gatewayId}`, gateway);
  }

  deleteGateway(gatewayId: number): Observable<any> {
    return this.http.delete(`${this.apiUrl}/${gatewayId}`);
  }

  getGateway(gatewayId: number): Observable<Gateway> {
    return this.http.get<Gateway>(`${this.apiUrl}/${gatewayId}`);
  }

  getGatewaysByProcess(processId: number): Observable<Gateway[]> {
    return this.http.get<Gateway[]>(`${this.apiUrl}/process/${processId}`);
  }
}
