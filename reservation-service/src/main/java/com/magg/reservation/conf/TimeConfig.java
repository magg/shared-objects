package com.magg.reservation.conf;

import java.util.TimeZone;
import javax.annotation.PostConstruct;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TimeConfig {

  @PostConstruct
  public void init() {
    // Setting Default timezone
    TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
  }

}
