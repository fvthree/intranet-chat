"use client";

import { useEffect, useMemo, useState } from "react";
import { useCurrentUser } from "@/components/intranet/current-user-context";
import { INTRANET_SEED_USERS } from "@/config/intranet-seed-users";
import {
  createChannel,
  createDirectConversation,
} from "@/lib/intranet-api/conversations";

type Props = {
  /** Called with the conversation id after a successful create or open-direct. */
  onCreated: (conversationId: string) => void;
};

export function CreateConversationForms({ onCreated }: Props) {
  const { user, loading: userLoading } = useCurrentUser();
  const [dmOtherId, setDmOtherId] = useState("");
  const [channelName, setChannelName] = useState("");
  const [dmLoading, setDmLoading] = useState(false);
  const [channelLoading, setChannelLoading] = useState(false);
  const [dmError, setDmError] = useState<string | null>(null);
  const [channelError, setChannelError] = useState<string | null>(null);

  const otherUsers = useMemo(() => {
    if (!user?.id) return [];
    return INTRANET_SEED_USERS.filter((u) => u.id !== user.id);
  }, [user?.id]);

  useEffect(() => {
    if (otherUsers.length === 1) {
      setDmOtherId(otherUsers[0].id);
    }
  }, [otherUsers]);

  async function submitDm(e: React.FormEvent) {
    e.preventDefault();
    setDmError(null);
    if (!dmOtherId) {
      setDmError("Choose someone to message.");
      return;
    }
    setDmLoading(true);
    try {
      const res = await createDirectConversation({ otherUserId: dmOtherId });
      onCreated(res.id);
    } catch (err) {
      setDmError(err instanceof Error ? err.message : "Could not open DM");
    } finally {
      setDmLoading(false);
    }
  }

  async function submitChannel(e: React.FormEvent) {
    e.preventDefault();
    setChannelError(null);
    const name = channelName.trim();
    if (!name) {
      setChannelError("Enter a channel name.");
      return;
    }
    setChannelLoading(true);
    try {
      const res = await createChannel({ name });
      setChannelName("");
      onCreated(res.id);
    } catch (err) {
      setChannelError(err instanceof Error ? err.message : "Could not create channel");
    } finally {
      setChannelLoading(false);
    }
  }

  const disabled = userLoading || !user;

  return (
    <div className="grid gap-4 md:grid-cols-2">
      <form
        onSubmit={(e) => void submitDm(e)}
        className="rounded-lg border border-gray-200 bg-white p-4 shadow-sm"
      >
        <h3 className="text-sm font-semibold text-gray-900">New direct message</h3>
        <p className="mt-1 text-xs text-gray-500">
          Opens an existing DM with that person or creates one (same id if it already exists).
        </p>
        <div className="mt-3">
          <label htmlFor="dm-other" className="mb-1 block text-xs font-medium text-gray-700">
            Other user (seed list)
          </label>
          <select
            id="dm-other"
            className="w-full rounded-md border border-gray-300 bg-white px-3 py-2 text-sm outline-none focus:border-primary focus:ring-1 focus:ring-gray-400 disabled:bg-gray-50"
            value={dmOtherId}
            onChange={(e) => setDmOtherId(e.target.value)}
            disabled={disabled || dmLoading || otherUsers.length === 0}
            required
          >
            <option value="">Select…</option>
            {otherUsers.map((u) => (
              <option key={u.id} value={u.id}>
                {u.label}
              </option>
            ))}
          </select>
        </div>
        {otherUsers.length === 0 && user && !userLoading ? (
          <p className="mt-2 text-xs text-amber-800">
            No other seed users to message (you are the only dev user in the list).
          </p>
        ) : null}
        {dmError ? (
          <p className="mt-2 text-xs text-red-800" role="alert">
            {dmError}
          </p>
        ) : null}
        <button
          type="submit"
          disabled={disabled || dmLoading || otherUsers.length === 0}
          className="bg-primary hover:bg-primary-dark mt-3 w-full rounded-md px-3 py-2 text-sm font-medium text-primary-foreground disabled:opacity-60"
        >
          {dmLoading ? "Opening…" : "Open DM"}
        </button>
      </form>

      <form
        onSubmit={(e) => void submitChannel(e)}
        className="rounded-lg border border-gray-200 bg-white p-4 shadow-sm"
      >
        <h3 className="text-sm font-semibold text-gray-900">New channel</h3>
        <p className="mt-1 text-xs text-gray-500">Create a channel; you are added as the first member.</p>
        <div className="mt-3">
          <label htmlFor="channel-name" className="mb-1 block text-xs font-medium text-gray-700">
            Channel name
          </label>
          <input
            id="channel-name"
            type="text"
            autoComplete="off"
            maxLength={255}
            placeholder="e.g. my-team"
            className="w-full rounded-md border border-gray-300 px-3 py-2 text-sm outline-none focus:border-primary focus:ring-1 focus:ring-gray-400 disabled:bg-gray-50"
            value={channelName}
            onChange={(e) => setChannelName(e.target.value)}
            disabled={disabled || channelLoading}
          />
        </div>
        {channelError ? (
          <p className="mt-2 text-xs text-red-800" role="alert">
            {channelError}
          </p>
        ) : null}
        <button
          type="submit"
          disabled={disabled || channelLoading}
          className="bg-primary hover:bg-primary-dark mt-3 w-full rounded-md px-3 py-2 text-sm font-medium text-primary-foreground disabled:opacity-60"
        >
          {channelLoading ? "Creating…" : "Create channel"}
        </button>
      </form>
    </div>
  );
}
