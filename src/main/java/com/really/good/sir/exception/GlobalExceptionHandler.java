package com.really.good.sir.exception;

import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RuntimeException.class)
    public void logException(RuntimeException ex, HttpServletRequest request) {
        System.err.println(
                "[ERROR] " + request.getMethod() + " " + request.getRequestURI()
                        + " -> " + ex.getMessage()
        );
        throw ex;
    }
}