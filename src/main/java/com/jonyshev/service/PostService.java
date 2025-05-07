package com.jonyshev.service;

import com.jonyshev.model.Post;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

public interface PostService {
    List<Post> getAllPosts(String search, int pageSize, int pageNumber);

    Optional<Post> getPostById(Long id);

    Long createPost(String title, String text, String tags, MultipartFile image);

    void updatePost(Post post);

    void updatePost(Long id, String title, String text, String tags, MultipartFile image);

    void deletePost(Long id);

    void likePost(Long id, boolean increase);

    void addCommentToPost(Long id, String text);

    void updateComment(Long id, Long commentId, String text);

    void deleteComment(Long id, Long commentId);

    int countPosts(String search);
}
