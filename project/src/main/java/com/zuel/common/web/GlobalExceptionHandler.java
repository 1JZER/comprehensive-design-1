package com.zuel.common.web;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RestControllerAdvice;

// 加参数指定Bean的名称
@Component
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
}
