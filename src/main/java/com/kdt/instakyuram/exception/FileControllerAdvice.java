package com.kdt.instakyuram.exception;

import org.apache.tomcat.util.http.fileupload.impl.FileSizeLimitExceededException;
import org.apache.tomcat.util.http.fileupload.impl.SizeLimitExceededException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;

@ControllerAdvice
public class FileControllerAdvice {

	private final Logger log = LoggerFactory.getLogger(FileControllerAdvice.class);

	@ExceptionHandler(FileSizeLimitExceededException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ModelAndView fileSizeException(FileSizeLimitExceededException e) {
		log.warn("ERROR-XXX : {}", e.getMessage(), e);

		return new ModelAndView("error")
			.addObject("errorMessage", e.getMessage());
	}

	@ExceptionHandler(SizeLimitExceededException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ModelAndView maxSizeException(SizeLimitExceededException e) {
		log.warn("ERROR-XXX : {}", e.getMessage(), e);

		return new ModelAndView("error")
			.addObject("errorMessage", e.getMessage());
	}

	@ExceptionHandler(InvalidFileException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ModelAndView invalidFileException(InvalidFileException e) {
		log.warn("ERROR-XXX : {}", e.getMessage(), e);

		return new ModelAndView("error")
			.addObject("errorMessage", e.getMessage());
	}

}
