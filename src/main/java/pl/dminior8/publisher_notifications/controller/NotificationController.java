package pl.dminior8.publisher_notifications.controller;

import lombok.RequiredArgsConstructor;
import org.quartz.SchedulerException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.dminior8.publisher_notifications.dto.NotificationDTO;
import pl.dminior8.publisher_notifications.service.NotificationService;

import java.util.UUID;


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
        return ResponseEntity.ok("Notification scheduled: " + (
                notificationService.getNotification(id).isPresent() ?
                        notificationService.getNotification(id).get().getId() : "[UNKNOWN]"
        ));
    }

    @PostMapping("/{id}/force-send")
    public ResponseEntity<String> forceSendNotification(@PathVariable UUID id) throws SchedulerException {
        if(notificationService.forceSendNotification(id)) {
            return ResponseEntity.ok("Notification scheduled to force send");
        }else{
            return ResponseEntity.status(404).body("Notification not found: " + id);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> cancelNotification(@PathVariable UUID id) throws SchedulerException {
        notificationService.cancelNotification(id);
        return ResponseEntity.ok("Notification " + id + " canceled.");
    }
}
