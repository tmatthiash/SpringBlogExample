package com.springboot.blog.posts;

import com.springboot.blog.TestSetup;
import com.springboot.blog.entity.Post;
import com.springboot.blog.payload.ErrorDetails;
import com.springboot.blog.payload.PostDto;
import com.springboot.blog.payload.PostResponse;
import com.springboot.blog.repository.PostRepository;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class PostApiTest extends TestSetup {

    @Autowired
    private PostRepository postRepository;

    @Test
    @DisplayName("should create a new post")
    void createNewPost() {

        PostDto returnPostDto = createPostWithTitle("new post");

        final Optional<Post> createdPost = postRepository
                .findById(1L);

        assertEquals(createdPost.isPresent(), true);
        createdPost.ifPresent(post -> {
            assertThat(post.getTitle()).isEqualTo("new post");
            assertThat(post.getContent()).isEqualTo("Test Content");
            assertThat(post.getDescription()).isEqualTo("Test Description");
        });

        assertThat(returnPostDto.getTitle()).isEqualTo("new post");
        assertThat(returnPostDto.getDescription()).isEqualTo("Test Description");
        assertThat(returnPostDto.getContent()).isEqualTo("Test Content");
    }

    @Test
    void getSinglePostById() {
        createPostWithTitle("new post");

        PostDto retrievedPost = given()
                .contentType("application/json")
                .get("/api/posts/1")
                .then()
                .statusCode(200)
                .extract()
                .body()
                .as(PostDto.class);

        assertThat(retrievedPost.getTitle()).isEqualTo("new post");
        assertThat(retrievedPost.getDescription()).isEqualTo("Test Description");
        assertThat(retrievedPost.getContent()).isEqualTo("Test Content");
    }

    @Test
    void getPageOfPosts() {
        createPostWithTitle("new post");

        PostResponse retrievedPage = given()
                .contentType("application/json")
                .get("/api/posts")
                .then()
                .statusCode(200)
                .extract()
                .body()
                .as(PostResponse.class);

        assertThat(retrievedPage.getPageNo()).isEqualTo(0);
        assertThat(retrievedPage.getPageSize()).isEqualTo(10);
        assertThat(retrievedPage.getTotalElements()).isEqualTo(1);
        assertThat(retrievedPage.getTotalPages()).isEqualTo(1);
        assertThat(retrievedPage.isLast()).isEqualTo(true);

        List<PostDto> pageContent = retrievedPage.getContent();

        assertThat(pageContent.get(0).getTitle()).isEqualTo("new post");
        assertThat(pageContent.get(0).getDescription()).isEqualTo("Test Description");
        assertThat(pageContent.get(0).getContent()).isEqualTo("Test Content");
    }

    @Test
    void getPostThatDoesntExist() {
        ErrorDetails response = given()
                .contentType("application/json")
                .get("/api/posts/100")
                .then()
                .statusCode(404)
                .extract()
                .body()
                .as(ErrorDetails.class);

        assertThat(response.getDetails()).isEqualTo("uri=/api/posts/100");
        assertThat(response.getMessage()).isEqualTo("Post not found with id : '100'");
    }

    private PostDto createPostWithTitle(String title) {
        PostDto postToCreate = new PostDto();
        postToCreate.setTitle(title);
        postToCreate.setDescription("Test Description");
        postToCreate.setContent("Test Content");

        PostDto returnPostDto = given()
                .body(postToCreate)
                .contentType("application/json")
                .post("/api/posts")
                .then()
                .statusCode(201)
                .extract()
                .body()
                .as(PostDto.class);
        return returnPostDto;
    }
}
