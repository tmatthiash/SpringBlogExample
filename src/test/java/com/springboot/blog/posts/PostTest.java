package com.springboot.blog.posts;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.springboot.blog.controller.PostController;
import com.springboot.blog.entity.Post;
import com.springboot.blog.payload.PostDto;
import com.springboot.blog.repository.PostRepository;
import com.springboot.blog.service.impl.PostServiceImpl;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.modelmapper.ModelMapper;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@WebMvcTest(PostController.class)
public class PostTest {
    
    @Autowired
    private MockMvc mockMvc;

    @SpyBean
    PostServiceImpl postService;

    @MockBean
    private PostRepository repository;

    @Autowired
    private ModelMapper mapper;

    @Test
    public void getShouldReturnPostList() throws Exception{
        Post aPost = new Post();
        aPost.setId(1L);
        aPost.setComments(null);
        aPost.setContent("test content");
        aPost.setDescription("test description");
        aPost.setTitle("test title");

        List<Post> returnList = List.of(aPost);

        Page<Post> pageResponse = new PageImpl<>(returnList);

        when(repository.findAll(Mockito.any(Pageable.class)))
            .thenReturn(pageResponse);
        
        RequestBuilder request = MockMvcRequestBuilders.get("/api/posts");
        
        MvcResult result = mockMvc.perform(request)
            .andExpect(status().isOk())
            .andReturn();

        JSONObject resultJSON = new JSONObject(result.getResponse().getContentAsString());

        assertEquals(resultJSON.getString("pageNo"), "0");
        assertEquals(resultJSON.getString("pageSize"), "10");
        assertEquals(resultJSON.getString("totalElements"), "1");
        assertEquals(resultJSON.getString("totalPages"), "1");
        assertEquals(resultJSON.getString("last"), "true");
        
        JSONArray contentField = resultJSON.getJSONArray("content");
        assertEquals(contentField.getJSONObject(0).getString("id"), "1");
        assertEquals(contentField.getJSONObject(0).getString("content"), "test content");
        assertEquals(contentField.getJSONObject(0).getString("description"), "test description");
        assertEquals(contentField.getJSONObject(0).getString("title"), "test title");
    }

    @Test
    public void shouldReturnSinglePost() throws Exception{
        Post aPost = new Post();
        aPost.setId(1L);
        aPost.setComments(null);
        aPost.setContent("test content");
        aPost.setDescription("test description");
        aPost.setTitle("test title");

        Optional<Post> postOptional = Optional.of(aPost);

        when(repository.findById(Mockito.anyLong()))
            .thenReturn(postOptional);

        RequestBuilder request = MockMvcRequestBuilders.get("/api/posts/1");
    
        MvcResult result = mockMvc.perform(request)
            .andExpect(status().isOk())
            .andReturn();
        
        JSONObject resultJSON = new JSONObject(result.getResponse().getContentAsString());

        assertEquals(resultJSON.getString("id"), "1");
        assertEquals(resultJSON.getString("content"), "test content");
        assertEquals(resultJSON.getString("description"), "test description");
        assertEquals(resultJSON.getString("title"), "test title");
    }

    @Test
    public void shouldCreateSinglePost() throws Exception {
        PostDto aPostDto = new PostDto();
        aPostDto.setId(1L);
        aPostDto.setComments(null);
        aPostDto.setContent("test content");
        aPostDto.setDescription("test description");
        aPostDto.setTitle("test title");

        ObjectMapper jsonMapper = new ObjectMapper();
        String postBodyString = jsonMapper.writeValueAsString(aPostDto);

        when(repository.save(Mockito.any()))
            .thenReturn(mapper.map(aPostDto, Post.class));

        RequestBuilder request = MockMvcRequestBuilders.post("/api/posts")
            .content(postBodyString)
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON);

        MvcResult result = mockMvc.perform(request)
            .andExpect(status().isCreated())
            .andReturn();

        ArgumentCaptor<Post> arguments = ArgumentCaptor.forClass(Post.class);
        verify(repository).save(arguments.capture());
        assertEquals(1, arguments.getValue().getId());
        assertEquals("test content", arguments.getValue().getContent());
        assertEquals("test description", arguments.getValue().getDescription());
        assertEquals("test title", arguments.getValue().getTitle());
        

        JSONObject resultJSON = new JSONObject(result.getResponse().getContentAsString());
        assertEquals(resultJSON.getString("id"), "1");
        assertEquals(resultJSON.getString("content"), "test content");
        assertEquals(resultJSON.getString("description"), "test description");
        assertEquals(resultJSON.getString("title"), "test title");
    }

    @Test
    public void invalidPostIdException() throws Exception{
        when(repository.findById(Mockito.anyLong()))
            .thenReturn(Optional.empty());

        RequestBuilder request = MockMvcRequestBuilders.get("/api/posts/1");
    
        MvcResult result = mockMvc.perform(request)
            .andExpect(status().isNotFound())
            .andReturn();
        
        JSONObject resultJSON = new JSONObject(result.getResponse().getContentAsString());

        System.out.println(resultJSON);
        assertEquals(resultJSON.getString("details"), "uri=/api/posts/1");
        assertEquals(resultJSON.getString("message"), "Post not found with id : '1'");
    }
}
