package dev.zen.common.event;

import java.util.List;
import java.util.UUID;

public record SeatReleasedEvent(
        UUID orderId,
        UUID eventId,
        List<UUID> seatIds,
        ReleaseReason reason) {
}
