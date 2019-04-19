package com.course.spring.reactive.rest.bootstrap;

import com.course.spring.reactive.rest.domain.Post;
import com.course.spring.reactive.rest.domain.Role;
import com.course.spring.reactive.rest.domain.User;
import com.course.spring.reactive.rest.repository.PostReactiveRepository;
import com.course.spring.reactive.rest.repository.UserReactiveRepository;
import com.course.spring.reactive.rest.security.PBKDF2Encoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Component
public class DbInit implements CommandLineRunner {

    private final PostReactiveRepository postReactiveRepository;
    private final UserReactiveRepository userReactiveRepository;
    private final PBKDF2Encoder passwordEncoder;

    @Autowired
    public DbInit(PostReactiveRepository postReactiveRepository,
                  UserReactiveRepository userReactiveRepository,
                  PBKDF2Encoder passwordEncoder) {
        this.postReactiveRepository = postReactiveRepository;
        this.userReactiveRepository = userReactiveRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        postReactiveRepository.deleteAll()
                .thenMany(postReactiveRepository.saveAll(posts()))
                .subscribe(
                        aVoid -> System.out.println("Initialized DB with new posts"),
                        throwable -> System.out.println(throwable.getMessage()));

        userReactiveRepository.deleteAll()
                .thenMany(userReactiveRepository.saveAll(users()))
                .subscribe(
                        user -> System.out.println("Initialized DB with new user: " + user),
                        throwable -> System.out.println(throwable.getMessage()));

        //Duplicate user save testing unique username
//        User user = new User();
//        user.setUsername("bob@test.com");
//
//        userReactiveRepository.save(user)
//                .subscribe(
//                        user1 -> System.out.println("sve ok"),
//                        throwable -> System.out.println(throwable.getMessage()));


    }

    private List<User> users() {

        List<Role> roles = new ArrayList<>();
        roles.add(Role.ROLE_USER);

        User user = new User();
        user.setUsername("bob@test.com");
        user.setId("bobId");
        user.setRoles(roles);
        user.setPassword(passwordEncoder.encode("password"));

        User user1 = new User();
        user1.setUsername("test@test.com");
        user1.setId("testId");
        user1.setRoles(roles);
        user1.setPassword(passwordEncoder.encode("password"));

        //Add new users below

        return Arrays.asList(
                user, user1
        );
    }

    private List<Post> posts(){
        Post post1 = new Post();
        post1.setId("idstr1");
        post1.setContent("All the nice weather, how awesome is that!");
        post1.setTitle("New Post - Spring is there!");
        post1.setUsername("bob@test.com");
        post1.setImagePath("http://localhost:8080/api/post/image/spring1.jpg");

        Post post2 = new Post();
        post2.setId("idstr2");
        post2.setContent("content example2");
        post2.setTitle("title example2");
        post2.setUsername("bob@test.com");
        post2.setImagePath("http://localhost:8080/api/post/image/spring2.jpg");

        return Arrays.asList(
                post1, post2
        );
    }
}
