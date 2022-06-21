package com.kdt.instakyuram.post.service;

import java.util.List;

import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kdt.instakyuram.exception.NotFoundException;
import com.kdt.instakyuram.post.domain.PostImage;
import com.kdt.instakyuram.post.domain.PostImageRepository;
import com.kdt.instakyuram.post.dto.PostConverter;
import com.kdt.instakyuram.post.dto.PostImageResponse;
import com.kdt.instakyuram.util.ImageManager;

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
	public void save(List<PostImage> postImages) {
		postImageRepository.saveAll(postImages);
	}

	public List<PostImageResponse> findByPostId(Long postId) {
		return postImageRepository.findByPostId(postId).stream()
			.map(postConverter::toPostImageResponse)
			.toList();
	}

	public FileSystemResource findByServerFileName(String serverFileName) {
		return postImageRepository.findByServerFileName(serverFileName)
			.map(postImage -> ImageManager.getFileResource(postImage.getPath(), postImage.getServerFileName()))
			.orElseThrow(() -> new NotFoundException("이미지가 존재하지 않습니다."));
	}

	@Transactional
	public List<PostImageResponse.DeleteResponse> delete(Long postId) {
		List<PostImage> postImages = postImageRepository.findByPostId(postId);
		postImageRepository.deleteAll(postImages);

		return postImages.stream()
			.map(postConverter::toDeletePostImageResponse)
			.toList();
	}
}
