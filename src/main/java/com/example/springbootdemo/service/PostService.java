package com.example.springbootdemo.service;

import com.example.springbootdemo.dto.PostRequest;
import com.example.springbootdemo.dto.PostResponse;
import com.example.springbootdemo.exceptions.SpringException;
import com.example.springbootdemo.mapper.PostMapper;
import com.example.springbootdemo.model.*;
import com.example.springbootdemo.repository.*;
import com.github.marlonlom.utilities.timeago.TimeAgo;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static com.example.springbootdemo.model.VoteType.DOWNVOTE;
import static com.example.springbootdemo.model.VoteType.UPVOTE;
import static java.util.stream.Collectors.toList;

@Service
@AllArgsConstructor
@Slf4j
@Transactional
public class PostService {

    private final PostRepository postRepository;
    private final SubredditRepository subredditRepository;
    private final UserRepository userRepository;
    private final AuthService authService;
    private final CommentRepository commentRepository;
    private final VoteRepository voteRepository;


    public void save(PostRequest postRequest) {
        Subreddit subreddit = subredditRepository.findByName(postRequest.getSubredditName())
                .orElseThrow(() -> new SpringException(postRequest.getSubredditName()));
        postRepository.save(map(postRequest, subreddit, authService.getCurrentUser()));
    }

    private Post map(PostRequest postRequest, Subreddit subreddit,User user) {
        return Post.builder()
                .createdDate(Instant.now())
                .url(postRequest.getUrl())
                .postName(postRequest.getPostName())
                .description(postRequest.getDescription())
                .subreddit(subreddit)
                .voteCount(0)
                .user(user)
                .build();
    }

    private PostResponse mapToDto(Post post) {
        return PostResponse.builder()
                .id(post.getPostId())
                .description(post.getDescription())
                .url(post.getUrl())
                .postName(post.getPostName())
                .subredditName(post.getSubreddit().getName())
                .userName(post.getUser() != null ? post.getUser().getUsername() : "")
                .commentCount(commentCount(post))
                .duration(getDuration(post))
                .upVote(isPostUpVoted(post))
                .downVote(isPostDownVoted(post))
                .voteCount(post.getVoteCount())
                .build();
    }


    Integer commentCount(Post post) {
        return commentRepository.findByPost(post).size();
    }

    protected String getDuration(Post post) {
        return TimeAgo.using(post.getCreatedDate().toEpochMilli());
    }

    boolean isPostUpVoted(Post post) {
        return checkVoteType(post, UPVOTE);
    }

    boolean isPostDownVoted(Post post) {
        return checkVoteType(post, DOWNVOTE);
    }

    private boolean checkVoteType(Post post, VoteType voteType) {
        if (authService.isLoggedIn()) {
            Optional<Vote> voteForPostByUser = voteRepository.findTopByPostAndUserOrderByVoteIdDesc(post, authService.getCurrentUser());
            return voteForPostByUser.filter(vote -> vote.getVoteType().equals(voteType))
                    .isPresent();
        }
        return false;
    }


    @Transactional(readOnly = true)
    public PostResponse getPost(Long id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new SpringException(id.toString()));
        return mapToDto(post);
    }


    @Transactional(readOnly = true)
    public List<PostResponse> getAllPosts() {
        return postRepository.findAll()
                .stream()
                .map(this::mapToDto)
                .collect(toList());
    }

    @Transactional(readOnly = true)
    public List<PostResponse> getPostsBySubreddit(Long subredditId) {
        Subreddit subreddit = subredditRepository.findById(subredditId)
                .orElseThrow(() -> new SpringException(subredditId.toString()));
        List<Post> posts = postRepository.findAllBySubreddit(subreddit);
        return posts.stream().map(this::mapToDto).collect(toList());
    }

    @Transactional(readOnly = true)
    public List<PostResponse> getPostsByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(username));
        return postRepository.findByUser(user)
                .stream()
                .map(this::mapToDto)
                .collect(toList());
    }
}
