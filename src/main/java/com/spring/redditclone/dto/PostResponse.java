package com.spring.redditclone.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PostResponse {
    private long id;
    private String postName;
    private String url;
    private String description;
    private String userName;
    private String subredditName;
    private int voteCount;
    private int commentCount;
    private String duration;
}
