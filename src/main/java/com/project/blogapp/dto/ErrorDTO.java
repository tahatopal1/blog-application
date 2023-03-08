package com.project.blogapp.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ErrorDTO {

    private LocalDateTime timestamp;
    private String message;
    private String details;

}
