export interface PayDistributionGroup {
  countryCode: string;
  department: string;
  currencyCode: string;
  employeeCount: number;
  totalAnnualBaseSalary: number;
  averageAnnualBaseSalary: number;
  medianAnnualBaseSalary: number;
  minimumAnnualBaseSalary: number;
  maximumAnnualBaseSalary: number;
}

export interface PayDistributionReport {
  asOfDate: string;
  groups: PayDistributionGroup[];
}

export interface PayDistributionQuery {
  asOfDate: string;
  country: string;
  department: string;
}

export interface CurrencySummary {
  currencyCode: string;
  employeeCount: number;
  groupCount: number;
  totalAnnualBaseSalary: number;
}
