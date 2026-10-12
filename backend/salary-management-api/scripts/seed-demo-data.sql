BEGIN;

INSERT INTO employees (
    employee_number,
    first_name,
    last_name,
    country_code,
    department,
    employment_status
)
SELECT
    'ACME-DEMO-' || LPAD(sequence.employee_index::text, 5, '0'),
    (ARRAY['Ava', 'Noah', 'Mia', 'Liam', 'Isha', 'Arjun', 'Emma', 'Oliver'])
        [((sequence.employee_index - 1) % 8) + 1],
    (ARRAY['Patel', 'Smith', 'Chen', 'Kumar', 'Garcia', 'Brown', 'Singh', 'Miller'])
        [((sequence.employee_index - 1) % 8) + 1],
    CASE sequence.employee_index % 5
        WHEN 0 THEN 'US'
        WHEN 1 THEN 'IN'
        WHEN 2 THEN 'GB'
        WHEN 3 THEN 'DE'
        ELSE 'JP'
    END,
    CASE (sequence.employee_index - 1) % 6
        WHEN 0 THEN 'Engineering'
        WHEN 1 THEN 'Human Resources'
        WHEN 2 THEN 'Finance'
        WHEN 3 THEN 'Sales'
        WHEN 4 THEN 'Operations'
        ELSE 'Product'
    END,
    CASE WHEN sequence.employee_index % 17 = 0 THEN 'INACTIVE' ELSE 'ACTIVE' END
FROM generate_series(1, 10000) AS sequence(employee_index)
ON CONFLICT (employee_number) DO NOTHING;

INSERT INTO salary_history (
    employee_id,
    annual_base_salary,
    currency_code,
    effective_from,
    changed_by,
    changed_at
)
SELECT
    employee.id,
    CASE employee.country_code
        WHEN 'US' THEN 70000 + (demo.sequence_number % 80000)
        WHEN 'IN' THEN 600000 + (demo.sequence_number % 1800000)
        WHEN 'GB' THEN 35000 + (demo.sequence_number % 65000)
        WHEN 'DE' THEN 40000 + (demo.sequence_number % 70000)
        WHEN 'JP' THEN 5000000 + (demo.sequence_number % 10000000)
    END,
    CASE employee.country_code
        WHEN 'US' THEN 'USD'
        WHEN 'IN' THEN 'INR'
        WHEN 'GB' THEN 'GBP'
        WHEN 'DE' THEN 'EUR'
        WHEN 'JP' THEN 'JPY'
    END,
    DATE '2025-01-01',
    'demo-seed',
    TIMESTAMPTZ '2025-01-01 00:00:00+00'
FROM employees employee
CROSS JOIN LATERAL (
    SELECT SPLIT_PART(employee.employee_number, '-', 3)::integer AS sequence_number
) demo
WHERE employee.employee_number LIKE 'ACME-DEMO-%'
ON CONFLICT (employee_id, effective_from) DO NOTHING;

COMMIT;
BEGIN;

INSERT INTO employees (
    employee_number,
    first_name,
    last_name,
    country_code,
    department,
    employment_status
)
SELECT
    'ACME-DEMO-' || LPAD(sequence.employee_index::text, 5, '0'),
    (ARRAY['Ava', 'Noah', 'Mia', 'Liam', 'Isha', 'Arjun', 'Emma', 'Oliver'])
        [((sequence.employee_index - 1) % 8) + 1],
    (ARRAY['Patel', 'Smith', 'Chen', 'Kumar', 'Garcia', 'Brown', 'Singh', 'Miller'])
        [((sequence.employee_index - 1) % 8) + 1],
    CASE sequence.employee_index % 5
        WHEN 0 THEN 'US'
        WHEN 1 THEN 'IN'
        WHEN 2 THEN 'GB'
        WHEN 3 THEN 'DE'
        ELSE 'JP'
    END,
    CASE (sequence.employee_index - 1) % 6
        WHEN 0 THEN 'Engineering'
        WHEN 1 THEN 'Human Resources'
        WHEN 2 THEN 'Finance'
        WHEN 3 THEN 'Sales'
        WHEN 4 THEN 'Operations'
        ELSE 'Product'
    END,
    CASE WHEN sequence.employee_index % 17 = 0 THEN 'INACTIVE' ELSE 'ACTIVE' END
FROM generate_series(1, 10000) AS sequence(employee_index)
ON CONFLICT (employee_number) DO NOTHING;

INSERT INTO salary_history (
    employee_id,
    annual_base_salary,
    currency_code,
    effective_from,
    changed_by,
    changed_at
)
SELECT
    employee.id,
    CASE employee.country_code
        WHEN 'US' THEN 70000 + (demo.sequence_number % 80000)
        WHEN 'IN' THEN 600000 + (demo.sequence_number % 1800000)
        WHEN 'GB' THEN 35000 + (demo.sequence_number % 65000)
        WHEN 'DE' THEN 40000 + (demo.sequence_number % 70000)
        WHEN 'JP' THEN 5000000 + (demo.sequence_number % 10000000)
    END,
    CASE employee.country_code
        WHEN 'US' THEN 'USD'
        WHEN 'IN' THEN 'INR'
        WHEN 'GB' THEN 'GBP'
        WHEN 'DE' THEN 'EUR'
        WHEN 'JP' THEN 'JPY'
    END,
    DATE '2025-01-01',
    'demo-seed',
    TIMESTAMPTZ '2025-01-01 00:00:00+00'
FROM employees employee
CROSS JOIN LATERAL (
    SELECT SPLIT_PART(employee.employee_number, '-', 3)::integer AS sequence_number
) demo
WHERE employee.employee_number LIKE 'ACME-DEMO-%'
ON CONFLICT (employee_id, effective_from) DO NOTHING;

COMMIT;
