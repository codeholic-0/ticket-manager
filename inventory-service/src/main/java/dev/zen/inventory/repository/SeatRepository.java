package dev.zen.inventory.repository;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import dev.zen.inventory.domain.Seat;
import dev.zen.inventory.domain.SeatStatus;

public interface SeatRepository extends JpaRepository<Seat, UUID> {
    List<Seat> findByEventIdAndIdIn(UUID eventId, Collection<UUID> ids);

    List<Seat> findByEventIdAndStatus(UUID eventId, SeatStatus status);

    long countByEventIdAndStatus(UUID eventId, SeatStatus status);
}
