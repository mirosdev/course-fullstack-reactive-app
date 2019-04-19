package com.course.spring.reactive.rest.services.implementations;

import com.course.spring.reactive.rest.domain.Role;
import com.course.spring.reactive.rest.domain.User;
import com.course.spring.reactive.rest.exceptions.UsernameAlreadyExistsException;
import com.course.spring.reactive.rest.repository.UserReactiveRepository;
import com.course.spring.reactive.rest.security.AuthRequest;
import com.course.spring.reactive.rest.security.PBKDF2Encoder;
import com.course.spring.reactive.rest.services.interfaces.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

import static com.course.spring.reactive.rest.domain.Role.ROLE_USER;

@Service
public class UserServiceImpl implements UserService {

    private final UserReactiveRepository userReactiveRepository;
    private final PBKDF2Encoder passwordEncoder;

    @Autowired
    public UserServiceImpl(UserReactiveRepository userReactiveRepository,
                           PBKDF2Encoder passwordEncoder) {
        this.userReactiveRepository = userReactiveRepository;
        this.passwordEncoder = passwordEncoder;
    }

//    //username:passwowrd -> user:user
//    private final String userUsername = "user";// password: user
//    private final User user = new User(userUsername, "cBrlgyL2GI2GINuLUUwgojITuIufFycpLG4490dhGtY=", Arrays.asList(ROLE_USER));
//
//    //username:passwowrd -> admin:admin
//    private final String adminUsername = "admin";// password: admin
//    private final User admin = new User(adminUsername, "dQNjUIMorJb8Ubj2+wVGYp6eAeYkdekqAcnYp+aRq5w=", Arrays.asList(Role.ROLE_ADMIN));


    @Override
    public Mono<User> findByUsername(String username) {

        return userReactiveRepository.findByUsername(username)
                .flatMap(user1 -> {
                    if(user1 != null){
                        return Mono.just(user1);
                    } else {
                        return Mono.empty();
                    }
                })
                .defaultIfEmpty(new User(null, null, null));
    }

    @Override
    public Mono<User> saveUser(User user) {
        return userReactiveRepository.save(user);
    }

    @Override
    public Mono<Boolean> signup(AuthRequest request) {

        return userReactiveRepository.findByUsername(request.getUsername())
                .flatMap(user1 -> {
                    if(user1 != null && user1.getUsername().equals(request.getUsername())){
                        return Mono.error(new UsernameAlreadyExistsException("E-Mail is already taken"));
                    } else {
                        return Mono.just(true);
                    }
                })
                .defaultIfEmpty(true)
                .flatMap(aBoolean -> {
                    if(aBoolean){
                        User user = new User();
                        user.setUsername(request.getUsername());
                        user.setPassword(passwordEncoder.encode(request.getPassword()));
                        List<Role> roles = new ArrayList<>();
                        roles.add(ROLE_USER);
                        user.setRoles(roles);
                        return userReactiveRepository.save(user)
                                .flatMap(user2 -> {
                                    if(user2.getUsername().equals(request.getUsername())){
                                        return Mono.just(true);
                                    } else {
                                        return Mono.just(false);
                                    }
                                });
                    } else {
                        return Mono.just(false);
                    }
                });
    }
}
