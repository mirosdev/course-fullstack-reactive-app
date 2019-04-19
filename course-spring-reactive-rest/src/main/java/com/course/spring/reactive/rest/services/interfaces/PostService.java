package com.course.spring.reactive.rest.services.interfaces;

import com.course.spring.reactive.rest.domain.Post;
import com.course.spring.reactive.rest.dto.PostDto;
import com.course.spring.reactive.rest.dto.PostsPaginatedResponse;
import org.springframework.core.io.Resource;
import reactor.core.publisher.Mono;

import java.net.MalformedURLException;


public interface PostService {
    Mono<PostsPaginatedResponse> findAll(Integer pageIndex, Integer pageSize);
    Mono<Post> saveOrUpdate(PostDto postDto, String username);
    Mono<Boolean> deletePost(String id);
    Mono<Post> findByIdAndUsername(String postId, String username);
    Mono<Resource> findImage(String imageFullName) throws MalformedURLException;
}
