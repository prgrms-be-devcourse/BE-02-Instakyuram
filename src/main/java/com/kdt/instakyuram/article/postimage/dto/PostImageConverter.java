package com.kdt.instakyuram.article.postimage.dto;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.io.FilenameUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.kdt.instakyuram.common.file.ResourcePath;
import com.kdt.instakyuram.article.post.domain.Post;
import com.kdt.instakyuram.article.postimage.domain.PostImage;

@Component
public class PostImageConverter {

	public Map<PostImage, MultipartFile> toPostImages(List<MultipartFile> files, Long postId) {
		Post post = Post.builder()
			.id(postId)
			.build();

		Map<PostImage, MultipartFile> postImagesMap = new HashMap<>();

		for (MultipartFile file : files) {
			String originalFileName = file.getOriginalFilename();
			String serverFileName = UUID.randomUUID() + "." + FilenameUtils.getExtension(originalFileName);

			postImagesMap.put(
				PostImage.builder()
					.post(post)
					.originalFileName(originalFileName)
					.serverFileName(serverFileName)
					.path(ResourcePath.POST.getPrefix())
					.size(file.getSize())
					.build(),
				file
			);
		}

		return postImagesMap;
	}

	public Map<String, MultipartFile> toFiles(Map<PostImage, MultipartFile> postImages) {
		Map<String, MultipartFile> files = new HashMap<>();
		postImages.forEach((postImage, multipartFile) -> files.put("/"+postImage.getServerFileName(), multipartFile));

		return files;
	}
}
