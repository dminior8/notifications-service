package pl.dminior8.publisher_notifications.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.springframework.stereotype.Service;
import pl.dminior8.publisher_notifications.job.NotificationJob;
import pl.dminior8.publisher_notifications.model.Notification;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.UUID;

import static pl.dminior8.publisher_notifications.model.EPriority.HIGH;

@Service
@Slf4j
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
                                ? adjustToAllowedHours(notification)
                                : new Date(System.currentTimeMillis() + retryDelayMillis)
                )
                .withPriority(notification.getPriority() == HIGH ? 10 : 5)
                .withSchedule(SimpleScheduleBuilder.simpleSchedule().withMisfireHandlingInstructionFireNow())
                .build();

        scheduler.scheduleJob(jobDetail, trigger);
    }

    public void cancelNotification(UUID notificationId) throws SchedulerException {
        JobKey jobKey = new JobKey(notificationId.toString(), "notifications");
        if (scheduler.checkExists(jobKey)) {
            scheduler.deleteJob(jobKey);
            log.info("Notification {} canceled", notificationId);
        }else{
            log.info("Notification {} not found", notificationId);
        }
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

    public Date adjustToAllowedHours(Notification notification) {
        ZoneId zoneId = notification.getTimezone();
        LocalDateTime scheduled = notification.getScheduledTime();


        LocalTime time = scheduled.toLocalTime();
        LocalTime start = LocalTime.of(8, 0);
        LocalTime end = LocalTime.of(22, 0);

        if (time.isBefore(start)) {
            scheduled = LocalDateTime.of(scheduled.toLocalDate(), start);
        } else if (time.isAfter(end)) {
            scheduled = LocalDateTime.of(scheduled.toLocalDate().plusDays(1), start);
        }

        return Date.from(scheduled.atZone(zoneId).toInstant());
    }



}

