package ch.admin.bag.covidcertificate.web.security;

import org.springframework.beans.factory.annotation.Value;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebFilter(urlPatterns = {"/v1/verify/*", "/v1/covidcertificate/*", "/v1/events/*", "/v1/authorization/*"})
public class HttpResponseHeaderFilter implements Filter {
    @Value("${cc-management-service.allowed-origin}")
    private String allowedOrigin;
    @Override
    public void doFilter(ServletRequest request, ServletResponse response,
                         FilterChain chain) throws IOException, ServletException {
        HttpServletResponse httpServletResponse = (HttpServletResponse) response;
        httpServletResponse.setHeader("Content-Security-Policy", "default-src 'self'");
        httpServletResponse.setHeader("Referrer-Policy", "strict-origin-when-cross-origin");
        httpServletResponse.setHeader("Feature-Policy", "microphone 'none'; payment 'none'; camera 'none'");
        httpServletResponse.setHeader("Access-Control-Allow-Origin", allowedOrigin);
        chain.doFilter(request, response);
    }
}
