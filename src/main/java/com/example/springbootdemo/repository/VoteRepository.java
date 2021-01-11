package com.example.springbootdemo.repository;

import com.example.springbootdemo.model.Post;
import com.example.springbootdemo.model.User;
import com.example.springbootdemo.model.Vote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VoteRepository extends JpaRepository<Vote, Long> {
    Optional<Vote> findTopByPostAndUserOrderByVoteIdDesc(Post post, User currentUser);
}
