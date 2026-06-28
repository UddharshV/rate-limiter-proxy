package com.uddharsh.ratelimiterproxy.proxy;

import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClient;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

//Unit tests for ProxyService.

class ProxyServiceTest {

    @Test
    void forwardRequest_stripsProxyPrefixAndCallsUpstream() {
        // Arrange
        RestClient restClient = mock(RestClient.class);
        // RestClient.method(...) returns a RequestBodyUriSpec, so mock that chain
        RestClient.RequestBodyUriSpec methodSpec = mock(RestClient.RequestBodyUriSpec.class);
        RestClient.ResponseSpec responseSpec = mock(RestClient.ResponseSpec.class);

        HttpServletRequest request = mock(HttpServletRequest.class);

        when(request.getMethod()).thenReturn("GET");
        when(request.getRequestURI()).thenReturn("/proxy/api/users");

        when(restClient.method(HttpMethod.GET)).thenReturn(methodSpec);
        when(methodSpec.uri("/api/users")).thenReturn(methodSpec);
        when(methodSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.toEntity(String.class))
                .thenReturn(ResponseEntity.ok("[{\"id\":\"1\",\"name\":\"Alice\"}]"));

        ProxyService proxyService = new ProxyService(restClient);

        // Act
        ResponseEntity<String> result = proxyService.forwardRequest(request);

        // Assert
        assertThat(result.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(result.getBody()).isEqualTo("[{\"id\":\"1\",\"name\":\"Alice\"}]");

        // Verify that /proxy was stripped and GET /api/users was used
        verify(restClient).method(HttpMethod.GET);
        verify(methodSpec).uri("/api/users");
        verify(methodSpec).retrieve();
        verify(responseSpec).toEntity(String.class);
    }

    @Test
    void forwardRequest_usesDynamicHttpMethod() {
        // Arrange
        RestClient restClient = mock(RestClient.class);
        RestClient.RequestBodyUriSpec methodSpec = mock(RestClient.RequestBodyUriSpec.class);
        RestClient.ResponseSpec responseSpec = mock(RestClient.ResponseSpec.class);

        HttpServletRequest request = mock(HttpServletRequest.class);

        when(request.getMethod()).thenReturn("POST");
        when(request.getRequestURI()).thenReturn("/proxy/api/users");

        when(restClient.method(HttpMethod.POST)).thenReturn(methodSpec);
        when(methodSpec.uri("/api/users")).thenReturn(methodSpec);
        when(methodSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.toEntity(String.class))
                .thenReturn(ResponseEntity.status(201).body("created"));

        ProxyService proxyService = new ProxyService(restClient);

        // Act
        ResponseEntity<String> result = proxyService.forwardRequest(request);

        // Assert
        assertThat(result.getStatusCode().value()).isEqualTo(201);
        assertThat(result.getBody()).isEqualTo("created");

        // Verify POST was used
        verify(restClient).method(HttpMethod.POST);
        verify(methodSpec).uri("/api/users");
    }
    @Test
    void forwardRequest_relaysNon200StatusFromUpstream() {
        // Arrange
        RestClient restClient = mock(RestClient.class);
        RestClient.RequestBodyUriSpec methodSpec = mock(RestClient.RequestBodyUriSpec.class);
        RestClient.ResponseSpec responseSpec = mock(RestClient.ResponseSpec.class);

        HttpServletRequest request = mock(HttpServletRequest.class);

        // Incoming proxy request
        when(request.getMethod()).thenReturn("GET");
        when(request.getRequestURI()).thenReturn("/proxy/api/users");

        // RestClient chain
        when(restClient.method(HttpMethod.GET)).thenReturn(methodSpec);
        when(methodSpec.uri("/api/users")).thenReturn(methodSpec);
        when(methodSpec.retrieve()).thenReturn(responseSpec);

        // Upstream returns a 404
        when(responseSpec.toEntity(String.class))
                .thenReturn(ResponseEntity.status(404).body("not found"));

        ProxyService proxyService = new ProxyService(restClient);

        // Act
        ResponseEntity<String> result = proxyService.forwardRequest(request);

        // Assert
        assertThat(result.getStatusCode().value()).isEqualTo(404);
        assertThat(result.getBody()).isEqualTo("not found");

        // And verify the method + path used
        verify(restClient).method(HttpMethod.GET);
        verify(methodSpec).uri("/api/users");
        verify(methodSpec).retrieve();
        verify(responseSpec).toEntity(String.class);
    }
}
