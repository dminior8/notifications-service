package pl.dminior8.publisher_notifications.service;

import org.quartz.*;
import org.springframework.stereotype.Service;
import pl.dminior8.publisher_notifications.job.NotificationJob;
import pl.dminior8.publisher_notifications.model.Notification;

@Service
public class QuartzNotificationService {
    private final Scheduler scheduler;

    public QuartzNotificationService(Scheduler scheduler) {
        this.scheduler = scheduler;
    }

    public void scheduleNotification(Notification notification) throws SchedulerException {
        JobDetail jobDetail = JobBuilder.newJob(NotificationJob.class)
                .withIdentity(String.valueOf(notification.getId()), "notifications")
                .usingJobData("notificationId", String.valueOf(notification.getId()))
                .build();

        Trigger trigger = TriggerBuilder.newTrigger()
                .withIdentity(String.valueOf(notification.getId()), "notifications")
                .startAt(java.util.Date.from(notification.getScheduledTime().atZone(java.time.ZoneId.systemDefault()).toInstant()))
                .withSchedule(SimpleScheduleBuilder.simpleSchedule().withMisfireHandlingInstructionFireNow())
                .build();

        scheduler.scheduleJob(jobDetail, trigger);
    }
}

