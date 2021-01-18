package com.example.springbootdemo.repository;

import com.example.springbootdemo.model.Comment;
import com.example.springbootdemo.model.Post;
import com.example.springbootdemo.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByPost(Post post);

    List<Comment> findAllByUser(User user);
//    void deleteCommentByPostOrId(long idPost);
//    boolean removeAllByPostOrderByIdIdAsc( Long id);

}
