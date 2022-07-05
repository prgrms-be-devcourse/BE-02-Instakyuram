package com.kdt.instakyuram.common.file;

import java.util.List;
import java.util.Map;

import org.springframework.core.io.FileSystemResource;
import org.springframework.web.multipart.MultipartFile;

public interface FileStorage {

	void upload(Map<String, MultipartFile> files, ResourcePath path);

	void upload(String serverFileName, MultipartFile file, ResourcePath path);

	FileSystemResource get(String path);

	void rollback(List<String> files);

	void delete(String serverFileName, ResourcePath path);

}
