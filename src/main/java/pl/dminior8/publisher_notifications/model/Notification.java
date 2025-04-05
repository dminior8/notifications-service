package pl.dminior8.publisher_notifications.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.UUID;

import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "notifications")
public class Notification {
    private UUID id;
    private String content;
    private EChannel channel;
    private EStatus status;
    private EPriority priority;
    private ZoneId timezone;
    private LocalDateTime scheduledTime;
    private int retryCount;

    @Override
    public String toString() {
        return "\nid=" + id
                + ", \ncontent=" + content
                + ", \nchannel=" + channel
                + ", \nstatus=" + status
                + ", \npriority=" + priority
                + ", \ntimezone=" + timezone
                + ", \nscheduledTime=" + scheduledTime
                + ", \nretryCount=" + retryCount;
    }
}

