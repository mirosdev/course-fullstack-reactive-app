package com.course.spring.reactive.rest.security;

import com.course.spring.reactive.rest.domain.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.stereotype.Component;

import javax.crypto.spec.SecretKeySpec;
import java.io.Serializable;
import java.security.Key;
import java.util.*;

import static com.course.spring.reactive.rest.security.SecurityConstants.EXPIRATION;
import static com.course.spring.reactive.rest.security.SecurityConstants.SECRET;

@Component
public class JWTUtil implements Serializable{

    private static final long serialVersionUID = 1L;

    Claims getAllClaimsFromToken(String token) {
        return Jwts.parser().setSigningKey(Base64.getEncoder().encode(SECRET.getBytes())).parseClaimsJws(token).getBody();
    }

    String getUsernameFromToken(String token) {
        return getAllClaimsFromToken(token).getSubject();
    }

    private Date getExpirationDateFromToken(String token) {
        return getAllClaimsFromToken(token).getExpiration();
    }

    private Boolean isTokenExpired(String token) {
        final Date expiration = getExpirationDateFromToken(token);
        return expiration.before(new Date());
    }

    public String generateToken(User user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", user.getRoles());
        return doGenerateToken(claims, user.getUsername());
    }

    //We will sign our JWT with our ApiKey secret
    private byte[] apiKeySecretBytes = Base64.getEncoder().encode(SECRET.getBytes());
    private Key signingKey = new SecretKeySpec(apiKeySecretBytes, SignatureAlgorithm.HS512.getJcaName());

    private String doGenerateToken(Map<String, Object> claims, String username) {
        Long expirationTimeLong = Long.parseLong(EXPIRATION); //in second

        final Date createdDate = new Date();
        final Date expirationDate = new Date(createdDate.getTime() + expirationTimeLong * 1000);
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(username)
                .setIssuedAt(createdDate)
                .setExpiration(expirationDate)
                .signWith(signingKey, SignatureAlgorithm.HS512)
                .compact();
    }

    Boolean validateToken(String token) {
        return !isTokenExpired(token);
    }
}
