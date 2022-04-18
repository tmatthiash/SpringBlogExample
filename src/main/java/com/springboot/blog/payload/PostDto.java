package com.springboot.blog.payload;

import java.util.Set;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

import lombok.Data;

@Data
public class PostDto {
    private Long id;

    @NotEmpty
    @Size(min = 2, message="Post title should be at least 2 characters")
    private String title;

    @NotEmpty
    @Size(min = 10, message="Post title should be at least 10 characters")
    private String description;

    @NotEmpty
    private String content;
    private Set<CommentDto> comments;
}
