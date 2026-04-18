-- Phase 1 baseline; domain tables are added in later phases.
CREATE TABLE IF NOT EXISTS schema_marker (
  id SERIAL PRIMARY KEY,
  created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);
