import { HttpErrorResponse } from '@angular/common/http';
import { DatePipe, DecimalPipe, Location } from '@angular/common';
import { Component, computed, inject, signal } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { catchError, map, of, Subject, switchMap } from 'rxjs';
import { EmployeeSummary } from '../../employees/data-access/employee-directory.models';
import { RecordSalaryChangeRequest, SalaryHistoryEntry } from '../data-access/salary-history.models';
import { SalaryHistoryService } from '../data-access/salary-history.service';

type HistoryLoadResult =
  | { kind: 'success'; history: SalaryHistoryEntry[] }
  | { kind: 'failure'; error: unknown };

type EmployeeNavigationState = { employee?: unknown };

@Component({
  selector: 'app-employee-salary-history-page',
  imports: [DatePipe, DecimalPipe, MatButtonModule, MatFormFieldModule, MatInputModule, MatSelectModule, ReactiveFormsModule, RouterLink],
  templateUrl: './employee-salary-history-page.html',
  styleUrl: './employee-salary-history-page.scss',
})
export class EmployeeSalaryHistoryPage {
  private readonly route = inject(ActivatedRoute);
  private readonly location = inject(Location);
  private readonly salaryHistoryService = inject(SalaryHistoryService);
  private readonly historyRequests = new Subject<number>();

  protected readonly employeeId = Number(this.route.snapshot.paramMap.get('employeeId'));
  protected readonly employee = signal(this.readEmployeeFromNavigation());
  protected readonly history = signal<SalaryHistoryEntry[]>([]);
  protected readonly currentSalary = computed(() => this.history()[0] ?? null);
  protected readonly loadingHistory = signal(false);
  protected readonly saving = signal(false);
  protected readonly historyError = signal('');
  protected readonly formError = signal('');
  protected readonly successMessage = signal('');

  protected readonly currencies = [
    { code: 'EUR', name: 'Euro' },
    { code: 'GBP', name: 'British pound' },
    { code: 'INR', name: 'Indian rupee' },
    { code: 'JPY', name: 'Japanese yen' },
    { code: 'USD', name: 'US dollar' },
  ];

  protected readonly form = new FormGroup({
    annualBaseSalary: new FormControl<number | null>(null, [Validators.required, Validators.min(0.0001)]),
    currencyCode: new FormControl('', { nonNullable: true, validators: [Validators.required, Validators.pattern(/^[A-Z]{3}$/)] }),
    effectiveFrom: new FormControl(this.localDateToday(), { nonNullable: true, validators: [Validators.required] }),
  });

  constructor() {
    this.historyRequests.pipe(
      switchMap((employeeId) => this.salaryHistoryService.getHistory(employeeId).pipe(
        map((history): HistoryLoadResult => ({ kind: 'success', history })),
        catchError((error: unknown) => of<HistoryLoadResult>({ kind: 'failure', error })),
      )),
      takeUntilDestroyed(),
    ).subscribe((result) => {
      if (result.kind === 'success') {
        this.history.set(result.history);
      } else {
        this.historyError.set(this.apiErrorMessage(result.error, 'history'));
      }
      this.loadingHistory.set(false);
    });

    if (Number.isSafeInteger(this.employeeId) && this.employeeId > 0) {
      this.loadHistory();
    } else {
      this.historyError.set('This employee could not be identified. Return to the directory and try again.');
    }
  }

  protected loadHistory(): void {
    this.loadingHistory.set(true);
    this.historyError.set('');
    this.historyRequests.next(this.employeeId);
  }

  protected recordSalaryChange(): void {
    if (this.form.invalid || this.saving()) {
      this.form.markAllAsTouched();
      return;
    }

    const request: RecordSalaryChangeRequest = {
      annualBaseSalary: this.form.controls.annualBaseSalary.value!,
      currencyCode: this.form.controls.currencyCode.value,
      effectiveFrom: this.form.controls.effectiveFrom.value,
    };

    this.saving.set(true);
    this.formError.set('');
    this.successMessage.set('');
    this.salaryHistoryService.recordChange(this.employeeId, request).subscribe({
      next: (created) => {
        this.saving.set(false);
        this.successMessage.set(`Salary change saved, effective ${created.effectiveFrom}.`);
        this.form.reset({ annualBaseSalary: null, currencyCode: '', effectiveFrom: this.localDateToday() });
        this.loadHistory();
      },
      error: (error: unknown) => {
        this.saving.set(false);
        this.formError.set(this.apiErrorMessage(error, 'save'));
      },
    });
  }

  private localDateToday(): string {
    const now = new Date();
    const year = now.getFullYear();
    const month = String(now.getMonth() + 1).padStart(2, '0');
    const day = String(now.getDate()).padStart(2, '0');
    return `${year}-${month}-${day}`;
  }

  private readEmployeeFromNavigation(): EmployeeSummary | null {
    const state = this.location.getState() as EmployeeNavigationState;
    const employee = state.employee;
    if (typeof employee !== 'object' || employee === null) {
      return null;
    }
    const candidate = employee as Partial<EmployeeSummary>;
    return typeof candidate.id === 'number'
      && typeof candidate.employeeNumber === 'string'
      && typeof candidate.firstName === 'string'
      && typeof candidate.lastName === 'string'
      && typeof candidate.countryCode === 'string'
      && typeof candidate.department === 'string'
      && typeof candidate.employmentStatus === 'string'
      ? candidate as EmployeeSummary
      : null;
  }

  private apiErrorMessage(error: unknown, action: 'history' | 'save'): string {
    if (error instanceof HttpErrorResponse) {
      if (error.status === 401) {
        return 'Your session has expired. Sign in again to continue.';
      }
      if (error.status === 404) {
        return 'This employee could not be found.';
      }
      if (error.status === 409 && action === 'save') {
        return 'A salary entry already exists for that effective date. Choose another date.';
      }
      if (error.status === 400 && action === 'save') {
        return 'Check the salary, currency, and effective date, then try again.';
      }
    }
    return action === 'history'
      ? 'Salary history could not be loaded. Check your connection and try again.'
      : 'The salary change could not be saved. Check your connection and try again.';
  }
}
