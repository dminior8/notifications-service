package pl.dminior8.publisher_notifications.service;

import lombok.RequiredArgsConstructor;
import org.quartz.*;
import org.springframework.stereotype.Service;
import pl.dminior8.publisher_notifications.job.NotificationJob;
import pl.dminior8.publisher_notifications.model.EPriority;
import pl.dminior8.publisher_notifications.model.Notification;

import java.util.Date;

import static pl.dminior8.publisher_notifications.model.EPriority.HIGH;

@Service
@RequiredArgsConstructor
public class QuartzNotificationService {
    private final Scheduler scheduler;

    public void scheduleNotification(Notification notification, long retryDelayMillis) throws SchedulerException {
        scheduleDelete(notification);

        JobDetail jobDetail = JobBuilder.newJob(NotificationJob.class)
                .withIdentity(String.valueOf(notification.getId()), "notifications")
                .usingJobData("notificationId", String.valueOf(notification.getId()))
                .build();


        Trigger trigger = TriggerBuilder.newTrigger()
                .withIdentity(String.valueOf(notification.getId()), "notifications")
                .startAt(
                        retryDelayMillis == 0
                                ? java.util.Date.from(notification.getScheduledTime().atZone(java.time.ZoneId.systemDefault()).toInstant())
                                : new Date(System.currentTimeMillis() + retryDelayMillis)
                )
                .withSchedule(SimpleScheduleBuilder.simpleSchedule().withMisfireHandlingInstructionFireNow())
                .build();

        scheduler.scheduleJob(jobDetail, trigger);
    }

    public void scheduleDelete(Notification notification) {
        String jobId = String.valueOf(notification.getId());
        try {
            if (scheduler.checkExists(new JobKey(jobId, "notifications"))) {
                scheduler.deleteJob(new JobKey(jobId, "notifications"));
            }
        } catch (SchedulerException e) {
            throw new RuntimeException(e);
        }
    }

}

