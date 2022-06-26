package com.kdt.instakyuram.config;

import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.MessageSourceAccessor;

import com.kdt.instakyuram.util.MessageUtils;

@Configuration
public class ServiceConfig {

	@Bean
	public MessageSourceAccessor messageSourceAccessor(MessageSource messageSource) {
		MessageSourceAccessor messageSourceAccessor = new MessageSourceAccessor(messageSource);
		MessageUtils.setMessageSourceAccessor(messageSourceAccessor);
		return messageSourceAccessor;
	}
}
