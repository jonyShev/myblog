package com.jonyshev.repository;

import com.jonyshev.model.Post;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Repository
public class PostRepositoryImpl implements PostRepository {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public PostRepositoryImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<Post> postRowMapper = (rs, rowNum) -> Post.builder()
            .id(rs.getLong("id"))
            .title(rs.getString("title"))
            .text(rs.getString("text"))
            .imagePath(rs.getString("image_path"))
            .tags(splitTags(rs.getString("tags")))
            .likesCount(rs.getInt("likes_count"))
            .build();


    @Override
    public List<Post> findAll(String search, int pageSize, int pageNumber) {
        boolean hasSearch = search != null && !search.isBlank();

        String sql = "SELECT * FROM posts " +
                (hasSearch ? "WHERE title ILIKE ? " : "") +
                "ORDERED BY id DESC LIMIT ? OFFSET ?";

        int offset = (pageNumber - 1) * pageSize;

        return hasSearch
                ? jdbcTemplate.query(sql, postRowMapper, "%" + search + "%", pageSize, offset)
                : jdbcTemplate.query(sql, postRowMapper, pageSize, offset);
    }

    @Override
    public Optional<Post> findById(Long id) {
        String sql = "SELECT * FROM posts WHERE id = ?";
        return jdbcTemplate.query(sql, postRowMapper, id).stream().findFirst();
    }

    @Override
    public Post save(Post post) {
        return null;
    }

    @Override
    public void update(Post post) {

    }

    @Override
    public void deleteById(Long id) {

    }

    @Override
    public void like(Long id, boolean increase) {

    }

    private List<String> splitTags(String tags) {
        if (tags == null || tags.isBlank()) {
            return List.of();
        }
        return Arrays.stream(tags.trim().split("\\s+"))
                .filter(s -> !s.isBlank())
                .toList();
    }

}
