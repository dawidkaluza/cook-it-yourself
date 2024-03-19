package pl.dkaluza.userservice.adapters.in.web.config;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.security.web.util.UrlUtils;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.resource.PathResourceResolver;
import org.springframework.web.servlet.resource.ResourceResolverChain;

import java.util.List;

@Configuration
class WebMvcConfig implements WebMvcConfigurer {
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry
            .addResourceHandler("/**")
            .addResourceLocations("classpath:/static/")
            .resourceChain(true)
            .addResolver(new WebAppResourceResolver());
    }

    static class WebAppResourceResolver extends PathResourceResolver {
        @Override
        protected Resource resolveResourceInternal(HttpServletRequest request, String requestPath,
                                                   List<? extends Resource> locations, ResourceResolverChain chain) {
            // Give PathResourceResolver a chance to resolve a resource on its own.
            Resource resource = super.resolveResourceInternal(request, requestPath, locations, chain);
            if (resource == null) {
                // If resource wasn't found, use index.html file.
                resource = super.resolveResourceInternal(request, "/web/index.html", locations, chain);
            }
            return resource;
        }
    }
}
