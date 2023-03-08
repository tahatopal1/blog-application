package com.project.blogapp.dto;

import lombok.*;

import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BlogDTO {

    private Long id;
    private String title;
    private String content;
    private Set<String> tags;

}
