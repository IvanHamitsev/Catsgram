package ru.yandex.practicum.catsgram.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.catsgram.exception.NotFoundException;
import ru.yandex.practicum.catsgram.model.User;
import ru.yandex.practicum.catsgram.service.UserService;

import java.util.Collection;

@RestController
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/users")
    public Collection<User> findAll() {
        return userService.findAll();
    }

    @GetMapping("/user/{userId}")
    public User findUserById(@PathVariable long userId) {
        return userService.findUserById(userId)
                .orElseThrow(() -> new NotFoundException("Не найден пользователь с id = " + userId));
    }

    @PostMapping("/user")
    public User create(@RequestBody User user) {
        return userService.create(user);
    }

    @PutMapping("/user")
    public User update(@RequestBody User user) {
        return userService.update(user);
    }
}
