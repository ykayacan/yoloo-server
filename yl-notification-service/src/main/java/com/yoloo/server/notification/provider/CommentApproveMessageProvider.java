package com.yoloo.server.notification.provider;

import com.google.firebase.database.utilities.Pair;
import com.google.firebase.messaging.Message;
import com.yoloo.server.common.id.generator.LongIdGenerator;
import com.yoloo.server.notification.entity.Notification;
import com.yoloo.server.notification.payload.NotificationBody;
import com.yoloo.server.notification.payload.NotificationPayload;

public class CommentApproveMessageProvider extends MessageProvider {

  private final LongIdGenerator idGenerator;

  public CommentApproveMessageProvider(LongIdGenerator idGenerator) {
    this.idGenerator = idGenerator;
  }

  @Override
  public Pair<Message, Notification> check(NotificationPayload payload) {
    if (payload.getType().equals("TYPE_APPROVE")) {
      NotificationBody.Approve body = (NotificationBody.Approve) payload.getBody();

      Message message =
          Message.builder()
              .setToken(body.getToken())
              .putData("FCM_KEY_TYPE", "TYPE_APPROVE")
              .putData("FCM_KEY_POST_ID", body.getPostId())
              .putData("FCM_KEY_COMMENT_CONTENT", body.getTrimmedCommentContent())
              .build();

      Notification notification =
          Notification.newBuilder()
              .id(idGenerator.generateId())
              .type(Notification.EntityType.COMMENT_APPROVED)
              .actor(
                  Notification.Actor.newBuilder().id(Long.parseLong(body.getPostOwnerId())).build())
              .receiver(
                  Notification.Receiver.newBuilder()
                      .id(Long.parseLong(body.getCommentOwnerId()))
                      .build())
              .build();

      return new Pair<>(message, notification);
    }

    return checkNext(payload);
  }
}