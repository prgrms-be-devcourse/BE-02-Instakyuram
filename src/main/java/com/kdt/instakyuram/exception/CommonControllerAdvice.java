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

import com.kdt.instakyuram.common.file.exception.InvalidFileException;
import com.kdt.instakyuram.util.NotFoundException;

@ControllerAdvice
public class CommonControllerAdvice {

	private final Logger log = LoggerFactory.getLogger(CommonControllerAdvice.class);

	@ResponseStatus(code = HttpStatus.NOT_FOUND)
	@ExceptionHandler(EntityNotFoundException.class)
	public ModelAndView handleNotFoundException(EntityNotFoundException e) {
		this.log.warn("{}", e.toString(), e);

		return new ModelAndView("error");
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
		this.log.warn("{}", e.toString(), e);

		return new ModelAndView("error")
			.addObject("errorMessage", e.getMessage());
	}

}
