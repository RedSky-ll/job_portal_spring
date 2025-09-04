package ir.parsakav.jobportal.config;

// ir/parsakav/jobportal/config/WebMvcConfig.java

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Value("${app.uploads.root:uploads}")
    private String uploadsRoot;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String location;
        if (uploadsRoot.startsWith("/") || uploadsRoot.matches("^[A-Za-z]:\\\\.*")) {
            // مسیر مطلق
            location = "file:" + (uploadsRoot.endsWith("/") ? uploadsRoot : uploadsRoot + "/");
        } else {
            // مسیر نسبی نسبت به working dir
            location = "file:" + System.getProperty("user.dir") + "/" + uploadsRoot + "/";
        }

        registry.addResourceHandler("/uploads/**")
                .addResourceLocations(location)
                .setCachePeriod(3600); // 1h cache
    }
}

