package com.jonyshev.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Post {
    private Long id;
    private String title;
    private String text;
    private String imagePath;
    private List<String> tags;
    private int likesCount;
    private List<Comment> comments;
}
