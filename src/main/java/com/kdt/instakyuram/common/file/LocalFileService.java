package com.kdt.instakyuram.common.file;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.kdt.instakyuram.common.file.exception.FileReadException;
import com.kdt.instakyuram.common.file.exception.FileWriteException;

@Component
public class LocalFileService implements FileStorage {

	private static final Logger logger = LoggerFactory.getLogger(LocalFileService.class);
	private static final String DEFAULT_PATH = "classpath:client-file";

	private final ResourceLoader resourceLoader;

	public LocalFileService(ResourceLoader resourceLoader) {
		this.resourceLoader = resourceLoader;
	}

	@Override
	public void upload(Map<String, MultipartFile> files, ResourcePath path) {
		List<String> fullPathsForRollBack = new ArrayList<>();
		Resource directory = resourceLoader.getResource(DEFAULT_PATH + path.getPrefix());

		files.forEach((serverFileName, file) -> {
				try {
					String fullPrefix = directory.getURI().getPath();
					String fullPath = fullPrefix + serverFileName;

					fullPathsForRollBack.add(fullPath);

					file.transferTo(new File(fullPath));
				} catch (IOException e) {
					rollback(fullPathsForRollBack);
					throw new FileWriteException("파일을 저장하면서 오류가 발생했습니다.");
				}
			}
		);
	}

	@Override
	public void upload(String serverFileName, MultipartFile file, ResourcePath path) {
		String fullPathForRollback = "";
		Resource directory = resourceLoader.getResource(DEFAULT_PATH + path.getPrefix());

		try {
			String fullPrefix = directory.getURI().getPath();
			String fullPath = fullPrefix +"/"+ serverFileName;

			fullPathForRollback = fullPath;

			file.transferTo(new File(fullPath));
		} catch (IOException e) {
			rollback(List.of(fullPathForRollback));
			throw new FileWriteException("파일을 저장하면서 오류가 발생했습니다.");
		}
	}

	@Override
	public FileSystemResource get(String fullPath) {
		Resource resource = resourceLoader.getResource(DEFAULT_PATH + fullPath);

		try {
			return new FileSystemResource(resource.getFile());
		} catch (IOException e) {
			throw new FileReadException("파일을 읽어오면서 오류가 발생했습니다.");
		}
	}

	@Override
	public void rollback(List<String> files) {
		for (String path : files) {
			File file = new File(path);

			if (!file.exists()) {
				return;
			}

			logger.warn("파일을 삭제합니다. [파일정보 : {}]", path);
			file.delete();
		}
	}

	@Override
	public void delete(String serverFileName, ResourcePath path) {
		try {
			Resource directory = resourceLoader.getResource(DEFAULT_PATH + path.getPrefix());
			String fullPath = directory.getURI().getPath() + "/" + serverFileName;

			File file = new File(fullPath);

			logger.warn("파일을 삭제합니다. [파일정보 : {}]", fullPath);
			file.delete();
		} catch (IOException e) {
			throw new FileWriteException("파일을 삭제하면서 오류가 발생했습니다.");
		}
	}

}
