package com.ysy.oath.aspect;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.surpassm.common.jackson.Result;
import com.github.surpassm.security.properties.SecurityProperties;
import com.ysy.oath.entity.user.OperationsLog;
import com.ysy.oath.entity.user.UserInfo;
import com.ysy.oath.mapper.user.OperationsLogMapper;
import com.ysy.oath.security.BeanConfig;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.time.LocalDateTime;

/**
 * @author Administrator
 */
@Slf4j
@Aspect
@Component
public class LogAspect {
	@Resource
	private ObjectMapper objectMapper;
	@Resource
	private OperationsLogMapper operationsLogMapper;
	@Resource
	private BeanConfig beanConfig;
	@Resource
	private SecurityProperties securityProperties;

	@Before("execution(* com.ysy.*.controller..*.insert*(..)) || " +
			"execution(* com.ysy.*.controller..*.update*(..)) || " +
			"execution(* com.ysy.*.controller..*.delete*(..))")
	public void setLog(JoinPoint joinPoint) {
		// 日志
		OperationsLog log = new OperationsLog();
		// 模块
		Class clazz = joinPoint.getSignature().getDeclaringType();
		if (clazz.isAnnotationPresent(Api.class)) {
			Api api = (Api) clazz.getAnnotation(Api.class);
			log.setModule(api.tags()[0]);
		}
		// 功能
		for (Method method : clazz.getMethods()) {
			if (joinPoint.getSignature().getName().equals(method.getName())) {
				if (method.isAnnotationPresent(ApiOperation.class)) {
					ApiOperation apiOperation = method.getAnnotation(ApiOperation.class);
					log.setFunction(apiOperation.value());
				}
			}
		}
		// 接口地址、客户端IP
		ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
		if (requestAttributes != null) {
			HttpServletRequest request = requestAttributes.getRequest();
			String url = request.getRequestURI();
			log.setUri(url);
			log.setClientIp(request.getRemoteHost());
			if (!checkAuthorization(url)) {
				String header = request.getHeader("Authorization");
				if (header != null && header.startsWith("Bearer ")) {
					String token = header.substring(7);
					UserInfo loginUser = beanConfig.getAccessToken(token);
					// 用户主键
					log.setUserId(loginUser.getId());
				}
			}
			// 操作开始时间
			log.setOperateStartTime(LocalDateTime.now());
			// 保存日志到本地线程
			LogHolder.set(log);
		}
	}

	/**
	 * 效验当前请求是否属于免验证接口
	 * @param url 接口
	 * @return boolean
	 */
	private boolean checkAuthorization(String url){
		boolean flag =false;
		url = url + "**";
		String[] noVerify = securityProperties.getNoVerify();
		for (String s : noVerify) {
			 if (url.equals(s)){
				 flag = true;
				 break;
			 }
		}
		return flag;
	}

	@AfterReturning(pointcut = "execution(* com.ysy.*.service..*.insert*(..)) || " +
			"execution(* com.ysy.*.service..*.update*(..)) || " +
			"execution(* com.ysy.*.service..*.deleteById*(..))", returning = "result")
	public void setLogData(Result result) throws JsonProcessingException {
		if (200 == result.getCode()) {
			// 获取日志
			OperationsLog log = LogHolder.get();
			// 数据
			if (result.getData() != null) {
				String data = objectMapper.writeValueAsString(result.getData());
				log.setData(data);
			}
			// 操作结束时间
			log.setOperateEndTime(LocalDateTime.now());
			// 新增日志
			operationsLogMapper.insert(log);
		}
	}

	@After("execution(* com.ysy.*.controller..*.insert*(..)) || " +
			"execution(* com.ysy.*.controller..*.update*(..)) || " +
			"execution(* com.ysy.*.controller..*.delete*(..))")
	public void removeLog() {
		// 从本地线程中删除日志
		LogHolder.remove();
	}
}
