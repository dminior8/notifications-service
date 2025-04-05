package pl.dminior8.publisher_notifications.controller;

import lombok.RequiredArgsConstructor;
import org.quartz.SchedulerException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import pl.dminior8.publisher_notifications.dto.NotificationDTO;
import pl.dminior8.publisher_notifications.model.Notification;
import pl.dminior8.publisher_notifications.service.NotificationService;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;


@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
public class NotificationController {
    private final NotificationService notificationService;

    @PostMapping
    public ResponseEntity<String> createNotification(@RequestBody NotificationDTO notificationDTO) throws SchedulerException {
        UUID scheduledId = notificationService.scheduleNotification(notificationDTO);
        return ResponseEntity.ok("Notification scheduled: " + scheduledId.toString());
    }

    @GetMapping("/{id}")
    public ResponseEntity<String> getNotification(@PathVariable UUID id) {
        return ResponseEntity.ok("Notification scheduled: " + notificationService.getNotification(id).get().toString());
    }
}
