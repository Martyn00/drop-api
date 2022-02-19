package com.configuration;

import com.foldermanipulation.FileService;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableWebMvc
@AllArgsConstructor
public class MvcConfiguration implements WebMvcConfigurer {
    private final FileService fileService;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry
                .addResourceHandler("/media/image/**")
                .addResourceLocations("file:" + fileService.BASIC_PATH + "/");
    }
}
