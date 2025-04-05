package pl.dminior8.publisher_notifications.controller;

import lombok.RequiredArgsConstructor;
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
    private final RabbitTemplate rabbitTemplate;
    private final ConcurrentMap<String, Notification> notificationStorage = new ConcurrentHashMap<>();
    private final NotificationService notificationService;

    @PostMapping
    public ResponseEntity<String> createNotification(@RequestBody NotificationDTO notificationDTO) {
        UUID scheduledId = notificationService.scheduleNotification(notificationDTO);
        return ResponseEntity.ok("Notification scheduled: " + scheduledId.toString());
    }

    @GetMapping("/{id}")
    public Notification getNotification(@PathVariable String id) {
        return notificationStorage.get(id);
    }
}
