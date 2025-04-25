package com.jonyshev.controller;

import com.jonyshev.model.Post;
import com.jonyshev.service.PostService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

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
}
