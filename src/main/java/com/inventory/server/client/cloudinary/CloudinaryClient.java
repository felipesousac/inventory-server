package com.inventory.server.client.cloudinary;

import com.cloudinary.Cloudinary;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CloudinaryClient {

    @Value("${CLOUDINARY_URL}")
    private String cloudinaryUrl;

    @Bean
    public Cloudinary cloudinary() {
        return new Cloudinary(this.cloudinaryUrl);
    }
}
