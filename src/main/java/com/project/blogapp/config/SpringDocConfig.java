package com.project.blogapp.config;

import com.project.blogapp.util.ReadJsonFileToJsonObject;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.examples.Example;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.responses.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;

import java.io.IOException;

@OpenAPIDefinition
@Configuration
@SecurityScheme(
        name = "token",
        type = SecuritySchemeType.HTTP,
        bearerFormat = "JWT",
        scheme = "bearer"
)
@SecurityScheme(
        name = "basicAuth",
        type = SecuritySchemeType.HTTP,
        scheme = "basic"
)
public class SpringDocConfig {

    @Autowired
    ReadJsonFileToJsonObject readJsonFileToJsonObject;

    @Bean
    public OpenAPI baseOpenAPI() throws IOException {


        ApiResponse genericErrorAPI = new ApiResponse().content(
                new Content().addMediaType(MediaType.APPLICATION_JSON_VALUE,
                        new io.swagger.v3.oas.models.media.MediaType().addExamples("default",
                                new Example().value(readJsonFileToJsonObject.read().get("genericErrorResponse").toString())))
        ).description("Internal Server Error!");

        ApiResponse badRequestResponseAPI = new ApiResponse().content(
                new Content().addMediaType(MediaType.APPLICATION_JSON_VALUE,
                        new io.swagger.v3.oas.models.media.MediaType().addExamples("default",
                                new Example().value(readJsonFileToJsonObject.read().get("badRequestResponse").toString())))
        ).description("Bad Request!");

        ApiResponse blogResponseAPI = new ApiResponse().content(
                new Content().addMediaType(MediaType.APPLICATION_JSON_VALUE,
                        new io.swagger.v3.oas.models.media.MediaType().addExamples("default",
                                new Example().value(readJsonFileToJsonObject.read().get("blogResponse").toString())))
        );

        ApiResponse blogListResponseAPI = new ApiResponse().content(
                new Content().addMediaType(MediaType.APPLICATION_JSON_VALUE,
                        new io.swagger.v3.oas.models.media.MediaType().addExamples("default",
                                new Example().value(readJsonFileToJsonObject.read().get("blogListResponse").toString())))
        );

        ApiResponse blogListSummaryResponseAPI = new ApiResponse().content(
                new Content().addMediaType(MediaType.APPLICATION_JSON_VALUE,
                        new io.swagger.v3.oas.models.media.MediaType().addExamples("default",
                                new Example().value(readJsonFileToJsonObject.read().get("blogListSummaryResponse").toString())))
        );

        Components components = new Components();
        components.addResponses("genericErrorAPI",genericErrorAPI);
        components.addResponses("badRequestResponseAPI",badRequestResponseAPI);
        components.addResponses("blogResponseAPI",blogResponseAPI);
        components.addResponses("blogListResponseAPI",blogListResponseAPI);
        components.addResponses("blogListSummaryResponseAPI",blogListSummaryResponseAPI);


        return new OpenAPI()
                .components(components)
                .info(new Info().title("Spring Doc").version("1.0.0").description("Spring doc"));
    }

}
