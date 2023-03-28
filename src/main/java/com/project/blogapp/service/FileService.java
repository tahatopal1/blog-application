package com.project.blogapp.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface FileService {

    void uploadFile(MultipartFile file, Long blogId, Double scale, Float quality) throws Exception;

    byte[] downloadFile(Long id, String fileName) throws IOException;

    void deleteFile(Long id, String fileName);

}
