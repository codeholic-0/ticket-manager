package dev.zen.common.event;

import java.math.BigDecimal;
import java.util.UUID;

public record OrderItemInfo(UUID eventId, UUID seatId, BigDecimal price) {

}
