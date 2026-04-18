/**
 * Fixed UUIDs from Flyway seed data / `api-endpoints-curl.md` for local dev.
 * Replace with a users directory API when available.
 */
export type SeedUser = {
  id: string;
  username: string;
  label: string;
};

export const INTRANET_SEED_USERS: SeedUser[] = [
  {
    id: "550e8400-e29b-41d4-a716-446655440001",
    username: "demo",
    label: "demo",
  },
  {
    id: "660e8400-e29b-41d4-a716-446655440002",
    username: "alice",
    label: "alice",
  },
];
