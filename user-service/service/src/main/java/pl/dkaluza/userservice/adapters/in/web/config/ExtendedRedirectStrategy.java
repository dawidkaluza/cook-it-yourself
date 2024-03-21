package pl.dkaluza.userservice.adapters.in.web.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.stereotype.Component;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.accept.ContentNegotiationStrategy;
import org.springframework.web.accept.HeaderContentNegotiationStrategy;
import org.springframework.web.context.request.ServletWebRequest;

import java.io.IOException;
import java.util.List;

@Component
class ExtendedRedirectStrategy implements RedirectStrategy {
    private final ContentNegotiationStrategy contentNegotiationStrategy;
    private final DefaultRedirectStrategy defaultRedirectStrategy;
    private final RestfulRedirectStrategy restfulRedirectStrategy;

    public ExtendedRedirectStrategy(DefaultRedirectStrategy defaultRedirectStrategy, ContentNegotiationStrategy contentNegotiationStrategy, RestfulRedirectStrategy restfulRedirectStrategy) {
        this.defaultRedirectStrategy = defaultRedirectStrategy;
        this.contentNegotiationStrategy = contentNegotiationStrategy;
        this.restfulRedirectStrategy = restfulRedirectStrategy;
    }

    @Autowired
    public ExtendedRedirectStrategy(RestfulRedirectStrategy restfulRedirectStrategy) {
        this(new DefaultRedirectStrategy(), new HeaderContentNegotiationStrategy(), restfulRedirectStrategy);
    }

    @Override
    public void sendRedirect(HttpServletRequest req, HttpServletResponse res, String url) throws IOException {
        List<MediaType> mediaTypes;
        try {
            mediaTypes = contentNegotiationStrategy.resolveMediaTypes(new ServletWebRequest(req));
        } catch (HttpMediaTypeNotAcceptableException e) {
            defaultRedirectStrategy.sendRedirect(req, res, url);
            return;
        }

        boolean isRestfulPreferred = false;
        for (var mediaType : mediaTypes) {
            if (mediaType.isCompatibleWith(MediaType.TEXT_HTML)) {
                break;
            }

            if (mediaType.isCompatibleWith(MediaType.APPLICATION_JSON)) {
                isRestfulPreferred = true;
                break;
            }
        }

        if (isRestfulPreferred) {
            restfulRedirectStrategy.sendRedirect(req, res, url);
        } else {
            defaultRedirectStrategy.sendRedirect(req, res, url);
        }
    }
}
