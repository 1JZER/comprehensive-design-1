package com.zuel;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.zuel.dao.mapper")
public class ComprehensiveApplication {
    public static void main(String[] args) {
        SpringApplication.run(ComprehensiveApplication.class, args);
    }
}
