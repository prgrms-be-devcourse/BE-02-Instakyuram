package com.kdt.instakyuram.util;

import static com.google.common.base.Preconditions.checkState;

import org.springframework.context.support.MessageSourceAccessor;

public class MessageUtils {

	private static MessageSourceAccessor messageSourceAccessor;

	public static String getMessage(String key) {
		checkState(messageSourceAccessor != null, "MessageSourceAccessor is not initialized.");
		return messageSourceAccessor.getMessage(key);
	}

	public static String getMessage(String key, Object... params) {
		checkState(messageSourceAccessor != null, "MessageSourceAccessor is not initialized.");
		return messageSourceAccessor.getMessage(key, params);
	}

	public static void setMessageSourceAccessor(MessageSourceAccessor messageSourceAccessor) {
		MessageUtils.messageSourceAccessor = messageSourceAccessor;
	}
}
