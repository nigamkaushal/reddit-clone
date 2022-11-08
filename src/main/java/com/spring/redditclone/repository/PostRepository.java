package com.spring.redditclone.repository;

import com.spring.redditclone.model.Post;
import com.spring.redditclone.model.Subreddit;
import com.spring.redditclone.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    List<Post> findAllBySubreddit(Subreddit subreddit);
    List<Post> findAllByUser(User user);
}
