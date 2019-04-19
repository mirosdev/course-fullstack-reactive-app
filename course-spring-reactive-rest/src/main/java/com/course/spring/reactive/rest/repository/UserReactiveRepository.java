package com.course.spring.reactive.rest.repository;

import com.course.spring.reactive.rest.domain.User;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Mono;

public interface UserReactiveRepository extends ReactiveMongoRepository<User, String> {
    Mono<User> findByUsername(String username);
}
