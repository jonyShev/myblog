package com.jonyshev.repository;

import com.jonyshev.model.Post;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
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
                "ORDER BY id DESC LIMIT ? OFFSET ?";

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
        String sql = "INSERT INTO posts (title, text, image_path, tags, likes_count) VALUES (?,?,?,?,?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, post.getTitle());
            ps.setString(2, post.getText());
            ps.setString(3, post.getImagePath());
            ps.setString(4, String.join(" ", post.getTags()));
            ps.setInt(5, post.getLikesCount());
            return ps;
        }, keyHolder);

        Long id = keyHolder.getKey().longValue();
        post.setId(id);
        return post;
    }

    @Override
    public void update(Post post) {
        String sql = "UPDATE posts SET title = ?, text = ?, image_path = ?, tags = ?, likes_count = ? WHERE ID = ?";
        jdbcTemplate.update(sql,
                post.getTitle(),
                post.getText(),
                post.getImagePath(),
                String.join(" ", post.getTags()),
                post.getLikesCount(),
                post.getId()
        );
    }

    @Override
    public void deleteById(Long id) {
        jdbcTemplate.update("DELETE FROM posts WHERE id = ?", id);
    }

    @Override
    public void like(Long id, boolean increase) {
        String sql = "UPDATE posts SET likes_count = likes_count " + (increase ? "+ 1" : "- 1") + " WHERE id = ?";
        jdbcTemplate.update(sql, id);
    }

    @Override
    public int countPosts(String search) {
        boolean hasSearch = search != null && !search.isBlank();

        String sql = "SELECT COUNT(*) FROM posts " + (hasSearch ? "WHERE title ILIKE ?" : "");

        return hasSearch
                ? jdbcTemplate.queryForObject(sql, Integer.class, "%" + search + "%")
                : jdbcTemplate.queryForObject(sql, Integer.class);
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
