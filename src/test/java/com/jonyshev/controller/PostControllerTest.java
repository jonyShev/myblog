package com.jonyshev.controller;

import com.jonyshev.config.AppConfig;
import com.jonyshev.config.TestJdbcConfig;
import com.jonyshev.config.WebMvcConfig;
import com.jonyshev.model.Post;
import com.jonyshev.service.PostService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {AppConfig.class, WebMvcConfig.class, TestJdbcConfig.class})
@WebAppConfiguration
class PostControllerTest {

    @Autowired
    private WebApplicationContext wac;

    private MockMvc mockMvc;

    @MockitoBean
    private PostService postService;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();
    }

    @Test
    void getAllPosts_shouldReturnPostsTemplate() throws Exception {
        mockMvc.perform(get("/posts"))
                .andExpect(status().isOk())
                .andExpect(view().name("posts"))
                .andExpect(model().attributeExists("posts", "search", "paging"));
    }

    @Test
    void likePost_shouldRedirectAndCallService() throws Exception {
        Long postId = 1L;

        mockMvc.perform(post("/posts/1/like").param("like", "true"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/posts/" + postId));

        verify(postService).likePost(postId, true);
    }

    @Test
    void getPostById_shouldReturnPostTemplate_whenPostExist() throws Exception {
        //given
        Long postId = 1L;
        Post post = Post.builder()
                .id(postId)
                .text("text")
                .comments(List.of())
                .build();

        //when
        when(postService.getPostById(postId)).thenReturn(Optional.of(post));

        //then
        mockMvc.perform(get("/posts/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("post"))
                .andExpect(model().attribute("post", post));
    }

    @Test
    void getPostById_shouldRedirect_whenPostDoesNotExist() throws Exception {
        //given
        Long postId = 1L;

        //when
        when(postService.getPostById(postId)).thenReturn(Optional.empty());
        //then
        mockMvc.perform(get("/posts/1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/posts"));
    }
}