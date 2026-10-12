import { HttpErrorResponse } from '@angular/common/http';
import { DecimalPipe } from '@angular/common';
import { Component, computed, inject, signal } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { FormControl, FormGroup, ReactiveFormsModule } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { catchError, map, of, Subject, switchMap } from 'rxjs';
import { EmployeeDirectoryService } from '../data-access/employee-directory.service';
import { EmployeePage, EmployeeSearch, EmployeeSummary, EmploymentStatus } from '../data-access/employee-directory.models';

const PAGE_SIZE = 20;
const EMPTY_PAGE: EmployeePage = {
  employees: [],
  page: 0,
  size: PAGE_SIZE,
  totalElements: 0,
  totalPages: 0,
};

type EmployeeLoadResult =
  | { kind: 'success'; page: EmployeePage }
  | { kind: 'failure'; error: unknown };

@Component({
  selector: 'app-employee-directory-page',
  imports: [DecimalPipe, MatButtonModule, MatFormFieldModule, MatInputModule, MatSelectModule, ReactiveFormsModule],
  templateUrl: './employee-directory-page.html',
  styleUrl: './employee-directory-page.scss',
})
export class EmployeeDirectoryPage {
  private readonly employeeDirectoryService = inject(EmployeeDirectoryService);

  protected readonly employees = signal<EmployeeSummary[]>([]);
  protected readonly page = signal(0);
  protected readonly totalElements = signal(0);
  protected readonly totalPages = signal(0);
  protected readonly loading = signal(false);
  protected readonly errorMessage = signal('');
  protected readonly pageStart = computed(() => this.totalElements() === 0 ? 0 : this.page() * PAGE_SIZE + 1);
  protected readonly pageEnd = computed(() => Math.min((this.page() + 1) * PAGE_SIZE, this.totalElements()));
  protected readonly canGoPrevious = computed(() => this.page() > 0);
  protected readonly canGoNext = computed(() => this.page() + 1 < this.totalPages());

  protected readonly countries = [
    { code: 'DE', name: 'Germany' },
    { code: 'GB', name: 'United Kingdom' },
    { code: 'IN', name: 'India' },
    { code: 'JP', name: 'Japan' },
    { code: 'US', name: 'United States' },
  ];
  protected readonly departments = ['Engineering', 'Human Resources', 'Product', 'Sales'];
  protected readonly statuses: { value: EmploymentStatus; label: string }[] = [
    { value: 'ACTIVE', label: 'Active' },
    { value: 'INACTIVE', label: 'Inactive' },
  ];

  protected readonly filters = new FormGroup({
    search: new FormControl('', { nonNullable: true }),
    country: new FormControl('', { nonNullable: true }),
    department: new FormControl('', { nonNullable: true }),
    status: new FormControl<EmploymentStatus | ''>('', { nonNullable: true }),
  });

  private readonly requests = new Subject<EmployeeSearch>();

  constructor() {
    this.requests.pipe(
      switchMap((query) => this.employeeDirectoryService.search(query).pipe(
        map((page): EmployeeLoadResult => ({ kind: 'success', page })),
        catchError((error: unknown) => of<EmployeeLoadResult>({ kind: 'failure', error })),
      )),
      takeUntilDestroyed(),
    ).subscribe((result) => {
      if (result.kind === 'success') {
        this.employees.set(result.page.employees);
        this.page.set(result.page.page);
        this.totalElements.set(result.page.totalElements);
        this.totalPages.set(result.page.totalPages);
      } else {
        this.employees.set(EMPTY_PAGE.employees);
        this.totalElements.set(0);
        this.totalPages.set(0);
        this.errorMessage.set(this.apiErrorMessage(result.error));
      }
      this.loading.set(false);
    });
    this.loadEmployees();
  }

  protected applyFilters(): void {
    this.page.set(0);
    this.loadEmployees();
  }

  protected clearFilters(): void {
    this.filters.reset({ search: '', country: '', department: '', status: '' });
    this.applyFilters();
  }

  protected goToPage(page: number): void {
    if (page < 0 || page >= this.totalPages() || page === this.page()) {
      return;
    }
    this.page.set(page);
    this.loadEmployees();
  }

  protected retry(): void {
    this.loadEmployees();
  }

  private loadEmployees(): void {
    this.loading.set(true);
    this.errorMessage.set('');
    this.requests.next(this.searchQuery());
  }

  private searchQuery(): EmployeeSearch {
    const filters = this.filters.getRawValue();
    return {
      ...filters,
      page: this.page(),
      size: PAGE_SIZE,
    };
  }

  private apiErrorMessage(error: unknown): string {
    if (error instanceof HttpErrorResponse && error.status === 401) {
      return 'Your session has expired. Sign in again to continue.';
    }
    return 'We could not load employees. Check your connection and try again.';
  }
}
