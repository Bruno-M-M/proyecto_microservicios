package com.Gateway.Api_Gateway;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.server.mvc.filter.BeforeFilterFunctions;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@Component
@Order(1)
public class LoggingFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(LoggingFilter.class);

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        long inicio = System.currentTimeMillis();
        String metodo = request.getMethod();
        String path   = request.getRequestURI();

        log.info("[GATEWAY] >>> {} {}", metodo, path);

        filterChain.doFilter(request, response);

        long duracion = System.currentTimeMillis() - inicio;
        int status    = response.getStatus();

        log.info("[GATEWAY] <<< {} {} — {} ({} ms)", metodo, path, status, duracion);
    }
}