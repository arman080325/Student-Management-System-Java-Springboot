package online.armanportfolio.sms.config;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Adds baseline security response headers to every request.
 * Deliberately dependency-free (no Spring Security) to keep the demo simple
 * while still demonstrating a hardened response surface.
 *
 * The strict Content-Security-Policy is skipped for the Swagger UI paths,
 * which need their own inline bootstrap.
 */
@Component
@Order(1)
public class SecurityHeadersFilter implements Filter {

    private static final String CSP =
            "default-src 'self'; " +
            "script-src 'self'; " +
            "style-src 'self' 'unsafe-inline' https://fonts.googleapis.com; " +
            "font-src 'self' https://fonts.gstatic.com; " +
            "img-src 'self' data:; " +
            "connect-src 'self'; " +
            "base-uri 'self'; " +
            "frame-ancestors 'none'; " +
            "form-action 'self'";

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {

        HttpServletResponse response = (HttpServletResponse) res;
        String path = ((HttpServletRequest) req).getRequestURI();

        response.setHeader("X-Content-Type-Options", "nosniff");
        response.setHeader("X-Frame-Options", "DENY");
        response.setHeader("Referrer-Policy", "no-referrer");
        response.setHeader("Permissions-Policy", "geolocation=(), microphone=(), camera=()");

        boolean isDocs = path.startsWith("/swagger-ui") || path.startsWith("/v3/api-docs");
        if (!isDocs) {
            response.setHeader("Content-Security-Policy", CSP);
        }

        chain.doFilter(req, res);
    }
}
