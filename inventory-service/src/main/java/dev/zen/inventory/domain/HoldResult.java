package dev.zen.inventory.domain;

import java.util.List;
import java.util.UUID;

public sealed interface HoldResult {
    record Success(List<UUID> heldSeats) implements HoldResult {
        public Success {
            heldSeats = List.copyOf(heldSeats);
        }
    }

    record Blocked(UUID seatId) implements HoldResult {
    }

    record BadRequest(String reason) implements HoldResult {
    }
}
