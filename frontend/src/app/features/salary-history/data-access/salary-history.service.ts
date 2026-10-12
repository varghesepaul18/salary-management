import { HttpClient } from '@angular/common/http';
import { inject, Service } from '@angular/core';
import { Observable } from 'rxjs';
import { API_BASE_URL } from '../../../core/api/api.config';
import { RecordSalaryChangeRequest, SalaryHistoryEntry } from './salary-history.models';

@Service()
export class SalaryHistoryService {
  private readonly http = inject(HttpClient);

  getHistory(employeeId: number): Observable<SalaryHistoryEntry[]> {
    return this.http.get<SalaryHistoryEntry[]>(this.endpoint(employeeId));
  }

  recordChange(employeeId: number, request: RecordSalaryChangeRequest): Observable<SalaryHistoryEntry> {
    return this.http.post<SalaryHistoryEntry>(this.endpoint(employeeId), request);
  }

  private endpoint(employeeId: number): string {
    return `${API_BASE_URL}/api/employees/${employeeId}/salary-history`;
  }
}
