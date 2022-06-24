package com.kdt.instakyuram.post.service;

import java.util.List;

import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kdt.instakyuram.exception.BusinessException;
import com.kdt.instakyuram.exception.ErrorCode;
import com.kdt.instakyuram.post.domain.Post;
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
	public void save(Iterable<PostImage> postImages) {
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
			.orElseThrow(() -> new BusinessException(ErrorCode.POST_IMAGE_NOT_FOUND));
	}

	@Transactional
	public List<PostImageResponse.DeleteResponse> delete(Long postId) {
		List<PostImage> postImages = postImageRepository.findByPostId(postId);
		postImageRepository.deleteAll(postImages);

		return postImages.stream()
			.map(postConverter::toDeletePostImageResponse)
			.toList();
	}

	public PostImageResponse.ThumbnailResponse findThumbnailByPostId(Long postId) {
		return postImageRepository.findTop1ByPostId(postId)
			.map(postImage -> postConverter.toThumbnailResponse(postId, postImage))
			.orElseThrow(() -> new BusinessException(ErrorCode.POST_IMAGE_NOT_FOUND));
	}

	public List<PostImageResponse> findByPostIn(List<Post> posts) {
		return postImageRepository.findByPostIn(posts).stream()
			.map(postConverter::toPostImageResponse)
			.toList();
	}
}
