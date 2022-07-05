package com.kdt.instakyuram.article.postimage.service;

import java.text.MessageFormat;
import java.util.List;
import java.util.Map;

import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.kdt.instakyuram.common.file.FileStorage;
import com.kdt.instakyuram.common.file.ResourcePath;
import com.kdt.instakyuram.exception.EntityNotFoundException;
import com.kdt.instakyuram.exception.ErrorCode;
import com.kdt.instakyuram.article.post.domain.Post;
import com.kdt.instakyuram.article.postimage.domain.PostImage;
import com.kdt.instakyuram.article.postimage.domain.PostImageRepository;
import com.kdt.instakyuram.article.post.dto.PostConverter;
import com.kdt.instakyuram.article.postimage.dto.PostImageConverter;
import com.kdt.instakyuram.article.postimage.dto.PostImageResponse;

@Service
@Transactional(readOnly = true)
public class PostImageService {

	private final PostImageRepository postImageRepository;
	private final PostImageConverter postImageConverter;
	private final FileStorage fileStorage;
	private final PostConverter postConverter;

	public PostImageService(PostImageRepository postImageRepository,
		PostImageConverter postImageConverter, FileStorage fileStorage,
		PostConverter postConverter) {
		this.postImageRepository = postImageRepository;
		this.postImageConverter = postImageConverter;
		this.fileStorage = fileStorage;
		this.postConverter = postConverter;
	}

	@Transactional
	public void save(Long postId, List<MultipartFile> uploadingFiles) {
		Map<PostImage, MultipartFile> postImages = postImageConverter.toPostImages(uploadingFiles, postId);

		postImageRepository.saveAll(postImages.keySet());

		Map<String, MultipartFile> files = postImageConverter.toFiles(postImages);
		fileStorage.upload(files, ResourcePath.POST);
	}

	public List<PostImageResponse> findByPostId(Long postId) {
		return postImageRepository.findByPostId(postId).stream()
			.map(postConverter::toPostImageResponse)
			.toList();
	}

	public FileSystemResource findByServerFileName(String serverFileName) {
		return postImageRepository.findByServerFileName(serverFileName)
			.map(postImage -> fileStorage.get(postImage.getFullPath()))
			.orElseThrow(() -> new EntityNotFoundException(ErrorCode.POST_IMAGE_NOT_FOUND,
				MessageFormat.format("serverFileName = {0}", serverFileName)));
	}

	@Transactional
	public void delete(Long postId) {
		List<PostImage> postImages = postImageRepository.findByPostId(postId);
		postImageRepository.deleteAll(postImages);

		postImages.stream()
			.map(postConverter::toDeletePostImageResponse)
			.toList();

		postImages.forEach(image ->
			fileStorage.delete(image.getServerFileName(), ResourcePath.POST));
	}

	public PostImageResponse.ThumbnailResponse findThumbnailByPostId(Long postId) {
		return postImageRepository.findTop1ByPostId(postId)
			.map(postImage -> postConverter.toThumbnailResponse(postId, postImage))
			.orElseThrow(() -> new EntityNotFoundException(ErrorCode.POST_IMAGE_NOT_FOUND,
				MessageFormat.format("post ID = {0}", postId)));
	}

	public List<PostImageResponse> findByPostIn(List<Post> posts) {
		return postImageRepository.findByPostIn(posts).stream()
			.map(postConverter::toPostImageResponse)
			.toList();
	}
}
