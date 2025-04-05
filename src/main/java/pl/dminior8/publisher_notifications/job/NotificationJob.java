package pl.dminior8.publisher_notifications.job;

import lombok.extern.slf4j.Slf4j;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;
import pl.dminior8.publisher_notifications.configuration.RabbitMQConfig;
import pl.dminior8.publisher_notifications.model.EChannel;
import pl.dminior8.publisher_notifications.model.Notification;
import pl.dminior8.publisher_notifications.service.NotificationService;

@Component
@Slf4j
public class NotificationJob implements Job {
    private final RabbitTemplate rabbitTemplate;
    private final NotificationService notificationService;

    public NotificationJob(RabbitTemplate rabbitTemplate, NotificationService notificationService) {
        this.rabbitTemplate = rabbitTemplate;
        this.notificationService = notificationService;
    }

    @Override
    public void execute(JobExecutionContext context) {
        String notificationId = context.getJobDetail().getJobDataMap().getString("notificationId");
        Notification notification = notificationService.getNotification(notificationId);

        if (notification != null && notification.getChannel() != null) {
            if(notification.getChannel() == EChannel.EMAIL){
                rabbitTemplate.convertAndSend(RabbitMQConfig.EXCHANGE_NAME, RabbitMQConfig.EMAIL_ROUTING_KEY, notification);
            }else {
                rabbitTemplate.convertAndSend(RabbitMQConfig.EXCHANGE_NAME, RabbitMQConfig.PUSH_ROUTING_KEY, notification);
            }
            log.info("Zaplanowane powiadomienie wysłane: " + notification.getId());
        } else {
            log.info("Powiadomienie nie znalezione: " + notificationId);
        }
    }
}

