package com.project.blogapp.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.FullTextField;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.Indexed;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "tag")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@Indexed(enabled = false)
public class Tag extends BaseEntity {

    @FullTextField
    private String tag_name;

    @ManyToMany(mappedBy = "tags")
    private Set<Blog> blogs = new HashSet<>();

}
