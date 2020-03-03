package caramel.stratego.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class MvcConfig implements WebMvcConfigurer {

    // Controller logic abstracted in ViewControllerRegistry.
    // Normally we would create our own controller classes to handle requests
    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/").setViewName("login");
        registry.addViewController("/login").setViewName("login");
        registry.addViewController("/stratego-board").setViewName("stratego-board");
        registry.addViewController("/logout").setViewName("login");
        registry.addViewController("/replay").setViewName("replay");
        registry.addViewController("/about-us").setViewName("about-us");
    }



}
