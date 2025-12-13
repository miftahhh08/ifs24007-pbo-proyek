package org.delcom.app.configs;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class MvcConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Logika: Jika ada request ke url /foto/..., arahkan ke folder uploads di komputer
        registry.addResourceHandler("/foto/")
                .addResourceLocations("file:./uploads/"); 
    }
}