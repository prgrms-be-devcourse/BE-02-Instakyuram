package com.kdt.instakyuram.util;

import java.util.Arrays;

import com.kdt.instakyuram.common.file.exception.InvalidFileException;

public enum FileType {
	JPEG,
	JPG,
	GIF,
	PNG,
	BMP;

	public static void verifyType(String extension) {
		Arrays.stream(FileType.values())
			.map(Enum::name)
			.filter(e -> e.toString().equals(extension.toUpperCase()))
			.findFirst()
			.orElseThrow(() -> new InvalidFileException("지원하지 않는 파일 타입입니다."));
	}

}
