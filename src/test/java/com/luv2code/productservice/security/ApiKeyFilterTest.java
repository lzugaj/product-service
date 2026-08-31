package com.luv2code.productservice.security;

import com.luv2code.productservice.rest.handler.ApiErrorResponse;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ApiKeyFilterTest {

    private static final String VALID_API_KEY = "super-secret-key-123";
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private FilterChain filterChain;

    private MockHttpServletRequest request;
    private MockHttpServletResponse response;

    private ApiKeyFilter apiKeyFilter;

    @BeforeEach
    void setUp() {
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
        apiKeyFilter = new ApiKeyFilter(VALID_API_KEY, objectMapper);
    }

    @Test
    void shouldProceedWhenApiKeyIsValid() throws ServletException, IOException {
        request.addHeader("X-API-KEY", VALID_API_KEY);

        apiKeyFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain, times(1)).doFilter(request, response);

        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNotNull();
        assertThat(SecurityContextHolder.getContext().getAuthentication().getPrincipal()).isEqualTo("API_USER");

        SecurityContextHolder.clearContext();
    }

    @Test
    void shouldReturnUnauthorizedWhenApiKeyIsInvalid() throws ServletException, IOException {
        request.addHeader("X-API-KEY", "wrong-key");
        request.setRequestURI("/api/v1/products");

        apiKeyFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain, never()).doFilter(any(), any());

        assertThat(response.getStatus()).isEqualTo(HttpStatus.UNAUTHORIZED.value());
        assertThat(response.getContentType()).isEqualTo("application/json;charset=UTF-8");

        String content = response.getContentAsString();
        ApiErrorResponse apiError = objectMapper.readValue(content, ApiErrorResponse.class);

        assertThat(apiError).isNotNull();
        assertThat(apiError.httpStatus()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(apiError.message()).isEqualTo("Unauthorized: Invalid or missing X-API-KEY");
        assertThat(apiError.messageKey()).isEqualTo("security.unauthorized.message");
        assertThat(apiError.path()).isEqualTo("/api/v1/products");
        assertThat(apiError.timestamp()).isNotNull();
    }

    @Test
    void shouldReturnUnauthorizedWhenApiKeyHeaderIsMissing() throws ServletException, IOException {
        request.setRequestURI("/api/v1/products");

        apiKeyFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain, never()).doFilter(any(), any());
        assertThat(response.getStatus()).isEqualTo(HttpStatus.UNAUTHORIZED.value());

        String content = response.getContentAsString();
        ApiErrorResponse apiError = objectMapper.readValue(content, ApiErrorResponse.class);
        assertThat(apiError.message()).contains("Invalid or missing X-API-KEY");
    }
}