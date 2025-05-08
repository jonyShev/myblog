package com.jonyshev.service;

import com.jonyshev.model.Comment;
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
import java.util.Optional;

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

    @Test
    void updatePost_shouldUpdate_whenValid() {
        //given
        //Long id, String title, String text, String tags, MultipartFile image
        Long id = 1L;
        String title = "title";
        String text = "title";
        String tags = "tags";

        Post existingPost = Post.builder()
                .id(id)
                .title(title)
                .text(text)
                .tags(List.of(tags))
                .build();

        when(postRepository.findById(id)).thenReturn(Optional.ofNullable(existingPost));

        MultipartFile image = mock(MultipartFile.class);

        when(image.isEmpty()).thenReturn(true);

        //when
        postService.updatePost(id, title, text, tags, image);

        //then
        verify(postRepository, times(1)).update(argThat(post -> post.getId().equals(id) &&
                post.getTitle().equals(title) &&
                post.getText().equals(text) &&
                post.getTags().equals(List.of(tags))));
    }

    @Test
    public void deletePost_shouldDeletePost_whenIdValid() {
        //given
        Long id = 1L;
        Post existingPost = Post.builder()
                .id(id)
                .build();

        when(postRepository.findById(id)).thenReturn(Optional.ofNullable(existingPost));

        //when
        postService.deletePost(id);

        //then
        verify(postRepository, times(1)).deleteById(id);
    }

    @Test
    public void likePost_shouldUpdatePost_whenDataValid() {
        //given
        Long id = 1L;
        int likesCount = 0;
        boolean increase = true;
        Post existingPost = Post.builder()
                .id(id)
                .likesCount(likesCount)
                .build();

        when(postRepository.findById(id)).thenReturn(Optional.ofNullable(existingPost));
        //when
        postService.likePost(id, increase);
        //then
        verify(postRepository, times(1)).update(argThat(post -> post.getId().equals(id) &&
                post.getLikesCount() == 1));
    }

    @Test
    public void addCommentToPost_shouldSaveComment_whenDataValid() {
        //given
        Long id = 1L;
        String text = "text";
        //when
        postService.addCommentToPost(id, text);
        //then
        verify(commentRepository, times(1)).save(eq(id), argThat(
                comment -> comment.getText().equals(text)));
    }

    @Test
    public void updateComment_shouldUpdateComment_whenDataValid() {
        //given
        Long id = 1L;
        Long commentId = 10L;
        String text = "text";

        //when
        postService.updateComment(id, commentId, text);

        //then
        verify(commentRepository, times(1)).update(eq(id), argThat(
                comment -> comment.getId().equals(commentId) &&
                        comment.getText().equals(text)));
    }
    }
}