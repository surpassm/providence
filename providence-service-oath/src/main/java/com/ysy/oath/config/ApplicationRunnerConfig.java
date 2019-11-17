package com.ysy.oath.config;

import com.github.surpassm.security.properties.SecurityProperties;
import com.ysy.oath.entity.user.Menu;
import com.ysy.oath.mapper.user.MenuMapper;
import com.spring4all.swagger.SwaggerProperties;
import io.swagger.models.Path;
import io.swagger.models.Swagger;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.service.Documentation;
import springfox.documentation.spring.web.DocumentationCache;
import springfox.documentation.swagger2.mappers.ServiceModelToSwagger2Mapper;

import javax.annotation.Resource;
import java.util.*;

/**
 * @author mc
 * Create date 2019/9/6 16:34
 * Version 1.0
 * Description
 */
@Slf4j
@Configuration
public class ApplicationRunnerConfig implements ApplicationRunner {
	@Resource
	private ServiceModelToSwagger2Mapper mapper;
	@Resource
	private DocumentationCache documentationCache;
	@Resource
	private MenuMapper menuMapper;
	@Resource
	private SecurityProperties securityProperties;
	@Resource
	private SwaggerProperties swaggerProperties;

	@Override
	public void run(ApplicationArguments args) throws Exception {
		//后台所有接口更新
		resourcesUpdate();
	}


	private void resourcesUpdate(){
		Map<String, SwaggerProperties.DocketInfo> docket = swaggerProperties.getDocket();
		if (docket != null && docket.size() > 0) {
			Iterator<Map.Entry<String, SwaggerProperties.DocketInfo>> iterator = docket.entrySet().iterator();
			List<String> noVerify = Arrays.asList(securityProperties.getNoVerify());
			while (iterator.hasNext()){
				Map.Entry<String, SwaggerProperties.DocketInfo> next = iterator.next();
				String group = next.getKey();
				Documentation documentation = documentationCache.documentationByGroup(group);
				if (documentation != null) {
					Swagger swagger = mapper.mapDocumentation(documentation);
					Map<String, Path> paths = swagger.getPaths();
					paths.forEach((key, value) -> {
						// 链接
						String url = key + "**";
						// 排除不验证的url
						if (!noVerify.contains(url)) {
							// 名称
							if (value.getPost() != null) {
								String name = value.getPost().getSummary();
								// 描述
								String description = value.getPost().getTags().get(0);
								// 权限
								Menu build = Menu.builder().name(description).build();
								build.setIsDelete(0);
								Menu menu = menuMapper.selectOne(build);
								menuInsertAndUpdata(url, name, description, menu);
							}
							if (value.getGet() != null) {
								String name = value.getGet().getSummary();
								// 描述
								String description = value.getGet().getTags().get(0);
								// 权限
								Menu build = Menu.builder().name(description).build();
								build.setIsDelete(0);
								Menu menu = menuMapper.selectOne(build);
								menuInsertAndUpdata(url, name, description, menu);
							}
						}
					});
				}
			}
		}
	}
	private void menuInsertAndUpdata(String url, String name, String description, Menu menu) {
		if (menu == null){
			//新增父级
			Menu parentMenu = Menu.builder()
					.name(description)
					.type(1)
					.build();
			parentMenu.setIsDelete(0);
			menuMapper.insert(parentMenu);
			//在添加当前url为子级
			Menu menuBuild = Menu.builder()
					.menuUrl(url)
					.parentId(parentMenu.getId())
					.describes(name)
					.type(1)
					.build();
			menuBuild.setIsDelete(0);
			menuMapper.insert(menuBuild);
		}else {
			Menu build = Menu.builder().menuUrl(url).build();
			build.setIsDelete(0);
			int selectCount = menuMapper.selectCount(build);
			if (selectCount == 0){
				//去除重复数据
				Menu menuBuild = Menu.builder()
						.menuUrl(url)
						.type(1)
						.parentId(menu.getId())
						.describes(name)
						.build();
				menuBuild.setIsDelete(0);
				menuMapper.insert(menuBuild);
			}
		}
	}
}
