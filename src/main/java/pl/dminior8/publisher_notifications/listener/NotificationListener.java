package pl.dminior8.publisher_notifications.listener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import pl.dminior8.publisher_notifications.configuration.RabbitMQConfig;
import pl.dminior8.publisher_notifications.model.EStatus;
import pl.dminior8.publisher_notifications.model.Notification;
import pl.dminior8.publisher_notifications.service.NotificationService;

import java.util.Random;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationListener {
    private final NotificationService notificationService;
    private final Random random = new Random();

    @RabbitListener(queues = RabbitMQConfig.PUSH_QUEUE)
    public void receivePushNotification(Notification notification) {
        if (random.nextBoolean()) {
            notification.setStatus(EStatus.DELIVERED);
            notificationService.updateNotification(notification);
            log.info("SUCCES | Push notification delivered successfully: {}", formatNotification(notification));
        } else {
            log.warn("FAILED | Push notification delivery faileds: {}", notification.getId());
        }
    }

    @RabbitListener(queues = RabbitMQConfig.EMAIL_QUEUE)
    public void receiveEmailNotification(Notification notification) {
        if (random.nextBoolean()) {
            notification.setStatus(EStatus.DELIVERED);
            notificationService.updateNotification(notification);
            log.info("SUCCES | Email notification delivered successfully: {}", formatNotification(notification));
        } else {
            log.warn("FAILED | Email notification delivery failed: {}", notification.getId());
        }
    }

    private String formatNotification(Notification n) {
        return String.format("""
                ID: %s
                Content: %s
                Channel: %s
                Priority: %s
                Scheduled Time: %s
                Timezone: %s
                Status: %s
                """,
                n.getId(),
                n.getContent(),
                n.getChannel(),
                n.getPriority(),
                n.getScheduledTime(),
                n.getTimezone(),
                n.getStatus()
        );
    }
}
