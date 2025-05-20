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

        jdbcTemplate.execute("DELETE FROM posts");
    }

    @Test
    void findAll_shouldReturnAllPosts_BySearchPageSizeAndPageNumber() {
        //given
        Post post1 = Post.builder()
                .title("title1")
                .text("text1")
                .imagePath("path1")
                .likesCount(1)
                .tags(List.of("java", "spring"))
                .build();

        Post post2 = Post.builder()
                .title("title2")
                .text("text2")
                .imagePath("path2")
                .likesCount(2)
                .tags(List.of("java", "sql"))
                .build();

        Post post3 = Post.builder()
                .title("title3")
                .text("text3")
                .imagePath("path3")
                .likesCount(3)
                .tags(List.of("maven", "gradle"))
                .build();

        postRepository.save(post1);
        postRepository.save(post2);
        postRepository.save(post3);

        //when
        List<Post> posts = postRepository.findAll("java", 10, 1);

        //then
        assertEquals(2, posts.size());
        assertTrue(posts.stream().allMatch(post -> post.getTags().contains("java")));
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
    void update_shouldUpdatePost_whenPostIsValid() {
        //given
        Post original = Post.builder()
                .title("original")
                .text("originalText")
                .imagePath("originalPath")
                .tags(List.of("originalTags"))
                .likesCount(1)
                .build();

        Long id = postRepository.save(original).getId();

        Post updatePost = Post.builder()
                .id(id)
                .title("update")
                .text("updateText")
                .imagePath("updatePath")
                .tags(List.of("updateTags"))
                .likesCount(2)
                .build();

        //when
        postRepository.update(updatePost);
        Optional<Post> optional = postRepository.findById(id);
        //then
        assertTrue(optional.isPresent());
        Post post = optional.get();
        assertEquals("update", post.getTitle());
        assertEquals("updateText", post.getText());
    }

    @Test
    void deleteById_shouldDeletePost_whenIdValid() {
        //given
        Post original = Post.builder()
                .title("original")
                .text("originalText")
                .imagePath("originalPath")
                .tags(List.of("originalTags"))
                .likesCount(1)
                .build();

        Long id = postRepository.save(original).getId();

        //when
        postRepository.deleteById(id);
        Optional<Post> result = postRepository.findById(id);

        //then
        assertTrue(result.isEmpty());
    }

    @Test
    void like() {
    }

    @Test
    void countPosts_shouldReturnCountOfPosts_whenIsPresentBySearch() {
        // given
        Post post1 = Post.builder()
                .title("Java")
                .text("text")
                .tags(List.of("java"))
                .build();

        Post post2 = Post.builder()
                .title("Spring")
                .text("text")
                .tags(List.of("java", "spring"))
                .build();

        Post post3 = Post.builder()
                .title("Docker")
                .text("text")
                .tags(List.of("devops"))
                .build();

        postRepository.save(post1);
        postRepository.save(post2);
        postRepository.save(post3);

        //when
        int all = postRepository.countPosts("");
        int javaPosts = postRepository.countPosts("java");
        int springPosts = postRepository.countPosts("spring");
        int devopsPosts = postRepository.countPosts("devops");

        //then
        assertEquals(3, all);
        assertEquals(2, javaPosts);
        assertEquals(1, springPosts);
        assertEquals(1, devopsPosts);
    }
}