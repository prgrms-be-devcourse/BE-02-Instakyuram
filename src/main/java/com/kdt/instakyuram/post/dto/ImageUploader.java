package com.kdt.instakyuram.post.dto;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.kdt.instakyuram.post.domain.Post;
import com.kdt.instakyuram.post.domain.PostImage;

@Component
public class ImageUploader {

	@Value("${file.dir.post}")
	String path;

	private static final Logger log = LoggerFactory.getLogger(ImageUploader.class);

	public List<PostImage> getPostImages(List<MultipartFile> images, Long postId) throws IOException {
		List<PostImage> postImages = new ArrayList<>();

		for (MultipartFile file : images) {
			PostImage postImage = getPostImage(file, postId);
			save(file, postImage.getServerFileName());

			postImages.add(postImage);
		}

		return postImages;
	}

	private PostImage getPostImage(MultipartFile file, Long postId) {
		String originalFileMame = file.getOriginalFilename();

		if(originalFileMame == null) {
			throw new IllegalArgumentException("이미지 파일명이 없습니다.");
		}

		String serverFileName = UUID.randomUUID().toString() + extractExt(originalFileMame);

		return PostImage.builder()
			.post(
				Post.builder()
					.id(postId)
					.build()
			)
			.originalFileName(originalFileMame)
			.serverFileName(serverFileName)
			.path(this.path)
			.size(file.getSize())
			.build();
	}

	private void save(MultipartFile file, String serverFileName) throws IOException {
		try {
			file.transferTo(
				new File( this.path + serverFileName )
			);
		} catch (IOException e) {
			log.warn("이미지 파일을 저장하면서 오류가 발생했습니다.", e);

			throw new IOException("이미지 파일이 저장되지 않습니다.");
		}
	}

	private static String extractExt(String originalFileName) {
		return originalFileName.substring(
			originalFileName.lastIndexOf(".")
		);
	}

}
