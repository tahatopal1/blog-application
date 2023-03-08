package com.project.blogapp.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "tag")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class Tag extends BaseEntity{

    private String tag_name;

/*    @ManyToMany(mappedBy = "tags")
    private Set<Blog> blogs = new HashSet<>();*/

}
