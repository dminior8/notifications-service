package pl.dminior8.publisher_notifications.service

import org.quartz.Scheduler
import pl.dminior8.publisher_notifications.model.Notification
import spock.lang.Specification

import java.time.*

class QuartzNotificationServiceTest extends Specification {

    def service = new QuartzNotificationService(Mock(Scheduler) as Scheduler)

    def "should adjust notification time to next allowed hour if scheduled at night"() {
        given:
        def notification = Mock(Notification)
        notification.getTimezone() >> ZoneId.of("Europe/Warsaw")
        notification.getScheduledTime() >> LocalDateTime.of(2025, 4, 6, scheduledHour, 0)

        when:
        Date adjustedDate = service.adjustToAllowedHours(notification)
        def adjustedLocalTime = Instant.ofEpochMilli(adjustedDate.time)
                .atZone(ZoneId.of("Europe/Warsaw"))
                .toLocalDateTime()

        then:
        adjustedLocalTime.getHour() == expectedHour
        adjustedLocalTime.toLocalDate() == expectedDate

        where:
        scheduledHour || expectedHour || expectedDate
        3             || 8            || LocalDate.of(2025, 4, 6)   // rano -> 8:00 tego samego dnia
        23            || 8            || LocalDate.of(2025, 4, 7)   // wieczór -> 8:00 następnego dnia
        9             || 9            || LocalDate.of(2025, 4, 6)   // w normie, bez zmiany
        21            || 21           || LocalDate.of(2025, 4, 6)   // w normie, bez zmiany
    }
}
