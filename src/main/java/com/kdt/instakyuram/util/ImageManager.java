package com.kdt.instakyuram.util;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.FileSystemResource;
import org.springframework.web.multipart.MultipartFile;

import com.kdt.instakyuram.post.domain.PostImage;

public class ImageManager {

	private ImageManager() {
	}

	private static final Logger logger = LoggerFactory.getLogger(ImageManager.class);
	private static final String DEFAULT_PATH = System.getProperty("user.dir") + "/picture/";

	public static void uploads(Set<Map.Entry<PostImage, MultipartFile>> entries) {
		List<String> files = new ArrayList<>();

		entries.forEach(entry -> {
				PostImage postImage = entry.getKey();
				MultipartFile file = entry.getValue();

				try {
					FileType.verifyType(FilenameUtils.getExtension(postImage.getServerFileName().toUpperCase()));

					file.transferTo(new File(postImage.getPath() + postImage.getServerFileName()));
					files.add(postImage.getPath() + postImage.getServerFileName());
				} catch (IOException e) {
					rollBack(files);
					throw new RuntimeException();
				} catch (InvalidFileException e) {
					rollBack(files);
					throw new RuntimeException("DB Post, PostImage RollBack");
				}
			}
		);

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

	public static FileSystemResource getBasicImage() {
		return new FileSystemResource(DEFAULT_PATH + "/basic.png");
	}

	private static void verifyFile(MultipartFile image) {
		if (image == null || image.getSize() <= 0) {
			logger.warn("파일이 존재하지 않거나 사이즈가 0보다 작습니다.");
			throw new InvalidFileException("파일이 존재하지 않거나 사이즈가 0보다 작습니다.");
		}
	}

	private static void verifyDirectory(String path) {
		if (!Files.exists(Path.of(path))) {
			logger.warn("디렉토리를 찾을 수 없습니다. {}", path);
			throw new NotFoundException("디렉토리를 찾을 수 없습니다.");
		}
	}

	private static void rollBack(List<String> files) {
		for (String path : files) {
			File file = new File(path);
			if (!file.exists()) {
				return;
			}

			logger.warn("파일을 삭제합니다. [파일정보 : {}]", path);
			file.delete();
		}
	}

}
