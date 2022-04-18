package com.springboot.blog.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import com.springboot.blog.entity.Post;
import com.springboot.blog.exception.ResourceNotFoundException;
import com.springboot.blog.payload.PostDto;
import com.springboot.blog.payload.PostResponse;
import com.springboot.blog.repository.PostRepository;
import com.springboot.blog.service.PostService;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
public class PostServiceImpl implements PostService {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private ModelMapper mapper;

    @Override
    public PostDto createPost(PostDto postDto) {
        Post post = mapToEntity(postDto);

        Post newPost = postRepository.save(post);

        PostDto postResponse = mapToDto(newPost);
        return postResponse;
    }

    @Override
    public PostResponse getAllPosts(int pageNo, int pageSize, String sortBy, String sortDir) {

        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortBy).ascending()
            : Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(pageNo, pageSize, sort);

        Page<Post> postPage = postRepository.findAll(pageable);

        List<Post> listOfPosts = postPage.getContent();

        List<PostDto> listOfPostDtos = listOfPosts.stream()
            .map((post) -> mapToDto(post)).collect(Collectors.toList());

        PostResponse postResponse = new PostResponse();
        postResponse.setContent(listOfPostDtos);
        postResponse.setPageNo(pageNo);
        postResponse.setPageSize(pageSize);
        postResponse.setTotalElements(postPage.getTotalElements());
        postResponse.setTotalPages(postPage.getTotalPages());
        postResponse.setLast(postPage.isLast());

        return postResponse;
    }


    @Override
    public PostDto getPostById(Long id) {
        return mapToDto(postRepository.findById(id).orElseThrow(()-> new ResourceNotFoundException("Post", "id", id)));
    }

    
    @Override
    public PostDto updatePost(PostDto postDto, Long id) {
        Post postToUpdate = postRepository.findById(id)
            .orElseThrow(()-> new ResourceNotFoundException("Post", "id", id));
        
        postToUpdate.setTitle(postDto.getTitle());
        postToUpdate.setContent(postDto.getContent());
        postToUpdate.setDescription(postDto.getDescription());
        
        Post updatedPost = postRepository.save(postToUpdate);
        return mapToDto(updatedPost);
    }


    @Override
    public void deletePost(Long id) {
        Post post = postRepository.findById(id)
            .orElseThrow(()-> new ResourceNotFoundException("Post", "id", id));
        postRepository.delete(post);
    }

    
    private PostDto mapToDto(Post post) {
        PostDto postDto = mapper.map(post, PostDto.class);

        return postDto;
    }

    private Post mapToEntity(PostDto postDto) {
        Post post = mapper.map(postDto, Post.class);

        return post;
    }

}
