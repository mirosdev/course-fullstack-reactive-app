package com.course.spring.reactive.rest.dto;

import com.course.spring.reactive.rest.domain.Post;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PostsPaginatedResponse {
    private Long totalItemsCount;
    private List<Post> posts;
}
