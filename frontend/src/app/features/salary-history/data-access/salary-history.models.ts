export interface SalaryHistoryEntry {
  employeeId: number;
  annualBaseSalary: number;
  currencyCode: string;
  effectiveFrom: string;
  changedBy: string;
  changedAt: string;
}

export interface RecordSalaryChangeRequest {
  annualBaseSalary: number;
  currencyCode: string;
  effectiveFrom: string;
}
