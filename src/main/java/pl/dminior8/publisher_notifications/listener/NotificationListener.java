package pl.dminior8.publisher_notifications.listener;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import pl.dminior8.publisher_notifications.configuration.RabbitMQConfig;
import pl.dminior8.publisher_notifications.model.EStatus;
import pl.dminior8.publisher_notifications.model.Notification;

import java.util.Random;

@Slf4j
@Component
public class NotificationListener {

    private final Random random = new Random();

    @RabbitListener(queues = RabbitMQConfig.PUSH_QUEUE)
    public void receivePushNotification(Notification notification) {
        log.info("➡️ PUSH | Receiving notification:\n{}", formatNotification(notification));

        if (random.nextBoolean()) {
            notification.setStatus(EStatus.DELIVERED);
            log.info("PUSH | Notification delivered successfully: {}", notification.getId());
        } else {
            notification.setStatus(EStatus.FAILED);
            log.warn("PUSH | Notification delivery failed after retries: {}", notification.getId());
        }
    }

    @RabbitListener(queues = RabbitMQConfig.EMAIL_QUEUE)
    public void receiveEmailNotification(Notification notification) {
        log.info("➡️ EMAIL | Receiving notification:\n{}", formatNotification(notification));

        if (random.nextBoolean()) {
            notification.setStatus(EStatus.DELIVERED);
            log.info("EMAIL | Notification delivered successfully: {}", notification.getId());
        } else {
            notification.setStatus(EStatus.FAILED);
            log.warn("EMAIL | Notification delivery failed after retries: {}", notification.getId());
        }
    }

    private String formatNotification(Notification n) {
        return String.format("""
                ID: %s
                Content: %s
                Channel: %s
                Scheduled Time: %s
                Timezone: %s
                Status: %s
                """,
                n.getId(),
                n.getContent(),
                n.getChannel(),
                n.getScheduledTime(),
                n.getTimezone(),
                n.getStatus()
        );
    }
}
