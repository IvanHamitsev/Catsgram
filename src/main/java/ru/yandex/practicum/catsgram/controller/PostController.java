package ru.yandex.practicum.catsgram.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.catsgram.exception.NotFoundException;
import ru.yandex.practicum.catsgram.model.Post;
import ru.yandex.practicum.catsgram.service.PostService;

import java.util.Collection;
import java.util.List;

@RestController
public class PostController {
    private final PostService postService;

    @Autowired
    public PostController(PostService postService) {
        this.postService = postService;
    }

    @GetMapping("/posts")
    public List<Post> findAll(@RequestParam(required = false, defaultValue = "0") int from,
                              @RequestParam(required = false, defaultValue = "0") int size,
                              @RequestParam(required = false, defaultValue = "no") String sort) {
        return postService.findAll(from, size, sort);
    }

    @GetMapping("/post/{postId}")
    public Post findPost(@PathVariable long postId) {
        return postService.findPostById(postId)
                .orElseThrow(() -> new NotFoundException("Не найден пост с id = " + postId));
    }

    @PostMapping("/post")
    public Post create(@RequestBody Post post) {
        return postService.create(post);
    }

    @PutMapping("/post")
    public Post update(@RequestBody Post newPost) {
        return postService.update(newPost);
    }
}