export type EmploymentStatus = 'ACTIVE' | 'INACTIVE';

export interface EmployeeSummary {
  id: number;
  employeeNumber: string;
  firstName: string;
  lastName: string;
  countryCode: string;
  department: string;
  employmentStatus: EmploymentStatus;
}

export interface EmployeePage {
  employees: EmployeeSummary[];
  page: number;
  size: number;
  totalElements: number;
  totalPages: number;
}

export interface EmployeeSearch {
  search: string;
  country: string;
  department: string;
  status: EmploymentStatus | '';
  page: number;
  size: number;
}
