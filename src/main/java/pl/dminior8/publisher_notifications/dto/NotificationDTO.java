package pl.dminior8.publisher_notifications.dto;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.ZoneId;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import pl.dminior8.publisher_notifications.model.EChannel;
import pl.dminior8.publisher_notifications.model.EPriority;

@Getter
@Setter
@Builder
public class NotificationDTO {
    private String content;
    private EChannel channel;
    private EPriority priority;
    private ZoneId timezone;
    private LocalDateTime scheduledTime;
}
