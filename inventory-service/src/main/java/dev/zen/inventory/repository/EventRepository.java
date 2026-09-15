package dev.zen.inventory.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import dev.zen.inventory.domain.Event;

public interface EventRepository extends JpaRepository<Event, UUID> {

}
