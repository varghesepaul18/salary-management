import { HttpClient, HttpParams } from '@angular/common/http';
import { inject, Service } from '@angular/core';
import { Observable } from 'rxjs';
import { API_BASE_URL } from '../../../core/api/api.config';
import { PayDistributionQuery, PayDistributionReport } from './salary-insights.models';

@Service()
export class SalaryInsightsService {
  private readonly http = inject(HttpClient);

  getPayDistribution(query: PayDistributionQuery): Observable<PayDistributionReport> {
    let params = new HttpParams().set('asOfDate', query.asOfDate);
    if (query.country) {
      params = params.set('country', query.country);
    }
    if (query.department) {
      params = params.set('department', query.department);
    }
    return this.http.get<PayDistributionReport>(`${API_BASE_URL}/api/analytics/pay-distribution`, { params });
  }
}
