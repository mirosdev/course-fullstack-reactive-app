package com.course.spring.reactive.rest.services.implementations;

import com.course.spring.reactive.rest.domain.Post;
import com.course.spring.reactive.rest.dto.PostDto;
import com.course.spring.reactive.rest.dto.PostsPaginatedResponse;
import com.course.spring.reactive.rest.exceptions.EditPermissionDeniedException;
import com.course.spring.reactive.rest.repository.PostReactiveRepository;
import com.course.spring.reactive.rest.services.interfaces.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileUrlResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.io.File;
import java.net.MalformedURLException;
import java.nio.file.Paths;
import java.util.stream.Collectors;

@Service
public class PostServiceImpl implements PostService {

    private String imagePathPrefix = "src/main/resources/images/";

    private final PostReactiveRepository postReactiveRepository;

    @Autowired
    public PostServiceImpl(PostReactiveRepository postReactiveRepository) {
        this.postReactiveRepository = postReactiveRepository;
    }

    // page index, size length
    @Override
    public Mono<PostsPaginatedResponse> findAll(Integer pageIndex, Integer pageSize) {
        return postReactiveRepository.count()
                .flatMap(aLong -> {
                    if(aLong != null && aLong > 0){
                        return postReactiveRepository.findAllBy(
                                PageRequest.of(pageIndex, pageSize))
                                .collect(Collectors.toList())
                                .flatMap(posts -> Mono.just(new PostsPaginatedResponse(aLong, posts)));
                    }
                    else {
                        System.out.println("in 2");
                        return Mono.just(new PostsPaginatedResponse(0L, null));
                    }
                });
    }

    @Override
    public Mono<Post> saveOrUpdate(PostDto postDto, String username) {

        // REFACTOR DUPLICATE CODE INTO LOCAL METHODS
        if(postDto.getId() != null){
            if(postDto.getUsername() != null && postDto.getUsername().equals(username)){
                if(postDto.getImage() != null){
                    return postReactiveRepository.findById(postDto.getId())
                            .flatMap(post -> {
                                if(post != null){
                                    File file = new File(imagePathPrefix + post.getImagePath().substring(37));
                                    if(file.delete()){
                                        System.out.println("File deleted successfully");
                                        return Mono.just(true);
                                    } else{
                                        System.out.println("Failed to delete the file");
                                        return Mono.just(false);
                                    }
                                } else {
                                    return Mono.just(false);
                                }
                            })
                            .flatMap(aBoolean -> postDto.getImage()
                                    .transferTo(Paths.get(imagePathPrefix + postDto.getImage().filename()))
                                    .then(Mono.just(true))
                                    .flatMap(aBoolean1 -> {
                                        Post post = new Post();
                                        post.setUsername(username);
                                        post.setTitle(postDto.getTitle());
                                        post.setContent(postDto.getContent());
                                        post.setImagePath("http://localhost:8080/api/post/image/" + postDto.getImage().filename());
                                        post.setId(postDto.getId());
                                        return postReactiveRepository.save(post);
                                    }));
                } else {
                    Post post = new Post();
                    post.setUsername(username);
                    post.setTitle(postDto.getTitle());
                    post.setContent(postDto.getContent());
                    if(postDto.getImagePath() != null){
                        post.setImagePath(postDto.getImagePath());
                    }
                    post.setId(postDto.getId());
                    return postReactiveRepository.save(post);
                }
            } else {
                return Mono.error(new EditPermissionDeniedException("Permission Denied"));
            }
        } else {
            return postDto.getImage()
                    .transferTo(Paths.get(imagePathPrefix + postDto.getImage().filename()))
                    .then(Mono.just(true))
                    .flatMap(aBoolean -> {
                        Post post = new Post();
                        post.setUsername(username);
                        post.setTitle(postDto.getTitle());
                        post.setContent(postDto.getContent());
                        post.setImagePath("http://localhost:8080/api/post/image/" + postDto.getImage().filename());
                        return postReactiveRepository.save(post);
                    });
        }
    }

    @Override
    public Mono<Boolean> deletePost(String id) {

        return postReactiveRepository.findById(id)
                .flatMap(post -> {
                    if(post != null){
                        return postReactiveRepository.findAllByImagePath(post.getImagePath())
                                .collect(Collectors.toList())
                                .flatMap(checkPost -> {
                                    if(checkPost.size() <= 1){
                                        File file = new File(imagePathPrefix + post.getImagePath().substring(37));
                                        if(file.delete()){
                                            System.out.println("File deleted successfully");
                                            return Mono.just(true);
                                        } else{
                                            System.out.println("Failed to delete the file");
                                            return Mono.just(false);
                                        }
                                    } else {
                                        System.out.println("Image file is still in use");
                                        return Mono.just(true);
                                    }
                                })
                                .flatMap(Mono::just);
                    } else {
                        // Throw exception
                        return Mono.just(false);
                    }
                })
                .flatMap(aBoolean -> postReactiveRepository.deleteById(id)
                        .then(Mono.just(true)));
    }

    @Override
    public Mono<Post> findByIdAndUsername(String postId, String username) {
        return postReactiveRepository.findByIdAndUsername(postId, username);
    }

    @Override
    public Mono<Resource> findImage(String imageFullName) throws MalformedURLException {
        return Mono.just(new FileUrlResource(imagePathPrefix + imageFullName));
    }
}
