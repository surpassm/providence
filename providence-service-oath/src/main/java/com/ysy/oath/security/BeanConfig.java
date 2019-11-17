package com.ysy.oath.security;

import com.github.surpassm.security.exception.SurpassmAuthenticationException;
import com.ysy.oath.entity.user.UserInfo;
import com.ysy.oath.mapper.user.UserInfoMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.TokenStore;

import javax.annotation.Resource;

/**
 * @author mc
 * Create date 2019/3/4 15:53
 * Version 1.0
 * Description
 */
@Slf4j
@Configuration
public class BeanConfig {
	@Resource
	private UserInfoMapper userInfoMapper;
	@Resource
	private TokenStore tokenStore;

	public UserInfo getAccessToken(String accessToken){
		try {
			OAuth2Authentication oAuth2Authentication = tokenStore.readAuthentication(accessToken);
			if (oAuth2Authentication == null){
				log.error("token已失效");
				throw new SurpassmAuthenticationException("token已失效");
			}
			Object principal = oAuth2Authentication.getPrincipal();
			if(principal instanceof UserInfo){
				return (UserInfo) principal;
			}else if (principal instanceof String){
				UserInfo userInfo = userInfoMapper.selectOne(UserInfo.builder().username(principal.toString()).build());
				if (userInfo != null) {
					return userInfo;
				}
			}
		}catch (Exception e){
			log.error("tokens必须是JWT类型");
			throw new SurpassmAuthenticationException("tokens必须是JWT类型");
		}
		log.error("请登陆");
		throw new SurpassmAuthenticationException("请登陆");
	}
}
