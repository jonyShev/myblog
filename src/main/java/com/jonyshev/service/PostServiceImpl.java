package com.jonyshev.service;

import com.jonyshev.model.Post;
import com.jonyshev.model.Tag;
import com.jonyshev.repository.PostRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

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
    public void deletePost(Long id) {
        postRepository.deleteById(id);
    }

    @Override
    public void likePost(Long id, boolean increase) {
        postRepository.like(id, increase);
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
