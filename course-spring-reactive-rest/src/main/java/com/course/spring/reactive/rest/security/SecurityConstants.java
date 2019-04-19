package com.course.spring.reactive.rest.security;

public class SecurityConstants {
    static final String SECRET = "ThisIsSecretForJWTHS512SignatureAlgorithmThatMUSTHave512bitsKeySize";
    public static final String EXPIRATION = "28800";
    static final String PASSWORD_SECRET = "mysecret";
    static final Integer ITERATION = 33;
    static final Integer KEYLENGTH = 256;
}
