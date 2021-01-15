package com.example.springbootdemo.service;

import com.example.springbootdemo.dto.CommentsDto;
import com.example.springbootdemo.dto.CommentsDtoDelete;
import com.example.springbootdemo.exceptions.SpringException;
import com.example.springbootdemo.model.Comment;
import com.example.springbootdemo.model.Post;
import com.example.springbootdemo.model.User;
import com.example.springbootdemo.repository.CommentRepository;
import com.example.springbootdemo.repository.PostRepository;
import com.example.springbootdemo.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

import static java.util.stream.Collectors.toList;

@Service
@AllArgsConstructor
public class CommentService {
    private static final String POST_URL = "";
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final AuthService authService;
    private final CommentRepository commentRepository;


    public void save(CommentsDto commentsDto) {
        Post post = postRepository.findById(commentsDto.getPostId()).orElseThrow(() -> new SpringException(commentsDto.getPostId().toString()));
        Comment comment = map(commentsDto, post, authService.getCurrentUser());
        commentRepository.save(comment);
    }

    private Comment map(CommentsDto commentsDto, Post post,User currentUser) {
        return Comment.builder()
                .text(commentsDto.getText())
                .createdDate(Instant.now())
                .post(post)
                .user(currentUser)
                .build();
    }


    public List<CommentsDto> getAllCommentsForPost(Long postId) {
        Post post = postRepository.findById(postId).orElseThrow(() -> new SpringException(postId.toString()));
        return commentRepository.findByPost(post)
                .stream()
                .map(this::mapToDto).collect(toList());
    }

    private CommentsDto mapToDto(Comment comment) {
        return CommentsDto.builder()
                .id(comment.getId())
                .userName(comment.getUser().getUsername())
                .createdDate(comment.getCreatedDate())
                .text(comment.getText())
                .postId(comment.getPost().getPostId())
                .build();
    }


    public List<CommentsDto> getAllCommentsForUser(String userName) {
        User user = userRepository.findByUsername(userName)
                .orElseThrow(() -> new UsernameNotFoundException(userName));
        return commentRepository.findAllByUser(user)
                .stream()
                .map(this::mapToDto)
                .collect(toList());
    }

    public void delete(CommentsDtoDelete commentsDtoDelete) {
        if (commentsDtoDelete.getUsername().equals(authService.getCurrentUser().getUsername()) ){
            commentRepository.deleteById(commentsDtoDelete.getCommentId());
        }else {
            System.out.println("chay vao day");
        }
    }
}
