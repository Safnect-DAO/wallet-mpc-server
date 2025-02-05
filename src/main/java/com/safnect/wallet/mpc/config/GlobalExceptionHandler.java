package com.safnect.wallet.mpc.config;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import com.safnect.wallet.mpc.dto.ResponseModel;

@ControllerAdvice
public class GlobalExceptionHandler {

    // 处理所有其他未捕获的异常
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleAllExceptions(Exception ex, WebRequest request) {
        ex.printStackTrace();
        return new ResponseEntity<>(ResponseModel.fail(ex.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}

