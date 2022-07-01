package com.kdt.instakyuram.config.log;

import java.util.Arrays;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class LogInterceptor implements HandlerInterceptor {

	private static final Logger logger = LoggerFactory.getLogger(LogInterceptor.class);
	private final ObjectMapper objectMapper;

	@Override
	public boolean preHandle(
		HttpServletRequest request,
		HttpServletResponse response,
		Object handler) throws Exception
	{
		String[] content = handler.toString().split("\\.");
		logger.warn("{} invoked", content[content.length - 1]);
		return true;
	}

	@Override
	public void afterCompletion(
		HttpServletRequest request,
		HttpServletResponse response,
		Object handler,
		Exception ex) throws Exception
	{
		ContentCachingRequestWrapper cachingRequest = (ContentCachingRequestWrapper) request;
		ContentCachingResponseWrapper cachingResponse = (ContentCachingResponseWrapper) response;

		String params = request.getParameterMap().entrySet().stream()
			.map(entry -> String.format("{\"%s\":\"%s\"", entry.getKey(), Arrays.toString(entry.getValue())))
			.collect(Collectors.joining(","));

		logger.warn("Params: {}", params);
		logger.warn("RequestBody: {} / ResponseBody: {}",
			objectMapper.readTree(cachingRequest.getContentAsByteArray()),
			objectMapper.readTree(cachingResponse.getContentAsByteArray())
		);
	}
}
