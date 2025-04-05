package pl.dminior8.publisher_notifications.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.UUID;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
public class Notification implements Serializable {
    private UUID id;
    private String content;
    private EChannel channel;
    private EStatus status;
    private ZoneId timezone;
    private LocalDateTime scheduledTime;
}

