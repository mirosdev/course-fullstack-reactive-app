package com.course.spring.reactive.rest.controllers;

import com.course.spring.reactive.rest.security.AuthRequest;
import com.course.spring.reactive.rest.security.AuthResponse;
import com.course.spring.reactive.rest.security.JWTUtil;
import com.course.spring.reactive.rest.security.PBKDF2Encoder;
import com.course.spring.reactive.rest.services.interfaces.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import static com.course.spring.reactive.rest.security.SecurityConstants.EXPIRATION;

@RestController
@RequestMapping("/api/auth")
public class AuthenticationREST {

    private final JWTUtil jwtUtil;
    private final PBKDF2Encoder passwordEncoder;
    private final UserService userService;

    @Autowired
    public AuthenticationREST(JWTUtil jwtUtil,
                              PBKDF2Encoder passwordEncoder,
                              UserService userService) {
        this.jwtUtil = jwtUtil;
        this.passwordEncoder = passwordEncoder;
        this.userService = userService;
    }

    @PostMapping("/login")
    public Mono<ResponseEntity<?>> login(@RequestBody AuthRequest request) {
        return userService
                .findByUsername(request.getUsername())
                .map(userDetails -> {
            if (passwordEncoder.encode(request.getPassword()).equals(userDetails.getPassword())) {
                return ResponseEntity.ok(new AuthResponse(jwtUtil.generateToken(userDetails),
                        Integer.parseInt(EXPIRATION), userDetails.getUsername()));
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Incorrect E-Mail or Password.");
            }
        }).defaultIfEmpty(ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Incorrect E-Mail or Password."));
    }

    @PostMapping("/signup")
    public Mono<Boolean> signup(@RequestBody AuthRequest request){
        return userService.signup(request);
    }
}
