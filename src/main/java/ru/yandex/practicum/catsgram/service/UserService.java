package ru.yandex.practicum.catsgram.service;

import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.catsgram.exception.ConditionsNotMetException;
import ru.yandex.practicum.catsgram.exception.DuplicatedDataException;
import ru.yandex.practicum.catsgram.exception.NotFoundException;
import ru.yandex.practicum.catsgram.model.User;

import java.time.Instant;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class UserService {
    private final Map<Long, User> users = new HashMap<>();

    public Collection<User> findAll() {
        return users.values();
    }

    public User create(@RequestBody User user) {
        // проверяем, что email хотябы заполнен
        if (user.getEmail() == null || user.getEmail().isBlank()) {
            throw new ConditionsNotMetException("Имейл должен быть указан");
        }

        // проверяем, что такой email ещё не используется
        if (getUserByEmail(user.getEmail()).isPresent()) {
            throw new DuplicatedDataException("Этот имейл уже используется");
        }

        // заполняем данные пользователя
        user.setId(getNextId());
        user.setRegistrationDate(Instant.now());
        users.put(user.getId(), user);
        return user;
    }

    public User update(@RequestBody User user) {
        if (user.getId() == null) {
            throw new ConditionsNotMetException("Id должен быть указан");
        }

        if (users.containsKey(user.getId())) {
            User userInDatabase = users.get(user.getId());
            Optional<User> anotherUser = getUserByEmail(user.getEmail());
            if (anotherUser.isPresent() && anotherUser.get().getId() != user.getId()) {
                throw new DuplicatedDataException("Этот имейл уже используется");
            }

            if (user.getEmail() != null && !user.getEmail().isBlank()) {
                userInDatabase.setEmail(user.getEmail());
            }

            if (user.getUsername() != null && !user.getUsername().isBlank()) {
                userInDatabase.setUsername(user.getUsername());
            }

            if (user.getPassword() != null && !user.getPassword().isBlank()) {
                userInDatabase.setPassword(user.getPassword());
            }

            return userInDatabase;
        }
        throw new NotFoundException("Пользователь с id = " + user.getId() + " не найден");
    }

    // вспомогательный метод для генерации идентификатора нового пользователя
    private long getNextId() {
        long currentMaxId = users.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }

    // вспомогательный метод получения пользователя по email
    private Optional<User> getUserByEmail(String email) {
        return users.values()
                .stream()
                .filter(user -> user.getEmail().equals(email))
                .findFirst();
    }

    public Optional<User> findUserById(Long id) {
        return Optional.ofNullable(users.get(id));
    }
}
