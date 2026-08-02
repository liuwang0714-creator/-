//package org.example.sotokenspringboot.handier;
//
//
//import org.example.sotokenspringboot.pojo.Result;
//import org.springframework.web.bind.annotation.ExceptionHandler;
//import org.springframework.web.bind.annotation.RestControllerAdvice;
//
//@RestControllerAdvice
//public class GlobalExceptionHandler {
//    // 全局异常拦截
//    @ExceptionHandler
//    public Result handlerException(Exception e) {
//        e.printStackTrace();
//        return Result.error(e.getMessage());
//    }
//}
//


package org.example.sotokenspringboot.handier;


import org.example.sotokenspringboot.pojo.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@RestControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(NoResourceFoundException.class)
    public Result handleNoResourceFoundException(NoResourceFoundException e) {
        log.warn("静态资源未找到: {}", e.getResourcePath());
        return Result.error("资源不存在");
    }

    @ExceptionHandler
    public Result handlerException(Exception e) {
        e.printStackTrace();
        return Result.error(e.getMessage());
    }
}
