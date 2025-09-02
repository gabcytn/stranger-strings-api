package com.gabcytn.strangerstrings;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class StrangerStringsApplication {
  public static void main(String[] args) {
    SpringApplication.run(StrangerStringsApplication.class, args);
  }
}
