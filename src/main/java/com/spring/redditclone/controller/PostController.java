package com.spring.redditclone.controller;

import com.spring.redditclone.dto.PostRequest;
import com.spring.redditclone.dto.PostResponse;
import com.spring.redditclone.service.PostService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/posts")
@AllArgsConstructor
public class PostController {
    private final PostService postService;

    @PostMapping
    public ResponseEntity<PostResponse> createPost(@RequestBody PostRequest postRequest) {
        return new ResponseEntity<>(postService.save(postRequest), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<PostResponse>> getAllPosts() {
        return new ResponseEntity<>(postService.getAll(), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PostResponse> getPost(@PathVariable long id) {
        return new ResponseEntity<>(postService.get(id), HttpStatus.OK);
    }

    @GetMapping("/by-subreddit/{id}")
    public ResponseEntity<List<PostResponse>> getPostsBySubreddit(@PathVariable long id) {
        return new ResponseEntity<>(postService.getPostsBySubreddit(id), HttpStatus.OK);
    }

    @GetMapping("/by-user/{name}")
    public ResponseEntity<List<PostResponse>> getPostsByUsername(@PathVariable String name) {
        return new ResponseEntity<>(postService.getPostsByUsername(name), HttpStatus.OK);
    }
}
