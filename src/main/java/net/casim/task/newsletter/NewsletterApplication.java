package net.casim.task.newsletter;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springdoc.core.GroupedOpenApi;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@OpenAPIDefinition
public class NewsletterApplication {

    public static void main(String[] args) {
        SpringApplication.run(NewsletterApplication.class, args);
    }
    @Bean
    public GroupedOpenApi customOpenAPI() {
        return GroupedOpenApi.builder()
                .group("newsletter")
                .pathsToMatch("/subscriptions/**")
                .build();
    }

}
