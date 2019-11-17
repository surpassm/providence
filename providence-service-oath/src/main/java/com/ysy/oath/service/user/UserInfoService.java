package com.ysy.oath.service.user;

import com.github.surpassm.common.jackson.Result;
import com.ysy.oath.entity.user.UserInfo;

/**
  * @author mc
  * Create date 2019-03-14 20:41:03
  * Version 1.0
  * Description 接口
  */
public interface UserInfoService {
    /**
	 * 新增
	 * @param userInfo 对象
	 * @return 前端返回格式
	 */
    Result insert(String accessToken, UserInfo userInfo);
    /**
	 * 修改
	 * @param userInfo 对象
	 * @return 前端返回格式
	 */
    Result update(String accessToken, UserInfo userInfo);
    /**
	 * 根据主键删除
	 * @param id 标识
	 * @return 前端返回格式
	 */
    Result deleteGetById(String accessToken, Long id);
    /**
	 * 根据主键查询
	 * @param id 标识
	 * @return 前端返回格式
	 */
    Result findById(String accessToken, Long id);
    /**
	 * 条件分页查询
	 * @param page 当前页
	 * @param size 显示多少条
	 * @param sort 排序字段
	 * @param userInfo 查询条件
	 * @return 前端返回格式
	 */
    Result pageQuery(String accessToken, Integer page, Integer size, String sort, UserInfo userInfo);

	/**
	 * 根据主键查询用户及角色、权限列表
	 * @param accessToken
	 * @param id
	 * @return
	 */
	Result findRolesAndMenus(String accessToken, Long id);

	/**
	 * 设置用户、组
	 * @param accessToken
	 * @param id
	 * @param groupIds
	 * @return
	 */
	Result setUserByGroup(String accessToken, Long id, String groupIds);

	/**
	 * 设置用户权限
	 * @param accessToken
	 * @param id
	 * @param menuIds
	 * @return
	 */
	Result setUserByMenu(String accessToken, Long id, String menuIds);

	/**
	 * 设置用户、角色
	 * @param accessToken
	 * @param id
	 * @param roleIds
	 * @return
	 */
	Result setUserByRoles(String accessToken, Long id, String roleIds);
}
