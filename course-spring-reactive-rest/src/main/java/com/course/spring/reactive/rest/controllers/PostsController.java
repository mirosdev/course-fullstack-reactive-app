package com.course.spring.reactive.rest.controllers;

import com.course.spring.reactive.rest.domain.Post;
import com.course.spring.reactive.rest.dto.PostDto;
import com.course.spring.reactive.rest.dto.PostsPaginatedResponse;
import com.course.spring.reactive.rest.exceptions.EditPermissionDeniedException;
import com.course.spring.reactive.rest.services.interfaces.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.net.MalformedURLException;
import java.security.Principal;


@RestController
@RequestMapping("/api/post")
public class PostsController {

    private final PostService postService;

    @Autowired
    public PostsController(PostService postService) {
        this.postService = postService;
    }

    @GetMapping("/all/{pageIndex}/{pageSize}")
    public Mono<PostsPaginatedResponse> findAllPosts(@PathVariable Integer pageIndex,
                                                     @PathVariable Integer pageSize) {
        return postService.findAll(pageIndex, pageSize);
    }

    @GetMapping("/single/{postId}")
//    @PreAuthorize("hasRole('USER')")
    public Mono<Post> findPostById(@PathVariable String postId, Principal principal){
        return postService.findByIdAndUsername(postId, principal.getName());
    }

    @PostMapping(value = "/save", consumes = "multipart/form-data")
    @PreAuthorize("hasRole('USER')")
    public Mono<Post> saveOrUpdatePostMultipart(@ModelAttribute PostDto postDto, Principal principal) {
        return postService.saveOrUpdate(postDto, principal.getName());
    }

    @PostMapping(value = "/save", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @PreAuthorize("hasRole('USER')")
    public Mono<Post> saveOrUpdatePost(@RequestBody PostDto postDto, Principal principal) {
        return postService.saveOrUpdate(postDto, principal.getName());
    }

    @DeleteMapping("/delete/{postId}/{username}")
//    @PreAuthorize("hasRole('USER')")
    public Mono<Boolean> deletePost(@PathVariable String postId,
                                    @PathVariable String username,
                                    Principal principal){
        if(username.equals(principal.getName())){
            return postService.deletePost(postId);
        } else {
            return Mono.error(new EditPermissionDeniedException("Permission Denied"));
        }
    }

    @GetMapping(value = "/image/{imageFullName}", produces = MimeTypeUtils.IMAGE_PNG_VALUE)
    public Mono<Resource> getImageFile(@PathVariable String imageFullName) throws MalformedURLException {
        return postService.findImage(imageFullName);
    }
}
