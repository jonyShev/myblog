package com.jonyshev.repository;

import com.jonyshev.config.TestJdbcConfig;
import com.jonyshev.model.Comment;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = TestJdbcConfig.class)
class CommentRepositoryImplTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private CommentRepositoryImpl commentRepository;

    @BeforeEach
    void setUp() {
        jdbcTemplate.execute("""
                CREATE TABLE IF NOT EXISTS posts (
                    id IDENTITY PRIMARY KEY,
                    title VARCHAR(255) NOT NULL,
                    text TEXT,
                    image_path VARCHAR(255),
                    tags TEXT,
                    likes_count INTEGER DEFAULT 0
                );
                """);

        jdbcTemplate.execute("""
                CREATE TABLE IF NOT EXISTS comments (
                    id IDENTITY PRIMARY KEY,
                    post_id BIGINT NOT NULL,
                    text TEXT,
                    CONSTRAINT fk_comments_posts FOREIGN KEY (post_id) REFERENCES posts(id) ON DELETE CASCADE
                );
                """);

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
    void delete() {
    }
}