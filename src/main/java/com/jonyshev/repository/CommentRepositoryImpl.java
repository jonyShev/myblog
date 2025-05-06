package com.jonyshev.repository;

import com.jonyshev.model.Comment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class CommentRepositoryImpl implements CommentRepository {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public CommentRepositoryImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<Comment> commentRowMapper = ((rs, rowNum) -> Comment.builder()
            .id(rs.getLong("id"))
            .text(rs.getString("text"))
            .build());

    @Override
    public List<Comment> findByPostId(Long postId) {
        String sql = "SELECT * FROM comments WHERE id = ? ORDER BY id";
        return jdbcTemplate.query(sql, commentRowMapper, postId);
    }

    @Override
    public void save(Long postId, Comment comment) {
        String sql = "INSERT INTO comments (post_id, text) VALUES (?, ?)";
        jdbcTemplate.update(sql, postId, comment.getText());

    }

    @Override
    public void update(Long postId, Comment comment) {
        String sql = "UPDATE comments SET text = ? WHERE id = ? AND post_id = ?";
        jdbcTemplate.update(sql, comment.getText(), comment.getId(), postId);
    }

    @Override
    public void delete(Long postId, Long commentId) {
        String sql = "DELETE FROM comments WHERE id = ? AND post_id = ?";
        jdbcTemplate.update(sql, commentId, postId);
    }
}
