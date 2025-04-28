package com.jonyshev.controller;

import com.jonyshev.model.Post;
import com.jonyshev.service.PostService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Controller
public class PostController {
    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    @GetMapping("/")
    public String redirectToPosts() {
        return "redirect:/posts";
    }

    @GetMapping("/posts")
    public String getAllPosts(
            @RequestParam(required = false, defaultValue = "") String search,
            @RequestParam(required = false, defaultValue = "10") int pageSize,
            @RequestParam(required = false, defaultValue = "1") int pageNumber,
            Model model) {
        List<Post> posts = postService.getAllPosts(search, pageSize, pageNumber);
        model.addAttribute("posts", posts);
        model.addAttribute("search", search);
        return "posts";
    }

    @GetMapping("posts/{id}")
    public String getPostById(@PathVariable Long id, Model model) {
        return postService.getPostById(id)
                .map(post -> {
                    model.addAttribute("post", post);
                    return "post";
                })
                .orElse("redirect:/posts");
    }

    @GetMapping("/posts/add")
    public String showAddPostForm(Model model) {
        model.addAttribute("post", null);
        return "add-post";
    }

    @PostMapping("/posts")
    public String createPost(
            @RequestParam String title,
            @RequestParam String text,
            @RequestParam(required = false) String tags,
            @RequestParam(required = false) MultipartFile image) {
        Long id = postService.createPost(title, text, tags, image);
        return "redirect:/posts/" + id;
    }

    @GetMapping("/images/{fileName}")
    public ResponseEntity<byte[]> getImage(@PathVariable String fileName) {
        Path imagePath = Paths.get("uploads", fileName);
        try {
            byte[] imageBytes = Files.readAllBytes(imagePath);

            return ResponseEntity
                    .ok()
                    .header("Content-Type", detectContentType(fileName))
                    .body(imageBytes);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @GetMapping("/posts/{id}/like")
    public String likePost(@PathVariable Long id,
                           @RequestParam boolean like) {
        postService.likePost(id, like);
        return "redirect/posts/" + id;
    }

    private String detectContentType(String fileName) {
        String lowerCaseFileName = fileName.toLowerCase();
        if (lowerCaseFileName.endsWith(".png")) {
            return "image/png";
        } else if (lowerCaseFileName.endsWith(".jpeg") || lowerCaseFileName.endsWith(".jpg")) {
            return "image/jpeg";
        } else if (lowerCaseFileName.endsWith(".gif")) {
            return "image/gif";
        } else {
            return "application/octet-stream";
        }
    }

}
