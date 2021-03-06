package project.service.Implementation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import project.persistence.entities.Event;
import project.persistence.entities.User;
import project.persistence.repositories.UserRepository;
import project.service.UserService;

import java.util.List;

@Service
public class UserServiceImplementation implements UserService {

    private UserRepository userRepository;
    private BCryptPasswordEncoder passwordEncoder;


    @Autowired
    public UserServiceImplementation(UserRepository repository) {
        this.userRepository = repository;
        this.passwordEncoder = new BCryptPasswordEncoder(10);
    }

    @Override
    public User save(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return cleanUser(userRepository.save(user));
    }

    @Override
    public void delete(User user) {
        userRepository.delete(user);
    }

    @Override
    public User findOne(Long id) {
        return cleanUser(userRepository.findOneById(id));
    }

    @Override
    public User update(User user) {
        return cleanUser(user);
    }

    @Override
    public User login(User user) {
        User byUsername = userRepository.findUserByUsername(user.getUsername());

        if (byUsername == null) return null;

        if (!passwordEncoder.matches(user.getPassword(), byUsername.getPassword())) return null;

        while (true) {
            try {
                String token = Long.toHexString(Double.doubleToLongBits(Math.random()));

                byUsername.setToken(token);

                User loggedIn = userRepository.save(byUsername);

                loggedIn.setEvents(null);
                loggedIn.setPassword(null);

                return loggedIn;
            } catch (Exception e) {

            }
        }

    }

    @Override
    public List<User> findAll() {
        return cleanUserList(userRepository.findAll());
    }

    private User cleanUser(User user) {
        if (user == null) return null;
        user.setPassword(null);
        user.setToken(null);

        if (user.getEvents() == null) return user;

        for (Event event : user.getEvents()) {
            event.setUsers(null);
        }
        return user;
    }

    @Override
    public User findByToken(String token) {
        String sub = token.substring(7);

        User user = userRepository.findByToken(sub);
        return user;
    }

    private List<User> cleanUserList(List<User> userList) {
        for (User user : userList) {
            user = cleanUser(user);
        }
        return userList;
    }
}
