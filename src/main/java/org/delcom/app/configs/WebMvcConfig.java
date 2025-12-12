package org.delcom.app.configs;

import org.delcom.app.interceptors.AuthInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.*;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
    
    @Autowired 
    private AuthInterceptor authInterceptor;
    
    @Override 
    public void addInterceptors(InterceptorRegistry r) { 
        r.addInterceptor(authInterceptor)
         .addPathPatterns("/**")
         .excludePathPatterns("/", "/auth/**", "/css/**", "/js/**", "/uploads/**", "/error", "/favicon.ico"); // <-- TAMBAHAN: "/" dikecualikan
    }
    
    @Override 
    public void addResourceHandlers(ResourceHandlerRegistry r) { 
        r.addResourceHandler("/uploads/**")
         .addResourceLocations("file:./uploads/"); 
    }
}