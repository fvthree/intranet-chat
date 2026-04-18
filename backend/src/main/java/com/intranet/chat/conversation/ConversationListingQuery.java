package com.intranet.chat.conversation;

import io.r2dbc.spi.Row;
import io.r2dbc.spi.RowMetadata;
import java.time.Instant;
import java.util.UUID;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

@Component
public class ConversationListingQuery {

  private static final String LIST_FOR_USER =
      """
      SELECT
        c.id AS cid,
        c.type AS ctype,
        c.name AS cname,
        c.created_by AS created_by,
        c.created_at AS created_at,
        c.updated_at AS updated_at,
        lm.id AS last_msg_id,
        LEFT(lm.content, 200) AS last_msg_content,
        lm.sender_id AS last_msg_sender_id,
        lm.created_at AS last_msg_created_at,
        (
          SELECT COUNT(*)::bigint
          FROM messages m
          WHERE m.conversation_id = c.id
            AND NOT m.deleted
            AND m.sender_id <> :userId
            AND m.created_at >= cp.joined_at
            AND (
              cp.last_read_message_id IS NULL
              OR NOT EXISTS (
                SELECT 1 FROM messages mr
                WHERE mr.id = cp.last_read_message_id
                  AND mr.conversation_id = c.id
                  AND NOT mr.deleted
              )
              OR (m.created_at, m.id) > (
                SELECT m2.created_at, m2.id
                FROM messages m2
                WHERE m2.id = cp.last_read_message_id
                  AND m2.conversation_id = c.id
                  AND NOT m2.deleted
              )
            )
        ) AS unread_count
      FROM conversations c
      INNER JOIN conversation_participants cp
        ON cp.conversation_id = c.id AND cp.user_id = :userId
      LEFT JOIN LATERAL (
        SELECT m.id, m.content, m.sender_id, m.created_at
        FROM messages m
        WHERE m.conversation_id = c.id AND NOT m.deleted
        ORDER BY m.created_at DESC
        LIMIT 1
      ) AS lm ON true
      ORDER BY c.updated_at DESC, c.id ASC
      """;

  private final DatabaseClient databaseClient;

  public ConversationListingQuery(DatabaseClient databaseClient) {
    this.databaseClient = databaseClient;
  }

  public Flux<ConversationListItemResponse> listForUser(UUID userId) {
    return databaseClient
        .sql(LIST_FOR_USER)
        .bind("userId", userId)
        .map(this::mapRow)
        .all();
  }

  private ConversationListItemResponse mapRow(Row row, RowMetadata metadata) {
    UUID lastMsgId = row.get("last_msg_id", UUID.class);
    LastMessagePreview last =
        lastMsgId == null
            ? null
            : new LastMessagePreview(
                lastMsgId,
                row.get("last_msg_content", String.class),
                row.get("last_msg_sender_id", UUID.class),
                row.get("last_msg_created_at", Instant.class));
    Object unreadObj = row.get("unread_count");
    long unread =
        unreadObj instanceof Number ? ((Number) unreadObj).longValue() : 0L;
    return new ConversationListItemResponse(
        row.get("cid", UUID.class),
        row.get("ctype", String.class),
        row.get("cname", String.class),
        row.get("created_by", UUID.class),
        row.get("created_at", Instant.class),
        row.get("updated_at", Instant.class),
        unread,
        last);
  }
}
