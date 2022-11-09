package com.spring.redditclone.service;

import com.spring.redditclone.dto.SubredditDto;
import com.spring.redditclone.exceptions.RedditCloneException;
import com.spring.redditclone.mapper.SubredditMapper;
import com.spring.redditclone.model.Subreddit;
import com.spring.redditclone.model.User;
import com.spring.redditclone.repository.SubredditRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
public class SubredditService {
    private final SubredditRepository subredditRepository;
    private final SubredditMapper subredditMapper;
    private final AuthService authService;

    @Transactional
    public SubredditDto save(SubredditDto subredditDto) {
        User user = authService.getCurrentUser();
        Subreddit subreddit = subredditRepository.save(subredditMapper.mapDtoToSubreddit(subredditDto, user));
        subredditDto.setId(subreddit.getId());
        return subredditDto;
    }

    @Transactional(readOnly = true)
    public List<SubredditDto> getAll() {
        return subredditRepository.findAll()
                .stream()
                .map(subredditMapper::mapSubredditToDto)
                .collect(Collectors.toList());

    }

    @Transactional(readOnly = true)
    public SubredditDto getSubreddit(long id) {
        return subredditMapper
                .mapSubredditToDto(
                        subredditRepository.findById(id)
                                .orElseThrow(() -> new RedditCloneException("No Subreddit found with id : " + id))
                );
    }
}
