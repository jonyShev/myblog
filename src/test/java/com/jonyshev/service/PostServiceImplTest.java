package com.jonyshev.service;

import com.jonyshev.model.Post;
import com.jonyshev.repository.CommentRepository;
import com.jonyshev.repository.PostRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PostServiceImplTest {

    @Mock
    private PostRepository postRepository;

    @Mock
    private CommentRepository commentRepository;

    @InjectMocks
    private PostServiceImpl postService;

    @Test
    void createPost_shouldSaveAndReturnId_whenValid() {
        //given
        String title = "title";
        String text = "text";
        String tags = "tags";
        MultipartFile image = mock(MultipartFile.class);

        when(image.isEmpty()).thenReturn(true);

        Post savedPost = Post.builder()
                .id(1L)
                .title(title)
                .text(text)
                .tags(List.of("tags"))
                .likesCount(1)
                .build();

        when(postRepository.save(any(Post.class))).thenReturn(savedPost);

        //when
        Long id = postService.createPost(title, text, tags, image);
        //then
        assertEquals(1L, id);
        verify(postRepository, times(1)).save(argThat(post ->
                post.getTitle().equals(title) &&
                        post.getText().equals(text) &&
                        post.getTags().containsAll(List.of(tags))
        ));
    }
}