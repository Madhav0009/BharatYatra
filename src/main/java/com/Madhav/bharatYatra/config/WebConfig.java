package com.Madhav.bharatYatra.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;


//STATIC FILE SERVING (for uploaded media)

@Configuration
public class WebConfig implements org.springframework.web.servlet.config.annotation.WebMvcConfigurer {

 @Value("${app.upload.dir}")
 private String uploadDir;

 @Override
 public void addResourceHandlers(
         org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry registry) {
     registry.addResourceHandler("/files/**")
         .addResourceLocations("file:" + System.getProperty("user.dir") + "/" + uploadDir);
 }
}


