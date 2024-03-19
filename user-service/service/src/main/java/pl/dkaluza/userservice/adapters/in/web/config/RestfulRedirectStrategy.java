package pl.dkaluza.userservice.adapters.in.web.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
class RestfulRedirectStrategy implements RedirectStrategy {
    @Override
    public void sendRedirect(HttpServletRequest req, HttpServletResponse res, String url) throws IOException {
        res.resetBuffer();
        res.setStatus(HttpServletResponse.SC_OK);
        res.setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);

        res.getWriter()
            .append("{\"redirectUrl\": \"")
            .append(url)
            .append("\"}");
        res.flushBuffer();
    }
}
