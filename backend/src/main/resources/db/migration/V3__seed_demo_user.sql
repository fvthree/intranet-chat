-- Demo user: username `demo`, password `password` (local/dev only; change in production).
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
  '550e8400-e29b-41d4-a716-446655440001',
  'E001',
  'demo',
  'Demo User',
  'demo@intranet.local',
  'Engineering',
  'USER',
  '$2a$10$.eNkwzMCya0CBTjMKFqYfOOfeBM6KU6yXGkL0m90iUnrwMW9Uxoxe',
  true
);
