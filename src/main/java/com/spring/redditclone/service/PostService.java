package com.spring.redditclone.service;

import com.spring.redditclone.dto.PostRequest;
import com.spring.redditclone.dto.PostResponse;
import com.spring.redditclone.exceptions.RedditCloneException;
import com.spring.redditclone.mapper.PostMapper;
import com.spring.redditclone.model.Post;
import com.spring.redditclone.model.Subreddit;
import com.spring.redditclone.model.User;
import com.spring.redditclone.repository.PostRepository;
import com.spring.redditclone.repository.SubredditRepository;
import com.spring.redditclone.repository.UserRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
public class PostService {
    private final SubredditRepository subredditRepository;
    private final AuthService authService;
    private final PostRepository postRepository;
    private final PostMapper postMapper;
    private final UserRepository userRepository;

    @Transactional
    public PostResponse save(PostRequest postRequest) {
        Subreddit subreddit = subredditRepository.findByName(postRequest.getSubRedditName())
                .orElseThrow(() -> new RedditCloneException(postRequest.getSubRedditName() + " : Subreddit not found"));
        User currentUser = authService.getCurrentUser();
        Post post = postMapper.map(postRequest, subreddit, currentUser);
        return postMapper.mapToDto(postRepository.save(post));
    }

    @Transactional(readOnly = true)
    public List<PostResponse> getAll() {
        return postRepository.findAll()
                .stream()
                .map(postMapper::mapToDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public PostResponse get(long id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new RedditCloneException("Post not found with id : " + id));
        return postMapper.mapToDto(post);
    }

    @Transactional(readOnly = true)
    public List<PostResponse> getPostsBySubreddit(long id) {
        Subreddit subreddit = subredditRepository.findById(id)
                .orElseThrow(() -> new RedditCloneException("Subreddit not found with id : " + id));
        return postRepository.findAllBySubreddit(subreddit)
                .stream()
                .map(postMapper::mapToDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<PostResponse> getPostsByUsername(String name) {
        User user = userRepository.findByUsername(name).
                orElseThrow(() -> new RedditCloneException("User not found with name - " + name));
        return postRepository.findAllByUser(user)
                .stream()
                .map(postMapper::mapToDto)
                .collect(Collectors.toList());
    }
}
