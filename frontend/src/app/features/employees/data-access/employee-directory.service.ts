import { HttpClient, HttpParams } from '@angular/common/http';
import { inject, Service } from '@angular/core';
import { Observable } from 'rxjs';
import { API_BASE_URL } from '../../../core/api/api.config';
import { EmployeePage, EmployeeSearch } from './employee-directory.models';

@Service()
export class EmployeeDirectoryService {
  private readonly http = inject(HttpClient);

  search(query: EmployeeSearch): Observable<EmployeePage> {
    let params = new HttpParams()
      .set('page', query.page)
      .set('size', query.size);

    if (query.search.trim()) {
      params = params.set('search', query.search.trim());
    }
    if (query.country) {
      params = params.set('country', query.country);
    }
    if (query.department) {
      params = params.set('department', query.department);
    }
    if (query.status) {
      params = params.set('status', query.status);
    }

    return this.http.get<EmployeePage>(`${API_BASE_URL}/api/employees`, { params });
  }
}
