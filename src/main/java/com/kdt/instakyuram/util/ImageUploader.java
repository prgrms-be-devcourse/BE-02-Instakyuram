package com.kdt.instakyuram.util;

import java.io.File;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

public class ImageUploader {

	private ImageUploader() {}

	private static final Logger logger = LoggerFactory.getLogger(ImageUploader.class);

	public static void writePostImages(MultipartFile image, String serverFileName, String path) {
		try {
			image.transferTo(new File(path + serverFileName));
		} catch (IOException e) {
			logger.warn("이미지 파일을 저장하면서 오류가 발생하였습니다.");
			throw new RuntimeException(e);
		}

	}

}
