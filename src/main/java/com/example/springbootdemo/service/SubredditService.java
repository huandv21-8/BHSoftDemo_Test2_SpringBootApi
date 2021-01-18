package com.example.springbootdemo.service;

import com.example.springbootdemo.dto.SubredditDto;
import com.example.springbootdemo.exceptions.SpringException;
import com.example.springbootdemo.model.Post;
import com.example.springbootdemo.model.Subreddit;
import com.example.springbootdemo.repository.SubredditRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static java.util.stream.Collectors.toList;

@Service
@AllArgsConstructor
@Slf4j
public class SubredditService {

    private final SubredditRepository subredditRepository;

    @Transactional
    public SubredditDto save(SubredditDto subredditDto) {
        Subreddit subreddit = subredditRepository.save(mapDtoToSubreddit(subredditDto));
        subredditDto.setId(subreddit.getId());
        return subredditDto;
    }

    private Subreddit mapDtoToSubreddit(SubredditDto subredditDto) {
        return Subreddit.builder()
                .id(subredditDto.getId())
                .name(subredditDto.getName())
                .description(subredditDto.getDescription())
                .build();
    }

//    @Transactional(readOnly = true)
    public List<SubredditDto> getAll() {
        return subredditRepository.findAll()
               .stream()
                .map(this::mapSubredditToDto)
                .collect(toList());
    }

    private SubredditDto mapSubredditToDto(Subreddit subreddit) {
        return SubredditDto.builder()
                .id(subreddit.getId())
                .description(subreddit.getDescription())
                .name(subreddit.getName())
                .numberOfPosts(mapPosts(subreddit.getPosts()))
                .build();
    }

     Integer mapPosts(List<Post> numberOfPosts) {
        return numberOfPosts.size();
    }

    public SubredditDto getSubreddit(Long id) {
        Subreddit subreddit = subredditRepository.findById(id)
                .orElseThrow(() -> new SpringException("No subreddit found with ID - " + id));
        return mapSubredditToDto(subreddit);
    }
}
