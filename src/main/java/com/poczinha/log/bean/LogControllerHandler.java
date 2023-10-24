package com.poczinha.log.bean;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.HttpStatusCodeException;

public class LogControllerHandler {

    @ExceptionHandler(HttpStatusCodeException.class)
    public ResponseEntity<String> handleHttpStatusCodeException(HttpStatusCodeException e) {
        return ResponseEntity.status(e.getStatusCode()).body(e.getMessage());
    }
}
