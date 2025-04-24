package com.jonyshev.service;

import com.jonyshev.model.Post;

import java.util.List;
import java.util.Optional;

public interface PostService {
    List<Post>  getAllPosts(String search, int pageSize, int pageNumber);

    Optional<Post> getPostById(Long id);

    Post createPost(Post post);

    void updatePost(Post post);

    void deletePost(Long id);

    void likePost(Long id, boolean increase);
}
