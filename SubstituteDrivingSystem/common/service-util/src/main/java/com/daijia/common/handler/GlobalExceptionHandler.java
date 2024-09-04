package com.daijia.common.handler;
import com.daijia.common.exeception.BusinessException;
import com.daijia.common.result.Result;
import com.daijia.common.result.ResultCodeEnum;
import feign.codec.DecodeException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Description： 全局异常处理
 *
 * @author: 段世超
 * @aate: Created in 2024/9/4 8:38
 */
@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(Exception.class)
    @ResponseBody
    public Result error(Exception e){

        e.printStackTrace();
        return Result.fail();
    }
    @ExceptionHandler(BusinessException.class)
    public Result error(BusinessException e){
        e.printStackTrace();
        return Result.build(null,e.getCode(),e.getMessage());
    }

    @ExceptionHandler(DecodeException.class)
    @ResponseBody
    public Result error(DecodeException e){
        e.printStackTrace();
        return Result.build(null,e.status(),  e.getMessage());
    }

    @ExceptionHandler({IllegalArgumentException.class})
    @ResponseBody
    public Result llegalArgumentException(Exception e) {
        e.printStackTrace();
        log.error("触发异常拦截: " + e.getMessage(), e);
        return Result.build(null, ResultCodeEnum.ARGUMENT_VALID_ERROR);
    }
    @ExceptionHandler(value = BindException.class)
    @ResponseBody
    public Result error(BindException exception){

        BindingResult result = exception.getBindingResult();
        HashMap<String, Object> errorMap = new HashMap<>();
        List<FieldError> fieldErrors = result.getFieldErrors();
        fieldErrors.forEach(error->{
            log.error("field: " + error.getField() + ", msg:" + error.getDefaultMessage());
            errorMap.put(error.getField(), error.getDefaultMessage());
        });
        return Result.build(errorMap,ResultCodeEnum.ARGUMENT_VALID_ERROR);

    }
    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    @ResponseBody
    public Result error(MethodArgumentNotValidException exception){

        BindingResult result = exception.getBindingResult();
        HashMap<String, Object> errorMap = new HashMap<>();
        List<FieldError> fieldErrors = result.getFieldErrors();
        fieldErrors.forEach(error->{
            log.error("field: " + error.getField() + ", msg:" + error.getDefaultMessage());
            errorMap.put(error.getField(), error.getDefaultMessage());
        });
        return  Result.build(errorMap,ResultCodeEnum.ARGUMENT_VALID_ERROR);
    }


}
