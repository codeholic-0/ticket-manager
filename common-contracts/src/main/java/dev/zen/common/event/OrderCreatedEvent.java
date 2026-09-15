package dev.zen.common.event;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record OrderCreatedEvent(
        UUID orderId,
        UUID reservationId,
        UUID customerId,
        List<OrderItemInfo> items,
        BigDecimal total,
        Instant createdAt) {

}
