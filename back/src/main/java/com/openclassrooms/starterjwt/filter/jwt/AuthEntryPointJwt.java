package com.openclassrooms.starterjwt.filter.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class AuthEntryPointJwt implements AuthenticationEntryPoint {

	private final MessageSource messageSource;

	private final ObjectMapper objectMapper;

	@Override
	public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException {
		log.error("Unauthorized error: {} - path={}", authException.getMessage(), request.getServletPath());

		response.setContentType(MediaType.APPLICATION_JSON_VALUE);
		response.setCharacterEncoding("UTF-8");
		response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
		response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate, proxy-revalidate");

		final Map<String, Object> body = new HashMap<>();
		body.put("timestamp", java.time.Instant.now().toString());
		body.put("status", HttpServletResponse.SC_UNAUTHORIZED);
		body.put("error", "Unauthorized");
		String message = messageSource.getMessage("error.security.unauthenticated", null, LocaleContextHolder.getLocale());
		body.put("message", message);
		body.put("path", request.getServletPath());
		body.put("clientIp", request.getRemoteAddr());

		objectMapper.writeValue(response.getOutputStream(), body);
	}

}
