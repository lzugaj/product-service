package com.luv2code.productservice.security;

import com.luv2code.productservice.rest.handler.ApiErrorResponse;
import com.luv2code.productservice.util.ClockUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Collections;

@Component
public class ApiKeyFilter extends OncePerRequestFilter {

    private final String expectedApiKey;
    private final ObjectMapper objectMapper;

    public ApiKeyFilter(
            @Value("${product-service.security.api-key}") String expectedApiKey,
            @Qualifier("jacksonJsonMapper") ObjectMapper objectMapper
    ) {
        this.expectedApiKey = expectedApiKey;
        this.objectMapper = objectMapper;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        String clientKey = request.getHeader("X-API-KEY");

        if (expectedApiKey.equals(clientKey)) {
            var authentication = new UsernamePasswordAuthenticationToken(
                    "API_USER",
                    null,
                    Collections.emptyList()
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);

            filterChain.doFilter(request, response);
        } else {
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.setCharacterEncoding("UTF-8");

            ApiErrorResponse apiError = new ApiErrorResponse(
                    LocalDateTime.now(ClockUtil.getClock()),
                    HttpStatus.UNAUTHORIZED,
                    "Unauthorized: Invalid or missing X-API-KEY",
                    "security.unauthorized.message",
                    request.getRequestURI()
            );

            String jsonResponse = objectMapper.writeValueAsString(apiError);

            response.setContentLength(jsonResponse.getBytes(StandardCharsets.UTF_8).length);

            java.io.PrintWriter writer = response.getWriter();
            writer.write(jsonResponse);
            writer.flush();
            writer.close();
        }
    }
}