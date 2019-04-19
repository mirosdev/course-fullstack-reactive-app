package com.course.spring.reactive.rest.domain;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotEmpty;

@Data
@Document
public class Post {

    @Id
    private String id;
    @NotEmpty(message = "Title field must not be empty")
    private String title;
    @NotEmpty(message = "Content field must not be empty")
    private String content;
    private String username;
    private String imagePath;
}
