package com.kdt.instakyuram.post.dto;

import java.text.MessageFormat;
import java.util.List;
import java.util.Objects;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.springframework.web.multipart.MultipartFile;

import com.kdt.instakyuram.common.file.exception.InvalidFileException;

public record PostRequest() {

	public record CreateRequest(
		@NotNull
		@Size(max = 2200, message = "최대 2200글자 까지만 입력하실 수 있습니다.")
		String content,
		List<MultipartFile> postImages
	) {
		public CreateRequest {
			if (Objects.isNull(postImages)) {
				throw new InvalidFileException("첨부된 이미지가 없습니다.");
			}
			verifyFile(postImages);
		}

		private static void verifyFile(List<MultipartFile> files) {
			for (MultipartFile file : files) {
				if (file.getSize() <= 0) {
					throw new InvalidFileException(
						MessageFormat.format("업로드 파일 중 크기가 0이하인 파일이 존재합니다. [파일이름 : {0}]",
							file.getOriginalFilename()));
				}
				if (file.getOriginalFilename() == null) {
					throw new InvalidFileException("파일의 이름이 존재하지 않습니다.");
				}
			}
		}
	}

	public record UpdateRequest(
		@Size(max = 2200, message = "최대 2200글자 까지만 입력하실 수 있습니다.")
		String content
	) {
	}

}
