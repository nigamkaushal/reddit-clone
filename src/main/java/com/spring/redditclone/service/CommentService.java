package com.spring.redditclone.service;

import com.spring.redditclone.dto.CommentDto;
import com.spring.redditclone.exceptions.RedditCloneException;
import com.spring.redditclone.mapper.CommentMapper;
import com.spring.redditclone.model.Comment;
import com.spring.redditclone.model.NotificationEmail;
import com.spring.redditclone.model.Post;
import com.spring.redditclone.model.User;
import com.spring.redditclone.repository.CommentRepository;
import com.spring.redditclone.repository.PostRepository;
import com.spring.redditclone.repository.UserRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
public class CommentService {
    private static final String POST_URL = " ";
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final AuthService authService;
    private final CommentMapper commentMapper;
    private final CommentRepository commentRepository;
    private final MailService mailService;
    private final MailContentBuilder mailContentBuilder;

    public void save(CommentDto commentDto) {
        Post post = postRepository.findById(commentDto.getPostId())
                .orElseThrow(() -> new RedditCloneException("Post Not found with id : " + commentDto.getPostId()));
        Comment comment = commentMapper.map(commentDto, post, authService.getCurrentUser());
        commentRepository.save(comment);
        String message = mailContentBuilder.build(post.getUser().getUsername() + " posted a comment on your post. " + POST_URL);
        sendCommentNotification(message, post.getUser());
    }

    private void sendCommentNotification(String message, User user) {
        mailService.sendMail(new NotificationEmail(user.getUsername() + " Commented on your post",
                user.getEmail(), message));
    }

    public List<CommentDto> getAllCommentsForPost(long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RedditCloneException("Post Not found with id : " + postId));
        return commentRepository.findAllByPost(post)
                .stream()
                .map(commentMapper::mapToDto)
                .collect(Collectors.toList());
    }

    public List<CommentDto> getAllCommentsForUser(String userName) {
        User user = userRepository.findByUsername(userName)
                .orElseThrow(() -> new UsernameNotFoundException(userName));
        return commentRepository.findAllByUser(user)
                .stream()
                .map(commentMapper::mapToDto)
                .collect(Collectors.toList());
    }
}
