-- Phase 4: seeded team channels (fixed ids for tests). Both demo and alice are members.
INSERT INTO conversations (id, type, name, direct_pair_key, created_by, created_at, updated_at) VALUES
(
  '880e8400-e29b-41d4-a716-446655440011',
  'CHANNEL',
  'general',
  NULL,
  '550e8400-e29b-41d4-a716-446655440001',
  now(),
  now()
),
(
  '880e8400-e29b-41d4-a716-446655440012',
  'CHANNEL',
  'engineering',
  NULL,
  '550e8400-e29b-41d4-a716-446655440001',
  now(),
  now()
);

INSERT INTO conversation_participants (id, conversation_id, user_id, joined_at) VALUES
(gen_random_uuid(), '880e8400-e29b-41d4-a716-446655440011', '550e8400-e29b-41d4-a716-446655440001', now()),
(gen_random_uuid(), '880e8400-e29b-41d4-a716-446655440011', '660e8400-e29b-41d4-a716-446655440002', now()),
(gen_random_uuid(), '880e8400-e29b-41d4-a716-446655440012', '550e8400-e29b-41d4-a716-446655440001', now()),
(gen_random_uuid(), '880e8400-e29b-41d4-a716-446655440012', '660e8400-e29b-41d4-a716-446655440002', now());
