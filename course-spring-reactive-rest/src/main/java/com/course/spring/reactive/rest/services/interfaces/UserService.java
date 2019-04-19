package com.course.spring.reactive.rest.services.interfaces;

import com.course.spring.reactive.rest.domain.User;
import com.course.spring.reactive.rest.security.AuthRequest;
import reactor.core.publisher.Mono;

public interface UserService {
    Mono<User> findByUsername(String username);
    Mono<User> saveUser(User user);
    Mono<Boolean> signup(AuthRequest request);
}
