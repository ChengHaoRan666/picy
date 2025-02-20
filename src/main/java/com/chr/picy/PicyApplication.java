package com.chr.picy;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@Slf4j
public class PicyApplication {

    public static void main(String[] args) {
        SpringApplication.run(PicyApplication.class, args);
        log.info("service...");
    }
}
