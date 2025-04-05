package pl.dminior8.publisher_notifications.dto;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.ZoneId;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import pl.dminior8.publisher_notifications.model.EChannel;

@Getter
@Setter
@Builder
public class NotificationDTO implements Serializable {
    private String content;
    private EChannel channel;
    private ZoneId timezone;
    private LocalDateTime scheduledTime;
}
