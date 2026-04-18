-- Second user for testing 1:1 flows: username `alice`, password `password`.
INSERT INTO users (
  id,
  employee_id,
  username,
  display_name,
  email,
  department,
  role,
  password_hash,
  active
) VALUES (
  '660e8400-e29b-41d4-a716-446655440002',
  'E002',
  'alice',
  'Alice User',
  'alice@intranet.local',
  'Engineering',
  'USER',
  '$2a$10$.eNkwzMCya0CBTjMKFqYfOOfeBM6KU6yXGkL0m90iUnrwMW9Uxoxe',
  true
);
