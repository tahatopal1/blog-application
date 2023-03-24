package com.project.blogapp.constants.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum FileConstant {

    IMAGE("image"),
    VIDEO("video");

    final String fileType;


}
