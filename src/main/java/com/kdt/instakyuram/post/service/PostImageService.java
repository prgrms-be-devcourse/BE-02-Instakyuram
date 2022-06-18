package com.kdt.instakyuram.post.service;

import java.io.IOException;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.kdt.instakyuram.post.domain.PostImage;
import com.kdt.instakyuram.post.domain.PostImageRepository;
import com.kdt.instakyuram.post.dto.ImageUploader;

@Service
@Transactional(readOnly = true)
public class PostImageService {

	private final PostImageRepository postImageRepository;
	private final ImageUploader imageUploader;

	public PostImageService(PostImageRepository postImageRepository,
		ImageUploader imageUploader) {
		this.postImageRepository = postImageRepository;
		this.imageUploader = imageUploader;
	}

	public List<PostImage> save(List<MultipartFile> images, Long postId) throws IOException {
		List<PostImage> postImages = imageUploader.getPostImages(images, postId);

		return postImageRepository.saveAll(postImages);
	}

}
