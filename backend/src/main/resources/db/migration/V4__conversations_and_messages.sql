-- Phase 3: direct messaging (channels added in a later phase).
CREATE TABLE conversations (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  type VARCHAR(32) NOT NULL,
  name VARCHAR(255),
  -- Deterministic key for DIRECT: min(uuid)|max(uuid) so duplicate 1:1 can be detected.
  direct_pair_key VARCHAR(80) UNIQUE,
  created_by UUID NOT NULL REFERENCES users (id),
  created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
  updated_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE INDEX idx_conversations_type ON conversations (type);

CREATE TABLE conversation_participants (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  conversation_id UUID NOT NULL REFERENCES conversations (id) ON DELETE CASCADE,
  user_id UUID NOT NULL REFERENCES users (id),
  joined_at TIMESTAMPTZ NOT NULL DEFAULT now(),
  last_read_message_id UUID,
  last_read_at TIMESTAMPTZ,
  UNIQUE (conversation_id, user_id)
);

CREATE INDEX idx_cp_user ON conversation_participants (user_id);
CREATE INDEX idx_cp_conversation ON conversation_participants (conversation_id);

CREATE TABLE messages (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  conversation_id UUID NOT NULL REFERENCES conversations (id) ON DELETE CASCADE,
  sender_id UUID NOT NULL REFERENCES users (id),
  content TEXT NOT NULL,
  created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
  updated_at TIMESTAMPTZ NOT NULL DEFAULT now(),
  deleted BOOLEAN NOT NULL DEFAULT false
);

CREATE INDEX idx_messages_conversation_created ON messages (conversation_id, created_at);
