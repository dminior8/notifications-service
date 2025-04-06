package pl.dminior8.publisher_notifications.job;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.SchedulerException;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Component;
import pl.dminior8.publisher_notifications.configuration.RabbitMQConfig;
import pl.dminior8.publisher_notifications.model.EChannel;
import pl.dminior8.publisher_notifications.model.EStatus;
import pl.dminior8.publisher_notifications.model.Notification;
import pl.dminior8.publisher_notifications.service.NotificationService;
import pl.dminior8.publisher_notifications.service.QuartzNotificationService;

import java.util.UUID;

import static pl.dminior8.publisher_notifications.model.EPriority.HIGH;
import static pl.dminior8.publisher_notifications.model.EStatus.*;

@Component
@Slf4j
@RequiredArgsConstructor
public class NotificationJob implements Job {
    private final RabbitTemplate rabbitTemplate;
    private final NotificationService notificationService;
    private final QuartzNotificationService quartzNotificationService;

    @Override
    public void execute(JobExecutionContext context) {
        UUID notificationId = UUID.fromString(context.getJobDetail().getJobDataMap().getString("notificationId"));

        notificationService.getNotification(notificationId).ifPresent(notification -> {
            if (notification.getStatus() == EStatus.IN_PROGRESS && notification.getRetryCount() < 3) {
                String queue = notification.getChannel() == EChannel.PUSH
                        ? RabbitMQConfig.PUSH_ROUTING_KEY
                        : RabbitMQConfig.EMAIL_ROUTING_KEY;

                try {
                    Notification finalNotification = notification;
                    MessagePostProcessor processor = message -> {
                        message.getMessageProperties().setPriority(finalNotification.getPriority() == HIGH ? 10 : 5);
                        return message;
                    };
                    rabbitTemplate.convertAndSend(
                            RabbitMQConfig.EXCHANGE_NAME,
                            queue,
                            notification,
                            processor
                    );
                    log.info("\tSENDING | Notification with ID {} successfully sent to queue.", notificationId);
                } catch (DataAccessException e) {
                    log.error("\tSENDING | Failed to send notification with ID {}. Retrying...", notificationId);
                } finally {
                    quartzNotificationService.scheduleDelete(notification);
                }

                notification = notificationService.getNotification(notificationId).orElse(null);
                if(notification != null && notification.getStatus() == EStatus.IN_PROGRESS) {
                    scheduleRetry(notification);
                }

            }else if(notification.getStatus() == DELIVERED) {
                log.info("\tDELIVERED | Notification with ID {} successfully delivered to queue.", notificationId);

            }else {
                log.warn("FAILED | Notification with ID {} exceeded retry limit.", notificationId);
                notification.setStatus(EStatus.FAILED);
                notificationService.updateNotification(notification);
            }

            if(notification != null && (notification.getStatus() == DELIVERED || notification.getStatus() == FAILED)) {
                quartzNotificationService.scheduleDelete(notification);
            }
        });
    }

    private void scheduleRetry(Notification notification) {
        notification.setRetryCount(notification.getRetryCount() + 1);
        notification.setStatus(EStatus.IN_PROGRESS);
        notificationService.updateNotification(notification);

        long retryDelayMillis = 3000; // Retry after 3 seconds

        try {
            quartzNotificationService.scheduleNotification(notification, retryDelayMillis);
        } catch (SchedulerException e) {
            log.error("Error scheduling retry for notification with ID {}", notification.getId(), e);
        }
    }
}

