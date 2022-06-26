package com.kdt.instakyuram.exception;

import org.apache.tomcat.util.http.fileupload.impl.FileSizeLimitExceededException;
import org.apache.tomcat.util.http.fileupload.impl.SizeLimitExceededException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;

import com.kdt.instakyuram.common.file.exception.InvalidFileException;

@ControllerAdvice
public class CommonControllerAdvice {

	private final Logger log = LoggerFactory.getLogger(CommonControllerAdvice.class);

	@ResponseStatus(code = HttpStatus.BAD_REQUEST)
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ModelAndView handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
		log.warn("ERROR-123 : {}", e.getMessage(), e);

		return new ModelAndView("error")
			.addObject("errorMessage", e.getMessage());
	}

	@ResponseStatus(code = HttpStatus.BAD_REQUEST)
	@ExceptionHandler(NotFoundException.class)
	public ModelAndView handleNotFoundException(NotFoundException e) {
		log.warn("ERROR-456 : {}", e.getMessage(), e);

		return new ModelAndView("error")
			.addObject("errorMessage", e.getMessage());
	}

	@ResponseStatus(code = HttpStatus.UNAUTHORIZED)
	@ExceptionHandler(NotAuthenticationException.class)
	public ModelAndView handleNotAuthenticationException(NotAuthenticationException e) {
		log.warn("ERROR-1 : {}", e.getMessage(), e);

		return new ModelAndView("error")
			.addObject("errorMessage", e.getMessage());
	}

	@ExceptionHandler({SizeLimitExceededException.class, FileSizeLimitExceededException.class,
		InvalidFileException.class})
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ModelAndView invalidFileException(Exception e) {
		log.warn("ERROR-XXX : {}", e.getMessage(), e);

		return new ModelAndView("error")
			.addObject("errorMessage", e.getMessage());
	}

}
