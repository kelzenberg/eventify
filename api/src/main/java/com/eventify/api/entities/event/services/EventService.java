package com.eventify.api.entities.event.services;

import com.eventify.api.entities.event.data.Event;
import com.eventify.api.entities.event.data.EventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
public class EventService {

    @Autowired
    private EventRepository eventRepository;

    public List<Event> getAllEvents() {
        return eventRepository.findAll();
    }

    public Event getEventById(UUID id) {
        return eventRepository.findById(id).orElse(null);
    }

    public Event createEvent(String title, String description, Date startedAt) {
        Event.EventBuilder newEvent = Event.builder()
                .title(title)
                .description(description);

        if (startedAt != null) {
            newEvent.startedAt(startedAt);
        }

        return eventRepository.save(newEvent.build());
    }

    public void deleteEvent(UUID id) {
        eventRepository.deleteById(id);
    }
}