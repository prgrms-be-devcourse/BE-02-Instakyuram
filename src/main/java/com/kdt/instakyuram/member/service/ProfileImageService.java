package com.kdt.instakyuram.member.service;

import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.kdt.instakyuram.common.file.FileStorage;
import com.kdt.instakyuram.common.file.ResourcePath;
import com.kdt.instakyuram.common.file.exception.FileReadException;

@Service
public class ProfileImageService {
	private final FileStorage fileStorage;

	public ProfileImageService(FileStorage fileStorage) {
		this.fileStorage = fileStorage;
	}

	public FileSystemResource findProfileImage(String name) {
		try {
			return fileStorage.get(ResourcePath.PROFILE.getPrefix() + "/" + name);
		} catch (FileReadException e) {
			return fileStorage.get(ResourcePath.PROFILE.getPrefix() + "/default_profile.jpg");
		}
	}

	public void update(MultipartFile profileImage, String profileImageName) {
		this.fileStorage.upload(profileImageName, profileImage, ResourcePath.PROFILE);
	}
}
