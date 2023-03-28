package com.project.blogapp;

import com.project.blogapp.util.FileUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FileUtilsTest {

    private FileUtils fileUtils;

    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp() {
        fileUtils = new FileUtils();
    }

    @Test
    void saveImage_withScaleAndQuality() throws IOException {
        BufferedImage originalImage = new BufferedImage(100, 100, BufferedImage.TYPE_INT_RGB);
        File originalFile = tempDir.resolve("originalImage.jpg").toFile();
        ImageIO.write(originalImage, "jpg", originalFile);

        FileInputStream fis = new FileInputStream(originalFile);
        MultipartFile multipartFile = new MockMultipartFile("file", "originalImage.jpg", "image/jpeg", fis);

        Double scale = 0.5;
        Float quality = 0.5f;

        File resultFile = fileUtils.saveImage(multipartFile, scale, quality);

        BufferedImage resultImage = ImageIO.read(resultFile);
        assertEquals(originalImage.getWidth() * scale, resultImage.getWidth());
        assertEquals(originalImage.getHeight() * scale, resultImage.getHeight());
        assertTrue(resultFile.length() < originalFile.length());
    }

}
