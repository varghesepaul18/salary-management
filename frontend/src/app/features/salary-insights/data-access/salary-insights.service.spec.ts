import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting, HttpTestingController } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { afterEach, beforeEach, describe, expect, it } from 'vitest';
import { API_BASE_URL } from '../../../core/api/api.config';
import { SalaryInsightsService } from './salary-insights.service';

describe('SalaryInsightsService', () => {
  let service: SalaryInsightsService;
  let httpTestingController: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting(), SalaryInsightsService],
    });
    service = TestBed.inject(SalaryInsightsService);
    httpTestingController = TestBed.inject(HttpTestingController);
  });

  afterEach(() => httpTestingController.verify());

  it('requests salary groups for the selected date and filters', () => {
    service.getPayDistribution({ asOfDate: '2026-10-13', country: 'US', department: 'Engineering' }).subscribe((report) => {
      expect(report.asOfDate).toBe('2026-10-13');
      expect(report.groups).toHaveLength(1);
    });

    const request = httpTestingController.expectOne((candidate) =>
      candidate.url === `${API_BASE_URL}/api/analytics/pay-distribution`
      && candidate.params.get('asOfDate') === '2026-10-13'
      && candidate.params.get('country') === 'US'
      && candidate.params.get('department') === 'Engineering');
    expect(request.request.method).toBe('GET');
    request.flush({ asOfDate: '2026-10-13', groups: [{ countryCode: 'US', department: 'Engineering', currencyCode: 'USD', employeeCount: 1, totalAnnualBaseSalary: 95000, averageAnnualBaseSalary: 95000, medianAnnualBaseSalary: 95000, minimumAnnualBaseSalary: 95000, maximumAnnualBaseSalary: 95000 }] });
  });
});
