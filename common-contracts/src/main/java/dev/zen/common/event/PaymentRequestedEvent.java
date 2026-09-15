package dev.zen.common.event;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record PaymentRequestedEvent(
        UUID paymentId,
        UUID orderId,
        BigDecimal amount,
        Instant requestedAt) {

}
