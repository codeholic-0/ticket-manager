package dev.zen.inventory.service;

import dev.zen.inventory.domain.Event;
import dev.zen.inventory.domain.Seat;
import dev.zen.inventory.domain.SeatStatus;
import dev.zen.inventory.exception.EventNotFoundException;
import dev.zen.inventory.exception.SeatAlreadyExistsException;
import dev.zen.inventory.repository.EventRepository;
import dev.zen.inventory.repository.SeatRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
public class InventoryService {
    private final EventRepository eventRepository;
    private final SeatRepository seatRepository;

    public InventoryService(EventRepository eventRepository, SeatRepository seatRepository) {
        this.eventRepository = eventRepository;
        this.seatRepository = seatRepository;
    }

    private void validateEventExists(UUID eventId) {
        if (!eventRepository.existsById(eventId)) {
            throw new EventNotFoundException(eventId);
        }
    }

    @Transactional(readOnly = true)
    public List<Event> listAll() {
        return eventRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Event getEvent(UUID id) {
        return eventRepository.findById(id)
                .orElseThrow(() -> new EventNotFoundException(id));
    }

    @Transactional(readOnly = true)
    public List<Seat> getSeatMap(UUID eventId) {
        validateEventExists(eventId);
        return seatRepository.findByEventId(eventId);
    }

    @Transactional(readOnly = true)
    public Seat getSeat(UUID eventId, UUID seatId) {
        Seat seat = seatRepository.findById(seatId)
                .orElseThrow(() -> new NoSuchElementException("Seat not found: " + seatId));
        if (!seat.getEvent().getId().equals(eventId)) {
            throw new NoSuchElementException("Seat " + seatId + " does not belong to event " + eventId);
        }
        return seat;
    }

    @Transactional(readOnly = true)
    public long countAvailable(UUID eventId) {
        validateEventExists(eventId);
        return seatRepository.countByEventIdAndStatus(eventId, SeatStatus.AVAILABLE);
    }

    @Transactional
    public Event createEvent(Event event) {
        return eventRepository.save(event);
    }

    @Transactional
    public List<Seat> createSeats(UUID eventId, int rows, int seatsPerRow) {
        Event event = getEvent(eventId);
        if (!seatRepository.findByEventId(eventId).isEmpty()) {
            throw new SeatAlreadyExistsException(eventId);
        }

        List<Seat> seats = new ArrayList<>();
        for (int r = 0; r < rows && r < 26; r++) {
            String rowLetter = String.valueOf((char) ('A' + r));
            for (int s = 1; s <= seatsPerRow; s++) {
                Seat seat = new Seat();
                seat.setEvent(event);
                seat.setSeatRow(rowLetter);
                seat.setSeatNumber(s);
                seat.setStatus(SeatStatus.AVAILABLE);
                seats.add(seat);
            }
        }
        return seatRepository.saveAll(seats);
    }

}
