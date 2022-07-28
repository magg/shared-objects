package com.magg.reservation.conf;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import java.util.Arrays;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
@ConditionalOnProperty(
    prefix = "springdoc.swagger-ui",
    name = "enabled",
    havingValue = "true",
    matchIfMissing = true)
public class SwaggerConfig {
  private static final String APP_VERSION = "1.0.0";
  private static final String APP_DESCRIPTION = "Scheduler Service";
  private static final String APP_TITLE = APP_DESCRIPTION + " API";
  private static final String TERMS_OF_SERVICE = "http://swagger.io/terms/";
  private static final String LICENSE = "Apache 2.0";
  private static final String SPRING_DOC_URL = "http://springdoc.org";

  @Value("${swagger.env.url}")
  private String serverUrl;

  /**
   * Swagger config.
   *
   * @return OpenAPI
   */
  @Bean
  public OpenAPI customOpenApi() {
    return new OpenAPI()
        .addServersItem(new Server().url(serverUrl))
        .info(new Info()
            .title(APP_TITLE)
            .version(APP_VERSION)
            .description(APP_DESCRIPTION)
            .termsOfService(TERMS_OF_SERVICE)
            .license(new License().name(LICENSE).url(SPRING_DOC_URL)));
  }

  /**
   * CORS filter.
   *
   * @return CorsFilter
   */
  @Bean
  public CorsFilter corsFilter() {
    // Allow anyone and anything access. Probably ok for Swagger spec
    CorsConfiguration config = new CorsConfiguration();
    config.setAllowCredentials(false);
    config.addAllowedOrigin("*");
    config.addAllowedHeader("*");
    config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "OPTIONS", "DELETE", "PATCH"));
    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", config);
    return new CorsFilter(source);
  }
}
