package com.kdt.instakyuram.common.file;

public enum ResourcePath {
	POST("/posts"), PROFILE("/profiles");

	private final String prefix;

	ResourcePath(String prefixPath) {
		this.prefix = prefixPath;
	}

	public String getPrefix() {
		return prefix;
	}

}
