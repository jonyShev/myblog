package com.jonyshev.service;

import com.jonyshev.model.Comment;
import com.jonyshev.model.Post;
import com.jonyshev.repository.PostRepository;
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

    public PostServiceImpl(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    @Override
    public List<Post> getAllPosts(String search, int pageSize, int pageNumber) {
        return postRepository.findAll(search, pageSize, pageNumber);
    }

    @Override
    public Optional<Post> getPostById(Long id) {
        return postRepository.findById(id);
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
        Optional<Post> optionalPost = postRepository.findById(id);
        if (optionalPost.isEmpty()) {
            throw new IllegalArgumentException("Пост не найден");
        }

        Post post = optionalPost.get();
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
        Optional<Post> optionalPost = postRepository.findById(id);
        if (optionalPost.isEmpty()) {
            throw new IllegalArgumentException("Пост не найден");
        }
        postRepository.deleteById(id);
    }

    @Override
    public void likePost(Long id, boolean increase) {
        Optional<Post> optionalPost = postRepository.findById(id);
        if (optionalPost.isEmpty()) {
            throw new IllegalArgumentException("Пост не найден");
        }

        Post post = optionalPost.get();
        int likes = post.getLikesCount();
        post.setLikesCount(increase ? likes + 1 : Math.max(0, likes - 1));
        postRepository.update(post);
    }

    @Override
    public void addCommentToPost(Long id, String text) {
        Optional<Post> optionalPost = postRepository.findById(id);
        if (optionalPost.isEmpty()) {
            throw new IllegalArgumentException("Пост не найден");
        }

        Post post = optionalPost.get();
        List<Comment> comments = post.getComments();
        if (comments == null) {
            post.setComments(new ArrayList<>());
        }

        long commentId = UUID.randomUUID().getMostSignificantBits() & Long.MAX_VALUE;
        Comment comment = new Comment(commentId, text);

        post.getComments().add(comment);

        postRepository.update(post);
    }

    @Override
    public void updateComment(Long id, Long commentId, String text) {
        Optional<Post> optionalPost = postRepository.findById(id);
        if (optionalPost.isEmpty()) {
            throw new IllegalArgumentException("Пост не найден");
        }

        Post post = optionalPost.get();
        List<Comment> comments = post.getComments();
        if (comments == null) {
            throw new IllegalArgumentException("У поста нет комментов");
        }

        Comment comment = comments.stream()
                .filter(x -> x.getId().equals(commentId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Комментарий не найден"));

        comment.setText(text);

        postRepository.update(post);
    }

    @Override
    public void deleteComment(Long id, Long commentId) {
        Optional<Post> optionalPost = postRepository.findById(id);
        if (optionalPost.isEmpty()) {
            throw new IllegalArgumentException("Пост не найден");
        }

        Post post = optionalPost.get();
        List<Comment> comments = post.getComments();
        if (comments == null) {
            throw new IllegalArgumentException("У поста нет комментов");
        }

        boolean removed = comments.removeIf(x -> x.getId().equals(commentId));

        if (!removed) {
            throw new IllegalArgumentException("Комментарий не найден");
        }

        postRepository.update(post);
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
}
