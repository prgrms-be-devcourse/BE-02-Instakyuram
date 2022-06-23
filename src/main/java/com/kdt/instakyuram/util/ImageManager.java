package com.kdt.instakyuram.util;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.FileSystemResource;
import org.springframework.web.multipart.MultipartFile;

import com.kdt.instakyuram.exception.InvalidFileException;
import com.kdt.instakyuram.exception.NotFoundException;

public class ImageManager {

	private ImageManager() {
	}

	private static final List<String> PERMISSION_TYPE = List.of("jpeg", "jpg", "gif", "png", "bmp");
	private static final Logger logger = LoggerFactory.getLogger(ImageManager.class);

	// TODO : 추후 커스텀 예외로 처리할 것
	public static void upload(MultipartFile image, String serverFileName, String path) {
		try {
			verifyDirectory(path);
			verifyType(serverFileName);
			verifyFile(image);

			image.transferTo(new File(path + serverFileName));
		} catch (
			IOException e) {
			logger.warn("이미지 파일을 저장하면서 오류가 발생하였습니다.");
			throw new RuntimeException("이미지 파일을 저장하면서 오류가 발생하였습니다.", e);
		}
	}

	public static FileSystemResource getFileResource(String path, String fileName) {
		return new FileSystemResource(path + fileName);
	}

	public static void delete(String path, String serverFileName) {
		File file = new File(path + serverFileName);
		if (!file.exists()) {
			return;
		}

		file.delete();
	}

	private static void verifyFile(MultipartFile image) {
		if (image == null && image.getSize() <= 0) {
			logger.warn("파일이 존재하지 않거나 사이즈가 0보다 작습니다.");
			throw new InvalidFileException("파일이 존재하지 않거나 사이즈가 0보다 작습니다.");
		}
	}

	private static void verifyDirectory(String path) {
		if (
			!Files.exists(Path.of(path))
		) {
			logger.warn("디렉토리를 찾을 수 없습니다. {}", path);
			throw new NotFoundException("디렉토리를 찾을 수 없습니다.");
		}
	}

	private static void verifyType(String serverFileName) {
		if (
			!PERMISSION_TYPE.contains(FilenameUtils.getExtension(serverFileName.toLowerCase()))
		) {
			logger.warn("지원하지 않는 파일 타입입니다. {}", serverFileName);
			throw new InvalidFileException("지원하지 않는 파일 타입입니다.");
		}
	}

}
