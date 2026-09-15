package dev.zen.inventory.exception;

import java.util.UUID;

public class SeatAlreadyExistsException extends RuntimeException{
    public SeatAlreadyExistsException(UUID eventId) {
        super("Seats already exist for event: " + eventId);
    }
}
