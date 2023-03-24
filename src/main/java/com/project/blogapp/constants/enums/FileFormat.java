package com.project.blogapp.constants.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum FileFormat {

    JPG("jpg"), PNG("png");

    final String format;

}
