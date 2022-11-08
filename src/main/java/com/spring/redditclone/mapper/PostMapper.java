package com.spring.redditclone.mapper;

import com.spring.redditclone.dto.PostRequest;
import com.spring.redditclone.dto.PostResponse;
import com.spring.redditclone.model.Post;
import com.spring.redditclone.model.Subreddit;
import com.spring.redditclone.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PostMapper {
    @Mapping(target = "postId", source = "postRequest.id")
    @Mapping(target = "createdDate", expression = "java(java.time.Instant.now())")
    @Mapping(target = "description", source = "postRequest.description")
    @Mapping(target = "user", source = "user")
    @Mapping(target = "subreddit", source = "subreddit")
    Post map(PostRequest postRequest, Subreddit subreddit, User user);

    @Mapping(target = "id", source = "postId")
    @Mapping(target = "userName", source = "user.username")
    @Mapping(target = "subredditName", source = "subreddit.name")
    PostResponse mapToDto(Post post);
}
