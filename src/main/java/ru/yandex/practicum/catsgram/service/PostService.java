package ru.yandex.practicum.catsgram.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.catsgram.exception.ConditionsNotMetException;
import ru.yandex.practicum.catsgram.exception.NotFoundException;
import ru.yandex.practicum.catsgram.model.Post;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

// Указываем, что класс PostService - является бином и его
// нужно добавить в контекст приложения
@Service
public class PostService {
    private final Map<Long, Post> posts = new HashMap<>();

    private final UserService userService;

    public PostService(UserService userService) {
        this.userService = userService;
    }

    public List<Post> findAll(int from, int size ,String sort) {
        // если ограничение не задано (==0)
        if (size < 1) {
            size = posts.size();
        }
        // если сортировка не задана
        if (SortOrder.from(sort) == null) {
            return posts.values().stream().skip(from).limit(size).collect(Collectors.toList());
        } else {
            return posts.values().stream().sorted((p0, p1) -> {
                if (SortOrder.from(sort) == SortOrder.ASCENDING) {
                    //прямой порядок сортировки
                    return p0.getPostDate().compareTo(p1.getPostDate());
                } else {
                    //обратный порядок сортировки
                    return p1.getPostDate().compareTo(p0.getPostDate());
                }
            }).skip(from).limit(size).collect(Collectors.toList());
        }
    }

    public Optional<Post> findPostById(long postId) {
        return Optional.ofNullable(posts.get(postId));
    }

    public Post create(Post post) {
        if (post.getDescription() == null || post.getDescription().isBlank()) {
            throw new ConditionsNotMetException("Описание не может быть пустым");
        }

        if (userService.findUserById(post.getAuthorId()).isEmpty()) {
            throw new ConditionsNotMetException("«Автор с id = " + post.getAuthorId() + " не найден»");
        }

        post.setId(getNextId());
        post.setPostDate(Instant.now());
        posts.put(post.getId(), post);
        return post;
    }

    public Post update(Post newPost) {
        if (newPost.getId() == null) {
            throw new ConditionsNotMetException("Id должен быть указан");
        }
        if (posts.containsKey(newPost.getId())) {
            Post oldPost = posts.get(newPost.getId());
            if (newPost.getDescription() == null || newPost.getDescription().isBlank()) {
                throw new ConditionsNotMetException("Описание не может быть пустым");
            }
            oldPost.setDescription(newPost.getDescription());
            return oldPost;
        }
        throw new NotFoundException("Пост с id = " + newPost.getId() + " не найден");
    }

    private long getNextId() {
        long currentMaxId = posts.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}