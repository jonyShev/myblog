package com.jonyshev.myblog.repository;

import com.jonyshev.myblog.model.Post;

import java.util.List;
import java.util.Optional;

public interface PostRepository {
    List<Post> findAll(String search, int pageSize, int pageNumber);

    Optional<Post> findById(Long id);

    Post save(Post post);

    void update(Post post);

    void deleteById(Long id);

    void like(Long id, boolean increase);

    int countPosts(String search);
}
