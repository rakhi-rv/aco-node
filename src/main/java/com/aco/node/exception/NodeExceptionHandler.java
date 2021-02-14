package com.aco.node.exception;

import com.aco.node.models.ApiError;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class NodeExceptionHandler {

    @ExceptionHandler(value = DataNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiError handleDataNotFoundException(DataNotFoundException exception) {
        return new ApiError(exception.getMessage());
    }

    @ExceptionHandler(value = {BadRequestException.class, IllegalArgumentException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleBadRequestException(BadRequestException exception) {
        return new ApiError(exception.getMessage());
    }
}
