package com.project.blogapp.controller;

import com.project.blogapp.service.FileService;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/blog")
@AllArgsConstructor
@Tag(name = "File")
@Hidden
@Slf4j
public class FileController {

    private FileService fileService;

    @PostMapping("/{id}/file")
    public ResponseEntity<?> uploadFile(@RequestParam("image") MultipartFile file,
                                        @PathVariable(name = "id") Long blogId,
                                        @RequestParam(name = "scale", required = false) Double scale,
                                        @RequestParam(name = "quality", required = false) Float quality) throws Exception {
        fileService.uploadFile(file, blogId, scale, quality);
        return ResponseEntity.status(HttpStatus.OK)
                .build();
    }

    @GetMapping("/{id}/file/{fileName}")
    public ResponseEntity<?> downloadFile(@PathVariable Long id, @PathVariable String fileName) throws IOException {
        byte[] imageData=fileService.downloadFile(id, fileName);
        return ResponseEntity.status(HttpStatus.OK)
                .body(imageData);
    }

    @DeleteMapping("/{id}/file/{fileName}")
    public ResponseEntity deleteFile(@PathVariable Long id, @PathVariable String fileName){
        fileService.deleteFile(id, fileName);
        return ResponseEntity.status(HttpStatus.OK)
                .build();
    }

}
