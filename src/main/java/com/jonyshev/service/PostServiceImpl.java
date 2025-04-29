package com.jonyshev.service;

import com.jonyshev.model.Comment;
import com.jonyshev.model.Post;
import com.jonyshev.repository.CommentRepository;
import com.jonyshev.repository.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

@Service
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;
    private final CommentRepository commentRepository;

    @Autowired
    public PostServiceImpl(PostRepository postRepository, CommentRepository commentRepository) {
        this.postRepository = postRepository;
        this.commentRepository = commentRepository;
    }


    @Override
    public List<Post> getAllPosts(String search, int pageSize, int pageNumber) {
        List<Post> posts = postRepository.findAll(search, pageSize, pageNumber);
        posts.forEach(post -> post.setComments(commentRepository.findByPostId(post.getId())));
        return posts;
    }

    @Override
    public Optional<Post> getPostById(Long id) {
        Optional<Post> optional = postRepository.findById(id);
        optional.ifPresent(post -> post.setComments(commentRepository.findByPostId(id)));
        return optional;
    }

    @Override
    public Long createPost(String title, String text, String tags, MultipartFile image) {
        List<String> tagList = parseTags(tags);
        String imagePath = saveImageFile(image);

        Post post = Post.builder()
                .title(title)
                .text(text)
                .tags(tagList)
                .imagePath(imagePath)
                .build();

        return postRepository.save(post).getId();
    }

    @Override
    public void updatePost(Post post) {
        postRepository.update(post);
    }

    @Override
    public void updatePost(Long id, String title, String text, String tags, MultipartFile image) {
        Post post = getPostOrThrow(id);
        post.setTitle(title);
        post.setText(text);
        if (tags != null && !tags.isBlank()) {
            post.setTags(parseTags(tags));
        }
        if (image != null && !image.isEmpty()) {
            post.setImagePath(saveImageFile(image));
        }
        postRepository.update(post);
    }

    @Override
    public void deletePost(Long id) {
        getPostOrThrow(id);
        postRepository.deleteById(id);
    }

    @Override
    public void likePost(Long id, boolean increase) {
        Post post = getPostOrThrow(id);
        int likes = post.getLikesCount();
        post.setLikesCount(increase ? likes + 1 : Math.max(0, likes - 1));
        postRepository.update(post);
    }

    @Override
    public void addCommentToPost(Long id, String text) {
        Comment comment = Comment.builder()
                .text(text)
                .build();

        commentRepository.save(id, comment);
    }

    @Override
    public void updateComment(Long id, Long commentId, String text) {
        Comment comment = Comment.builder()
                .id(commentId)
                .text(text)
                .build();

        commentRepository.update(id, comment);
    }

    @Override
    public void deleteComment(Long id, Long commentId) {
        commentRepository.delete(id, commentId);
    }


    private String saveImageFile(MultipartFile image) {
        if (image == null || image.isEmpty()) {
            return null;
        }

        String imageName = UUID.randomUUID() + "_" + image.getOriginalFilename();
        Path pathToImage = Paths.get("uploads", imageName);

        try {
            Files.createDirectories(pathToImage.getParent());
            image.transferTo(pathToImage.toFile());
            return imageName;
        } catch (IOException e) {
            throw new RuntimeException("Не удалось сохранить файл", e);
        }
    }

    private List<String> parseTags(String tags) {
        if (tags == null || tags.isBlank()) {
            return List.of();
        }

        return Arrays.stream(tags.trim().split("\\s+"))
                .filter(s -> !s.isBlank())
                .toList();
    }

    private Post getPostOrThrow(Long id) {
        return postRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Пост не найден"));
    }

    private List<Comment> getOrCreateComments(Post post) {
        if (post.getComments() == null) {
            post.setComments(new ArrayList<>());
        }
        return post.getComments();
    }

    private static List<Comment> getCommentsOrThrow(Post post) {
        List<Comment> comments = post.getComments();
        if (comments == null) {
            throw new IllegalArgumentException("У поста нет комментов");
        }
        return comments;
    }
}
