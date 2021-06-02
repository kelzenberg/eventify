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

    public List<Event> getAll() {
        return eventRepository.findAll();
    }

    public Event getReferenceById(UUID id) {
        return eventRepository.getOne(id);
    }

    public Event getById(UUID id) {
        return eventRepository.findById(id).orElse(null);
    }

    public Event create(String title, String description, Date startedAt) {
        Event.EventBuilder newEntity = Event.builder()
                .title(title)
                .description(description);

        if (startedAt != null) {
            newEntity.startedAt(startedAt);
        }

        return eventRepository.save(newEntity.build());
    }

    public void deleteById(UUID id) {
        eventRepository.deleteById(id);
    }
}
