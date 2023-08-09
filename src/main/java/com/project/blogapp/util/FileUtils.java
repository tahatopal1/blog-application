package com.project.blogapp.util;

import com.project.blogapp.constants.enums.FileConstant;
import com.project.blogapp.constants.enums.FileFormat;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.Objects;

@Component
@AllArgsConstructor
public class FileUtils {


    public File saveImage(MultipartFile file, Double scale, Float quality) throws IOException {

        String[] fileTypeFormat = file.getContentType().split("/");
        String fileType = fileTypeFormat[0];
        String fileFormat = fileTypeFormat[1];

        byte[] bytes = file.getBytes();

        if (Objects.nonNull(scale) && fileType.equals(FileConstant.IMAGE.getFileType())) {
            bytes = this.scaleImage(bytes, fileFormat, scale);
        }
        if (Objects.nonNull(quality) && fileFormat.equals(FileFormat.JPG.getFormat())) {
            bytes = this.qualifyImage(bytes, fileFormat, quality);
        }

        File convertedFile = this.convertFileS3Persistance(file, bytes);
        return convertedFile;
    }

    private byte[] scaleImage(byte[] bytes, String imageFormat, Double scale) throws IOException {
        ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
        BufferedImage originalImage = ImageIO.read(bis);
        BufferedImage resizedImage = new BufferedImage((int) (originalImage.getWidth() * scale),
                (int) (originalImage.getHeight() * scale), originalImage.getType());

        Graphics2D g2d = resizedImage.createGraphics();
        g2d.drawImage(originalImage, 0, 0, (int) (originalImage.getWidth() * scale),
                (int) (originalImage.getHeight() * scale),
                null);
        g2d.dispose();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(resizedImage, imageFormat, baos);
        baos.close();
        return baos.toByteArray();
    }

    private byte[] qualifyImage(byte[] bytes, String imageFormat, Float quality) throws IOException {
        ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
        BufferedImage originalImage = ImageIO.read(bis);

        ImageWriter jpgWriter = ImageIO.getImageWritersByFormatName(imageFormat).next();

        ImageWriteParam jpgWriteParam = jpgWriter.getDefaultWriteParam();
        jpgWriteParam.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
        jpgWriteParam.setCompressionQuality(quality);

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try (ImageOutputStream imageOutputStream = ImageIO.createImageOutputStream(byteArrayOutputStream)) {
            jpgWriter.setOutput(imageOutputStream);
            jpgWriter.write(null, new javax.imageio.IIOImage(originalImage, null, null), jpgWriteParam);
        } finally {
            jpgWriter.dispose();
        }
        bytes = byteArrayOutputStream.toByteArray();
        return bytes;
    }

    private java.io.File convertFileS3Persistance(MultipartFile file, byte[] bytes) throws IOException {
        java.io.File convertedFile = new java.io.File(file.getOriginalFilename());
        FileOutputStream fos = new FileOutputStream(convertedFile);
        fos.write(bytes);
        return convertedFile;
    }


}
