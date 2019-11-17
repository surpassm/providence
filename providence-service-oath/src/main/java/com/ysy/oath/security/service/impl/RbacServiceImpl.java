package com.ysy.oath.security.service.impl;

import com.ysy.oath.entity.user.UserInfo;
import com.ysy.oath.security.service.RbacService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;

import javax.servlet.http.HttpServletRequest;

/**
 * @author mc
 * version 1.0
 * date 2018/11/6 14:55
 * description 授权业务逻辑处理类
 */
@Slf4j
@Component("rbacService")
public class RbacServiceImpl implements RbacService {

	private AntPathMatcher antPathMatcher = new AntPathMatcher();
	@Override
	public boolean hasPermission(HttpServletRequest request, Authentication authentication) {
		boolean flag = false;
		Object principal = authentication.getPrincipal();
		if (principal instanceof UserInfo){
			//当前用戶
			String username = ((UserInfo) principal).getUsername();
//			Optional<UserInfo> userInfo = userInfoMapper.findByUsername(username);
//			//当前用户所具备的权限
//			List<UserMenu> menuList = userInfoMapper.findByUserRoles(userInfo.get().getUserRoles());
//			//當前用戶所擁有權限的所有URL进行遍历查看用户是否具备权限
//			String requestURI = request.getRequestURI();
//			if (!requestURI.equals("/favicon.ico")){
//				log.info("请求验证的URL"+requestURI);
//				for (UserMenu menu : menuList){
//					if (menu.getMenuUrl() != null && antPathMatcher.match(menu.getMenuUrl(),requestURI)){
//						flag = true;
//						break;
//					}
//				}
//			}
		}
		return true;
	}
}
