package org.ademun.timetablebot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.retry.annotation.EnableRetry;

@SpringBootApplication
@EnableRetry
public class TimetableBotApplication {

  public static void main(String[] args) {
    SpringApplication.run(TimetableBotApplication.class, args);
  }
}
