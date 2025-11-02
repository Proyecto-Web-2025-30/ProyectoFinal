import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface Activity {
  id?: number;
  name: string;
  activityType: string;
  description: string;
  roleId?: number;
}

@Injectable({
  providedIn: 'root'
})
export class ActivityService {
  private apiUrl = 'http://localhost:8080/api/activities';

  constructor(private http: HttpClient) {}

  createActivity(processId: number, activity: Activity): Observable<Activity> {
    return this.http.post<Activity>(`${this.apiUrl}/process/${processId}`, activity);
  }

  updateActivity(activityId: number, activity: Activity): Observable<Activity> {
    return this.http.put<Activity>(`${this.apiUrl}/${activityId}`, activity);
  }

  deleteActivity(activityId: number): Observable<any> {
    return this.http.delete(`${this.apiUrl}/${activityId}`);
  }

  getActivity(activityId: number): Observable<Activity> {
    return this.http.get<Activity>(`${this.apiUrl}/${activityId}`);
  }

  getActivitiesByProcess(processId: number): Observable<Activity[]> {
    return this.http.get<Activity[]>(`${this.apiUrl}/process/${processId}`);
  }
}
