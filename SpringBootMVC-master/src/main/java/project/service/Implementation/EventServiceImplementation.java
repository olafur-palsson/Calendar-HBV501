package project.service.Implementation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import project.controller.exceptions.UnauthorizedException;
import project.persistence.entities.Event;
import project.persistence.entities.User;
import project.persistence.repositories.EventRepository;
import project.persistence.repositories.UserRepository;
import project.service.EventService;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class EventServiceImplementation implements EventService {

    // Instance Variables
    EventRepository eventRepository;
    UserRepository userRepository;

    // Dependency Injection
    @Autowired
    public EventServiceImplementation(EventRepository eventRepository, UserRepository userRepository) {
        this.eventRepository = eventRepository;
        this.userRepository = userRepository;
    }

    @Override
    public Event save(User user, Event event) {
        event.setUsers(new ArrayList<>());
        event.getUsers().add(user);

        return cleanEvent(eventRepository.save(event));
    }

    @Override
    public void delete(User user, Long id) throws UnauthorizedException {
        Event currentEvent = eventRepository.findOneById(id);

        if (!this.canUpdate(user, currentEvent)) throw new UnauthorizedException("Cannot delete event");
        else {
            eventRepository.delete(currentEvent);
        }
    }

    @Override
    public List<Event> findByDate(User user, Date startDate, Date endDate) {

        List<Event> filtered = new ArrayList<>();

        if (user.getEvents() == null) return filtered;

        for (Event event : user.getEvents()) {
            Date eventStartDate = event.getStartDate();
            if (eventStartDate.after(startDate) && eventStartDate.before(endDate)) {
                filtered.add(event);
            }
        }

        return filtered;
    }

    @Override
    public Event findOne(Long id) {
        return cleanEvent(eventRepository.findOneById(id));
    }

    @Override
    public Event updateEvent(User user, Long id, Event event) throws Exception {

        // Find the event being updated
        Event currentEvent = eventRepository.findOneById(id);

        if (event == null) return null;

        if (!this.canUpdate(user, currentEvent)) throw new Exception("Cannot update event!");

        // Only update available fields.
        if (event.getStartDate() != null) currentEvent.setStartDate(event.getStartDate());
        if (event.getEndDate() != null) currentEvent.setEndDate(event.getEndDate());
        if (event.getTitle() != null) currentEvent.setTitle(event.getTitle());
        if (event.getDescription() != null) currentEvent.setDescription(event.getDescription());

        return cleanEvent(eventRepository.save(currentEvent));
    }

    @Override
    public Event updateUserList(User user, Long id, List<String> usernames) throws Exception {
        // Get event with id
        Event event = eventRepository.findOneById(id);

        if (event == null) return null;

        if (!this.canUpdate(user, event)) throw new Exception("Cannot update event!");

        // Get current userList
        List <User> currentUserList = event.getUsers();


        // Validate each user and add to List
        for (String username : usernames) {
            User currentUser = userRepository.findUserByUsername(username);
            if (currentUser != null && !currentUserList.contains(currentUser)) {
                currentUserList.add(currentUser);
            }
        }



        try {
            // eventRepository removes duplicate users
            event = eventRepository.save(event);
        } catch (Exception e){

        }
        // Clean event object
        return cleanEvent(event);

    }

    private boolean canUpdate(User user, Event event) {
        // Cannot update event not shared with you;
        if (user.getEvents() == null) return false;
        if (!user.getEvents().contains(event)) return false;
        return true;
    }

    // set events for each user as null to prevent infinite recursive
    // definitions
    private Event cleanEvent(Event event) {
        if (event == null) return null;
        if (event.getUsers() == null) return event;
        for (User user: event.getUsers()) {
            user.setToken(null);
            user.setPassword(null);
            user.setEvents(null);
        }
        return event;
    }

    private List<Event> cleanEventList(List<Event> eventList) {
        for (Event event : eventList) {
            event = cleanEvent(event);
        }
        return eventList;
    }
}
