package com.project.blogapp.dto;

import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class BlogDTO extends BaseEntityDTO{

    private String title;
    private String content;
    private Set<String> tags;

}
