package com.jonyshev.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Post {
    private Long id;
    private String title;
    private String text;
    private String imagePath;
    private List<Tag> tags;
    private int likesCount;
    private List<Comment> comments;
}
