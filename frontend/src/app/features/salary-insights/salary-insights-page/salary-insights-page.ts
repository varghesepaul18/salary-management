import { CurrencyPipe, DatePipe, DecimalPipe } from '@angular/common';
import { HttpErrorResponse } from '@angular/common/http';
import { Component, computed, inject, signal } from '@angular/core';
import { FormControl, FormGroup, ReactiveFormsModule } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { SalaryInsightsService } from '../data-access/salary-insights.service';
import { CurrencySummary, PayDistributionGroup, PayDistributionReport } from '../data-access/salary-insights.models';

type ReportResult = { kind: 'success'; report: PayDistributionReport } | { kind: 'failure'; error: unknown };

@Component({
  selector: 'app-salary-insights-page',
  imports: [CurrencyPipe, DatePipe, DecimalPipe, MatButtonModule, MatFormFieldModule, MatInputModule, MatSelectModule, ReactiveFormsModule],
  templateUrl: './salary-insights-page.html',
  styleUrl: './salary-insights-page.scss',
})
export class SalaryInsightsPage {
  private readonly salaryInsightsService = inject(SalaryInsightsService);

  protected readonly loading = signal(false);
  protected readonly errorMessage = signal('');
  protected readonly report = signal<PayDistributionReport | null>(null);
  protected readonly groups = computed(() => this.report()?.groups ?? []);
  protected readonly currencySummaries = computed(() => this.summarizeByCurrency(this.groups()));
  protected readonly totalEmployeeCount = computed(() => this.groups().reduce((count, group) => count + group.employeeCount, 0));
  protected readonly query = new FormGroup({
    asOfDate: new FormControl(this.localDateToday(), { nonNullable: true }),
    country: new FormControl('', { nonNullable: true }),
    department: new FormControl('', { nonNullable: true }),
  });

  protected readonly countries = [
    { code: 'DE', name: 'Germany' },
    { code: 'GB', name: 'United Kingdom' },
    { code: 'IN', name: 'India' },
    { code: 'JP', name: 'Japan' },
    { code: 'US', name: 'United States' },
  ];

  protected readonly departments = ['Engineering', 'Finance', 'Human Resources', 'Product', 'Sales'];

  constructor() {
    this.loadReport();
  }

  protected loadReport(): void {
    if (this.query.invalid || this.loading()) {
      this.query.markAllAsTouched();
      return;
    }
    this.loading.set(true);
    this.errorMessage.set('');
    this.salaryInsightsService.getPayDistribution(this.query.getRawValue()).subscribe({
      next: (report) => {
        this.report.set(report);
        this.loading.set(false);
      },
      error: (error: unknown) => {
        this.errorMessage.set(this.apiErrorMessage(error));
        this.loading.set(false);
      },
    });
  }

  private summarizeByCurrency(groups: PayDistributionGroup[]): CurrencySummary[] {
    const summaries = new Map<string, CurrencySummary>();
    for (const group of groups) {
      const summary = summaries.get(group.currencyCode) ?? {
        currencyCode: group.currencyCode,
        employeeCount: 0,
        groupCount: 0,
        totalAnnualBaseSalary: 0,
      };
      summary.employeeCount += group.employeeCount;
      summary.groupCount += 1;
      summary.totalAnnualBaseSalary += group.totalAnnualBaseSalary;
      summaries.set(group.currencyCode, summary);
    }
    return [...summaries.values()].sort((left, right) => left.currencyCode.localeCompare(right.currencyCode));
  }

  private localDateToday(): string {
    const now = new Date();
    return `${now.getFullYear()}-${String(now.getMonth() + 1).padStart(2, '0')}-${String(now.getDate()).padStart(2, '0')}`;
  }

  private apiErrorMessage(error: unknown): string {
    if (error instanceof HttpErrorResponse && error.status === 401) {
      return 'Your session has expired. Sign in again to view salary insights.';
    }
    if (error instanceof HttpErrorResponse && error.status === 400) {
      return 'The date or filter is not valid. Check your selections and try again.';
    }
    return 'Salary insights could not be loaded. Check your connection and try again.';
  }
}
