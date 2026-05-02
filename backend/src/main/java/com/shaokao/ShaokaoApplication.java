package com.shaokao;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.shaokao.mapper")
public class ShaokaoApplication {
    public static void main(String[] args) {
        SpringApplication.run(ShaokaoApplication.class, args);
    }
}
