package com.course.spring.reactive.rest.dto;

import lombok.Data;
import lombok.ToString;
import org.springframework.http.codec.multipart.FilePart;

import javax.validation.constraints.NotEmpty;


@Data
@ToString
public class PostDto {
    private String id;
    @NotEmpty(message = "Title field must not be empty")
    private String title;
    @NotEmpty(message = "Content field must not be empty")
    private String content;
    private String username;
    private String imagePath;
    private FilePart image;
}
