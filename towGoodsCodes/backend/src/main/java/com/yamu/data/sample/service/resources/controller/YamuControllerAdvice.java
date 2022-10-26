package com.yamu.data.sample.service.resources.controller;

import com.yamu.data.sample.common.exception.YamuException;
import com.yamu.data.sample.common.result.ErrorResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * @author Guopeng Teng
 * Date 2020-04-17
 */
@ControllerAdvice
@Slf4j
public class YamuControllerAdvice {

    @ExceptionHandler(value = NumberFormatException.class)
    public ResponseEntity numberFormatExceptionHandler(NumberFormatException ex) {
        log.error(ex.getMessage(), ex);
        return new ResponseEntity(new ErrorResult(ErrorResult.SYSTEM_FAILURE, "数据异常"), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(value = YamuException.class)
    public ResponseEntity yamuExceptionHandler(YamuException e) {
        log.error(e.getMessage(), e);
        return new ResponseEntity(new ErrorResult(ErrorResult.SYSTEM_FAILURE, e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(value = Exception.class)
    public ResponseEntity exceptionHandler(Exception ex) {
        log.error(ex.getMessage(), ex);
        return new ResponseEntity(new ErrorResult(ErrorResult.SYSTEM_FAILURE, "系统异常"), HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
