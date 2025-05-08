package org.example.exception;

import org.example.common.Result;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

@ControllerAdvice("org.example.controller")
public class GlobalExceptionHander {

    @ExceptionHandler(Exception.class)
    @ResponseBody
    public Result error(Exception e) {
        e.printStackTrace();
        return Result.error();
    }

    @ExceptionHandler(CustomException.class)
    @ResponseBody
    public Result error(CustomException e) {
        return Result.error(e.getCode(),e.getMessage());
    }

}
