package com.example.emos.wx.config;


import com.example.emos.wx.exception.EmosException;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authz.UnauthorizedException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class ExceptionAdvice {

    @ResponseBody //响应体
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)//用来捕获exception的异常,别忘了
    public String validExceptionHandler(Exception e){
         log.error("执行异常",e);
         if (e instanceof MethodArgumentNotValidException){//Spring validation框架的异常方法
             MethodArgumentNotValidException exception=(MethodArgumentNotValidException) e;
             return exception.getBindingResult().getFieldError().getDefaultMessage();
         }
         else if(e instanceof EmosException){
             EmosException exception=(EmosException) e;
             return exception.getMsg();
         }else if (e instanceof UnauthorizedException){
             return "你不具备相关权限";
         }else{
             return "后端执行异常";
         }
    }
}
