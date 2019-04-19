package com.course.spring.reactive.rest.repository;

import com.course.spring.reactive.rest.domain.Post;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface PostReactiveRepository extends ReactiveMongoRepository<Post, String> {
    Mono<Post> findByIdAndUsername(String id, String username);
    Flux<Post> findAllBy(Pageable pageable);
    Flux<Post> findAllByImagePath(String imagePath);
}
