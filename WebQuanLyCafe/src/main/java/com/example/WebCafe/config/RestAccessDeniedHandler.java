package com.example.WebCafe.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * Trả JSON 403 khi đã đăng nhập nhưng sai vai trò / không đủ quyền.
 */
public class RestAccessDeniedHandler implements AccessDeniedHandler {

	private static final String BODY = "{\"error\":\"FORBIDDEN\",\"message\":\"Bạn không có quyền truy cập.\"}";

	@Override
	public void handle(HttpServletRequest request, HttpServletResponse response,
			AccessDeniedException accessDeniedException) throws IOException {
		response.setStatus(HttpServletResponse.SC_FORBIDDEN);
		response.setContentType(MediaType.APPLICATION_JSON_VALUE);
		response.setCharacterEncoding(StandardCharsets.UTF_8.name());
		response.getWriter().write(BODY);
	}
}
