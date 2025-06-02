package com.jonyshev.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Arrays;
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

    public List<String> getTextParts() {
        if (text == null || text.isBlank()) return List.of();
        return Arrays.asList(text.split("\\r?\\n"));
    }

    public String getTextPreview() {
        if (text == null) return "";
        String[] lines = text.split("\n");

        StringBuilder preview = new StringBuilder();
        for (int i = 0; i < Math.min(lines.length, 3); i++) {
            preview.append(lines[i]).append(" ");
        }

        String result = preview.toString().trim();
        return result.length() > 300 ? result.substring(0, 300) + "..." : result;
    }

    public String getTagsAsText() {
        if (tags == null || tags.isEmpty()) return "";
        return String.join(" ", tags);
    }
}
