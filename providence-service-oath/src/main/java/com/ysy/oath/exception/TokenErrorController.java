package com.ysy.oath.exception;

import com.google.common.base.Strings;
import org.springframework.boot.autoconfigure.web.ErrorProperties;
import org.springframework.boot.autoconfigure.web.servlet.error.BasicErrorController;
import org.springframework.boot.autoconfigure.web.servlet.error.ErrorViewResolver;
import org.springframework.boot.web.servlet.error.DefaultErrorAttributes;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.common.exceptions.InvalidTokenException;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * @author mc
 * Create date 2019/9/11 15:22
 * Version 1.0
 * Description
 * BasicErrorController 这个类就是用来捕获 /error 的所有错误，而过滤器中的错误会被重定向到 /error。
 * 我编写一个新的控制层类 TokenErrorController 并继承 BasicErrorController 类，
 * 这样错误 json 类型的错误都会被重定向到这个控制层里。
 */
@RestController
public class TokenErrorController extends BasicErrorController {

	private static final String PATH = "/error";

	public TokenErrorController() {
		super(new DefaultErrorAttributes(), new ErrorProperties());
	}

	@Override
	@RequestMapping(produces = {MediaType.APPLICATION_JSON_VALUE})
	public ResponseEntity<Map<String, Object>> error(HttpServletRequest request) {
		Map<String, Object> body = getErrorAttributes(request, isIncludeStackTrace(request, MediaType.ALL));
		HttpStatus status = getStatus(request);
		if (!Strings.isNullOrEmpty((String) body.get("exception")) && body.get("exception").equals(InvalidTokenException.class.getName())) {
			body.put("status", HttpStatus.FORBIDDEN.value());
			status = HttpStatus.FORBIDDEN;
		}
		return new ResponseEntity<>(body, status);
	}

	@Override
	public String getErrorPath() {
		return PATH;
	}
}
