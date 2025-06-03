package com.jonyshev.myblog.repository;

import com.jonyshev.myblog.model.Comment;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@ActiveProfiles("test")
class CommentRepositoryImplTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private CommentRepositoryImpl commentRepository;

    @BeforeEach
    void setUp() {
        jdbcTemplate.execute("DELETE FROM comments");
        jdbcTemplate.execute("DELETE FROM posts");
    }


    @Test
    void findByPostId_shouldReturnCommentsInOrder_byPostId() {
        //given
        Long postId = 1L;

        jdbcTemplate.execute("INSERT INTO posts (id, title) VALUES (1, 'test_post')");

        jdbcTemplate.update("INSERT INTO comments (post_id, text) VALUES (?, ?)", postId, "first_comment");
        jdbcTemplate.update("INSERT INTO comments (post_id, text) VALUES (?, ?)", postId, "second_comment");
        jdbcTemplate.update("INSERT INTO comments (post_id, text) VALUES (?, ?)", postId, "third_comment");


        //when
        List<Comment> commentList = commentRepository.findByPostId(postId);

        //then
        assertEquals(3, commentList.size());
        assertEquals("first_comment", commentList.get(0).getText());
        assertEquals("second_comment", commentList.get(1).getText());
        assertEquals("third_comment", commentList.get(2).getText());
    }

    @Test
    void save_shouldSaveCommentProperly() {
        //given
        Long postId = 1L;
        Comment comment = Comment.builder()
                .text("text")
                .build();

        //when
        jdbcTemplate.execute("INSERT INTO posts (id, title) VALUES (1, 'post')");

        commentRepository.save(postId, comment);
        List<Comment> commentByPostId = commentRepository.findByPostId(postId);

        //then
        assertEquals("text", commentByPostId.get(0).getText());
    }

    @Test
    void update_shouldUpdateComment_byPostId() {
        //given
        Long postId = 1L;
        Comment comment = Comment.builder()
                .id(1L)
                .text("update_comment")
                .build();

        jdbcTemplate.execute("INSERT INTO posts (id, title) VALUES (1, 'post')");
        jdbcTemplate.execute("INSERT INTO comments (id, post_id, text) VALUES (1, 1, 'original_comment')");

        //when
        commentRepository.update(postId, comment);
        List<Comment> comments = commentRepository.findByPostId(postId);

        //then
        assertEquals("update_comment", comments.get(0).getText());
    }

    @Test
    void delete_shouldDeleteComment_byPostIdAndCommentId() {
        //given
        Long postId = 1L;
        Long commentId = 1L;

        jdbcTemplate.execute("INSERT INTO posts (id, title) VALUES (1, 'post')");
        jdbcTemplate.execute("INSERT INTO comments (id, post_id, text) VALUES (1, 1, 'comment1')");
        jdbcTemplate.execute("INSERT INTO comments (id, post_id, text) VALUES (2, 1, 'comment2')");

        //when
        commentRepository.delete(postId, commentId);
        List<Comment> comments = commentRepository.findByPostId(postId);

        //then
        assertEquals(1, comments.size());
        assertEquals("comment2", comments.get(0).getText());
    }
}