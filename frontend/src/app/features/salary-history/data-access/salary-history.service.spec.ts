import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting, HttpTestingController } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { afterEach, beforeEach, describe, expect, it } from 'vitest';
import { API_BASE_URL } from '../../../core/api/api.config';
import { SalaryHistoryService } from './salary-history.service';

describe('SalaryHistoryService', () => {
  let service: SalaryHistoryService;
  let httpTestingController: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        SalaryHistoryService,
      ],
    });
    service = TestBed.inject(SalaryHistoryService);
    httpTestingController = TestBed.inject(HttpTestingController);
  });

  afterEach(() => httpTestingController.verify());

  it('loads salary history for the requested employee', () => {
    service.getHistory(42).subscribe((entries) => expect(entries).toEqual([]));

    const request = httpTestingController.expectOne(`${API_BASE_URL}/api/employees/42/salary-history`);
    expect(request.request.method).toBe('GET');
    request.flush([]);
  });

  it('records a salary change using the backend request shape', () => {
    const change = {
      annualBaseSalary: 95000,
      currencyCode: 'USD',
      effectiveFrom: '2026-10-13',
    };
    service.recordChange(42, change).subscribe();

    const request = httpTestingController.expectOne(`${API_BASE_URL}/api/employees/42/salary-history`);
    expect(request.request.method).toBe('POST');
    expect(request.request.body).toEqual(change);
    request.flush({ employeeId: 42, ...change, changedBy: 'hr-manager', changedAt: '2026-10-13T10:00:00Z' });
  });
});
