package com.intranet.chat.conversation;

import java.util.UUID;

public final class DirectPairKey {

  private DirectPairKey() {}

  /** Stable key for a pair of user ids (order-independent). */
  public static String of(UUID a, UUID b) {
    String sa = a.toString();
    String sb = b.toString();
    return sa.compareTo(sb) <= 0 ? sa + "|" + sb : sb + "|" + sa;
  }
}
