package com.kdt.instakyuram.post.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.kdt.instakyuram.post.domain.Post;
import com.kdt.instakyuram.post.domain.PostImage;
import com.kdt.instakyuram.post.domain.PostImageRepository;
import com.kdt.instakyuram.post.dto.PostConverter;
import com.kdt.instakyuram.util.ImageUploader;

@Service
@Transactional(readOnly = true)
public class PostImageService {

	private final PostImageRepository postImageRepository;
	private final PostConverter postConverter;

	public PostImageService(PostImageRepository postImageRepository, PostConverter postConverter) {
		this.postImageRepository = postImageRepository;
		this.postConverter = postConverter;
	}

	@Transactional
	public void save(List<MultipartFile> images, Post post) {
		for (MultipartFile image : images) {
			PostImage postImage = postConverter.toPostImage(image, post);

			ImageUploader.writePostImages(image, postImage.getServerFileName(), postImage.getPath());
			postImageRepository.save(postImage);
		}
	}

}
