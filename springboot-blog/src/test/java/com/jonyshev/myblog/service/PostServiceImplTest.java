package com.jonyshev.myblog.service;

import com.jonyshev.myblog.model.Comment;
import com.jonyshev.myblog.model.Post;
import com.jonyshev.myblog.repository.CommentRepository;
import com.jonyshev.myblog.repository.PostRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
@ActiveProfiles("test")
class PostServiceImplTest {

    @MockitoBean
    private PostRepository postRepository;

    @MockitoBean
    private CommentRepository commentRepository;

    @Autowired
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

    @Test
    public void getAllPosts_shouldReturnPosts_whenDataValid() {
        //given
        String search = "search";
        int pageSize = 5;
        int pageNumber = 1;
        Long postId = 1L;

        Post post = Post.builder()
                .id(postId)
                .build();
        List<Post> posts = List.of(post);

        when(postRepository.findAll(search, pageSize, pageNumber)).thenReturn(posts);

        Comment comment = Comment.builder()
                .id(10L)
                .text("text")
                .build();
        List<Comment> comments = List.of(comment);

        when(commentRepository.findByPostId(postId)).thenReturn(comments);

        //when
        List<Post> result = postService.getAllPosts(search, pageSize, pageNumber);

        //then
        assertEquals(1, result.size());
        assertEquals(postId, result.get(0).getId());
        assertEquals(1, result.get(0).getComments().size());
        assertEquals("text", result.get(0).getComments().get(0).getText());

        verify(postRepository, times(1)).findAll(eq(search), eq(pageSize), eq(pageNumber));
        verify(commentRepository, times(1)).findByPostId(eq(postId));
    }

    @Test
    public void getPostById_shouldReturnOptionalPost_whenIdValid() {
        //given
        Long id = 1L;

        Post post = Post.builder().id(id).build();
        Optional<Post> optional = Optional.of(post);

        when(postRepository.findById(id)).thenReturn(optional);

        Comment comment = Comment.builder()
                .id(100L)
                .text("text")
                .build();
        List<Comment> comments = List.of(comment);

        when(commentRepository.findByPostId(id)).thenReturn(comments);

        //when
        Optional<Post> optionalResult = postService.getPostById(id);

        //then
        assertEquals(1L, optionalResult.get().getId());
        assertEquals(comments, optionalResult.get().getComments());

        verify(postRepository, times(1)).findById(eq(id));
        verify(commentRepository, times(1)).findByPostId(eq(id));
    }

    @Test
    public void deleteComment_shouldDeleteComment_whenDataValid() {
        //given
        Long id = 1L;
        Long commentId = 100L;

        //when
        postService.deleteComment(id, commentId);
        //then
        verify(commentRepository, times(1)).delete(id, commentId);
    }

    @Test
    public void countPosts_shouldReturnCount_whenSearchValid() {
        //given
        String search = "search";
        when(postRepository.countPosts(search)).thenReturn(100);

        //when
        int result = postService.countPosts(search);

        //then
        assertEquals(100, result);
        verify(postRepository, times(1)).countPosts(search);
    }
}