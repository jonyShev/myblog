package com.jonyshev.repository;

import com.jonyshev.config.TestJdbcConfig;
import com.jonyshev.model.Post;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = TestJdbcConfig.class)
class PostRepositoryImplTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private PostRepositoryImpl postRepository;


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
    }

    @Test
    void findAll() {
    }

    @Test
    void findById() {
    }

    @Test
    void save_shouldSaveAndReturnId_whenPostValid() {
        //given
        Post post = Post.builder()
                .title("title")
                .text("text")
                .imagePath("imagePath")
                .tags(List.of("tags"))
                .likesCount(1)
                .build();

        //when
        Post savedPost = postRepository.save(post);
        Optional<Post> foundPost = postRepository.findById(savedPost.getId());

        //then
        assertTrue(foundPost.isPresent());
        assertEquals(savedPost.getId(), foundPost.get().getId());
        assertEquals("title", foundPost.get().getTitle());
        assertEquals("text", foundPost.get().getText());
    }

    @Test
    void update() {
    }

    @Test
    void deleteById() {
    }

    @Test
    void like() {
    }

    @Test
    void countPosts() {
    }
}