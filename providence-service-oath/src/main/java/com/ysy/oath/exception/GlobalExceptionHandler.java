package com.ysy.oath.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 * @author mc
 * Create date 2019/9/11 15:32
 * Version 1.0
 * Description 全局异常处理类
 * <p>
 * ControllerAdvice 捕获 Controller 层抛出的异常，如果添加 @ResponseBody 返回信息则为JSON 格式。
 * RestControllerAdvice 相当于 @ControllerAdvice 与 @ResponseBody 的结合体。
 * ExceptionHandler 统一处理一种类的异常，减少代码重复率，降低复杂度。
 * <p>
 * 因为我们这里全部异常信息都约定返回json，所以直接使用 @RestControllerAdvice 代替 @ControllerAdvice ，这样在方法上就可以不需要添加 @ResponseBody了
 * <p>
 * 步骤：
 * 1.创建一个 GlobalExceptionHandler 类，并添加上 @RestControllerAdvice 注解就可以实现异常通知类的定义了
 * 2.定义的方法中添加上 @ExceptionHandler 即可实现Controller层的异常捕捉
 */
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {
	/**
	 * 如果需要捕获多个异常   定义如下：@ExceptionHandler({})
	 *
	 * @param request  request
	 * @param e        Exception
	 * @param response response
	 * @return 捕获多个异常的写法
	 */
	@ExceptionHandler({CustomException.class, CustomException.class})
	public ExceptionResponseEntity customExceptionHandler(HttpServletRequest request, final Exception e, HttpServletResponse response) {
		response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
		CustomException exception = (CustomException) e;
		if (exception.getStackTrace().length > 0) {
			StackTraceElement stackTraceElement = exception.getStackTrace()[0];
			printlnLog("CustomException", stackTraceElement);
		}
		return new ExceptionResponseEntity(exception.getCode(), exception.getMessage());
	}


	/**
	 * 捕获  NullPointerException 异常
	 * 如果在一个 exceptionHandler 通过  if (e instanceof xxxException) 太麻烦，
	 * 可以写多个方法标注@ExceptionHandler处理不同的异常
	 *
	 * @param request  request
	 * @param e        exception
	 * @param response response
	 * @return 响应结果
	 */
	@ExceptionHandler(NullPointerException.class)
	public ExceptionResponseEntity nullPointerExceptionHandler(HttpServletRequest request, final Exception e, HttpServletResponse response) {
		response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
		NullPointerException exception = (NullPointerException) e;
		if (exception.getStackTrace().length > 0) {
			StackTraceElement stackTraceElement = exception.getStackTrace()[0];
			printlnLog("NullPointerException", stackTraceElement);
		}
		return new ExceptionResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR.value(), exception.getMessage());
	}

	/**
	 * 打印错误信息
	 *
	 * @param name              异常名称
	 * @param stackTraceElement 错误信息
	 */
	private void printlnLog(String name, StackTraceElement stackTraceElement) {
		logger.error(name +
				"->类名：" + stackTraceElement.getFileName() +
				" ->错误代码行：" + stackTraceElement.getLineNumber() +
				" ->路径：" + stackTraceElement.getClassName());
	}
}