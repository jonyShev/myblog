package com.jonyshev.myblog.repository;

import com.jonyshev.myblog.model.Comment;

import java.util.List;

public interface CommentRepository {
    List<Comment> findByPostId(Long postId);

    void save(Long postId, Comment comment);

    void update(Long postId, Comment comment);

    void delete(Long postId, Long commentId);

}
