package pl.dminior8.publisher_notifications.service;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import pl.dminior8.publisher_notifications.configuration.RabbitMQConfig;
import pl.dminior8.publisher_notifications.dto.NotificationDTO;
import pl.dminior8.publisher_notifications.model.EStatus;
import pl.dminior8.publisher_notifications.model.Notification;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Service
public class NotificationService {
    private final RabbitTemplate rabbitTemplate;
    private final ConcurrentMap<String, Notification> notificationStorage = new ConcurrentHashMap<>();

    public NotificationService(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public UUID scheduleNotification(NotificationDTO notificationDTO) {
        Notification notification =
                Notification.builder()
                        .id(UUID.randomUUID())
                        .content(notificationDTO.getContent())
                        .channel(notificationDTO.getChannel())
                        .timezone(notificationDTO.getTimezone())
                        .scheduledTime(notificationDTO.getScheduledTime())
                        .status(EStatus.PENDING)
                        .build();
        notificationStorage.put(String.valueOf(notification.getId()), notification);
        String routingKey = switch (notification.getChannel()) {
            case PUSH -> RabbitMQConfig.PUSH_ROUTING_KEY;
            case EMAIL -> RabbitMQConfig.EMAIL_ROUTING_KEY;
        };

        rabbitTemplate.convertAndSend(RabbitMQConfig.EXCHANGE_NAME, routingKey, notification);
        return notification.getId();
    }

    public Notification getNotification(String id) {
        return notificationStorage.get(id);
    }
}

