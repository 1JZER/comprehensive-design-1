package com.zuel.common.web;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.zuel.common.errorcode.BaseErrorCode;
import com.zuel.convention.result.Result;
import com.zuel.convention.result.Results;
import jakarta.servlet.http.HttpServletRequest;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.thymeleaf.util.StringUtils;

import java.util.Optional;

// 加参数指定Bean的名称
@Component
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    // 拦截参数验证异常
    @SneakyThrows
    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public Result validExceptionHandler(HttpServletRequest request, MethodArgumentNotValidException ex) {
        // 获取异常中的字段错误对象，可能有多个字段错误，这里只取第一个
        BindingResult bindingResult = ex.getBindingResult();
        FieldError firstFieldError = CollectionUtil.getFirst(bindingResult.getFieldErrors());
        // 从错误对象中提取错误消息，如果没有则返回空字符串
        String exceptionStr = Optional.ofNullable(firstFieldError)
                .map(FieldError::getDefaultMessage)
                .orElse(StrUtil.EMPTY);
        // 记录错误日志
        log.error("[{}] {} [ex] {}", request.getMethod(), getUrl(request), exceptionStr);
        // 返回一个封装了错误信息的Result对象
        return Results.failure(BaseErrorCode.CLIENT_ERROR.code(), exceptionStr);
    }

    // 捕获未拦截的异常
    @ExceptionHandler(value = Throwable.class)
    public Result defaultErrorHandler(HttpServletRequest request, Throwable throwable) {
        // 记录详细的错误日志
        log.error("[{}] {} ", request.getMethod(), getUrl(request), throwable);
        return Results.failure();
    }

    private String getUrl(HttpServletRequest request) {
        if (StringUtils.isEmpty(request.getQueryString())) {
            return request.getRequestURI().toString();
        }
        return request.getRequestURL().toString() + "?" + request.getQueryString();
    }
}
