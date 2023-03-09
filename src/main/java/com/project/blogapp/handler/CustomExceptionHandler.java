package com.project.blogapp.handler;

import com.project.blogapp.dto.ErrorDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.LocalDateTime;

@ControllerAdvice
@Slf4j
public class CustomExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(Exception.class)
    public final ResponseEntity handleExceptions(Exception e, WebRequest request){
        LocalDateTime now = LocalDateTime.now();
        String message = e.getMessage();
        String endpoint = request.getDescription(false);
        ErrorDTO errorDetails = new ErrorDTO(now, message, endpoint);
        log.error("An error occured \nMessage: {}\nEndpoint: {}\nDate: {}", message, endpoint, now);
        return new ResponseEntity(errorDetails, HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
