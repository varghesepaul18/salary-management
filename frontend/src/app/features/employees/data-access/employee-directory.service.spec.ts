import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting, HttpTestingController } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { afterEach, beforeEach, describe, expect, it } from 'vitest';
import { API_BASE_URL } from '../../../core/api/api.config';
import { EmployeeDirectoryService } from './employee-directory.service';

describe('EmployeeDirectoryService', () => {
  let service: EmployeeDirectoryService;
  let httpTestingController: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        EmployeeDirectoryService,
      ],
    });
    service = TestBed.inject(EmployeeDirectoryService);
    httpTestingController = TestBed.inject(HttpTestingController);
  });

  afterEach(() => httpTestingController.verify());

  it('requests a filtered employee page using the API query contract', () => {
    service.search({
      search: '  Varghese  ',
      country: 'GB',
      department: 'Product',
      status: 'ACTIVE',
      page: 2,
      size: 20,
    }).subscribe();

    const request = httpTestingController.expectOne((candidate) =>
      candidate.url === `${API_BASE_URL}/api/employees`);
    expect(request.request.method).toBe('GET');
    expect(request.request.params.get('search')).toBe('Varghese');
    expect(request.request.params.get('country')).toBe('GB');
    expect(request.request.params.get('department')).toBe('Product');
    expect(request.request.params.get('status')).toBe('ACTIVE');
    expect(request.request.params.get('page')).toBe('2');
    expect(request.request.params.get('size')).toBe('20');
    request.flush({ employees: [], page: 2, size: 20, totalElements: 0, totalPages: 0 });
  });

  it('omits blank filters while preserving paging parameters', () => {
    service.search({
      search: '  ',
      country: '',
      department: '',
      status: '',
      page: 0,
      size: 20,
    }).subscribe();

    const request = httpTestingController.expectOne((candidate) =>
      candidate.url === `${API_BASE_URL}/api/employees`);
    expect(request.request.params.keys().sort()).toEqual(['page', 'size']);
    request.flush({ employees: [], page: 0, size: 20, totalElements: 0, totalPages: 0 });
  });
});
