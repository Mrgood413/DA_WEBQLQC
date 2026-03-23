package com.example.WebCafe.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * Trả JSON 401 khi chưa đăng nhập mà gọi API cần phiên.
 */
public class RestAuthenticationEntryPoint implements AuthenticationEntryPoint {

	private static final String BODY = "{\"error\":\"UNAUTHORIZED\",\"message\":\"Vui lòng đăng nhập.\"}";

	@Override
	public void commence(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException authException) throws IOException {
		response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
		response.setContentType(MediaType.APPLICATION_JSON_VALUE);
		response.setCharacterEncoding(StandardCharsets.UTF_8.name());
		response.getWriter().write(BODY);
	}
}
