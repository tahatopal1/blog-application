package com.project.blogapp.dto;

import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class BaseEntityDTO {

    private long id;
    private Date createdDate;
    private Date lastModifiedDate;

}
