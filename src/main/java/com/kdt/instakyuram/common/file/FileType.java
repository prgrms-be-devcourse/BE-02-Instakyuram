package com.kdt.instakyuram.common.file;

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
			.filter(e -> e.toString().equalsIgnoreCase(extension))
			.findFirst()
			.orElseThrow(() -> new InvalidFileException("지원하지 않는 파일 타입입니다."));
	}

}
