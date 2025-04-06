package pl.dminior8.publisher_notifications.service;

import lombok.RequiredArgsConstructor;
import org.quartz.SchedulerException;
import org.springframework.stereotype.Service;
import pl.dminior8.publisher_notifications.dto.NotificationDTO;
import pl.dminior8.publisher_notifications.model.EStatus;
import pl.dminior8.publisher_notifications.model.Notification;
import pl.dminior8.publisher_notifications.repository.NotificationRepository;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Optional;
import java.util.UUID;

import static pl.dminior8.publisher_notifications.model.EStatus.IN_PROGRESS;

@Service
@RequiredArgsConstructor
public class NotificationService {
    private final NotificationRepository notificationRepository;
    private final QuartzNotificationService quartzNotificationService;


    public UUID scheduleNotification(NotificationDTO notificationDTO) throws SchedulerException {
        Notification notification =
                Notification.builder()
                        .id(UUID.randomUUID())
                        .content(notificationDTO.getContent())
                        .channel(notificationDTO.getChannel())
                        .priority(notificationDTO.getPriority())
                        .timezone(notificationDTO.getTimezone())
                        .scheduledTime(notificationDTO.getScheduledTime())
                        .status(EStatus.IN_PROGRESS)
                        .retryCount(0)
                        .build();
        notificationRepository.save(notification);

        quartzNotificationService.scheduleNotification(notification, 0);

        return notification.getId();
    }

    public boolean forceSendNotification(UUID notificationId) throws SchedulerException {
        Notification notification = notificationRepository.findById(notificationId).orElse(null);
        if (notification != null && notification.getStatus() == IN_PROGRESS) {
            notification.setScheduledTime(LocalDateTime.ofInstant(Instant.now(), ZoneId.systemDefault()));
            notificationRepository.save(notification);
            quartzNotificationService.scheduleNotification(notification, 0);
            return true;
        }
        return false;

    }

    public void cancelNotification(UUID notificationId) throws SchedulerException {
        notificationRepository.deleteById(notificationId);
        quartzNotificationService.cancelNotification(notificationId);
    }

    public Optional<Notification> getNotification(UUID id) {
        return notificationRepository.findById(id);
    }

    public void updateNotification(Notification notification) {
        notificationRepository.save(notification);
    }

}

