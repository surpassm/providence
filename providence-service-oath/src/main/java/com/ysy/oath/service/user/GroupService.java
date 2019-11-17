package com.ysy.oath.service.user;

import com.github.surpassm.common.jackson.Result;
import com.ysy.oath.entity.user.Group;

/**
  * @author mc
  * Create date 2019-03-14 20:41:03
  * Version 1.0
  * Description 权限接口
  */
public interface GroupService {
    /**
	 * 新增
	 * @param group 对象
	 * @return 前端返回格式
	 */
    Result insert(String accessToken, Group group);
    /**
	 * 修改
	 * @param group 对象
	 * @return 前端返回格式
	 */
    Result update(String accessToken, Group group);
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
	 * @param group 查询条件
	 * @return 前端返回格式
	 */
    Result pageQuery(String accessToken, Integer page, Integer size, String sort, Group group);

	/**
	 * 根据父级Id查询
	 * @param accessToken
	 * @param parentId
	 * @return
	 */
	Result getParentId(String accessToken, Long parentId);

	/**
	 * 根据主键查询自己和所有子级
	 * @param accessToken
	 * @param id
	 * @return
	 */
	Result findByOnlyAndChildren(String accessToken, Long id);

	/**
	 * 设置组的权限
	 * @param accessToken
	 * @param id
	 * @param menuId
	 * @return
	 */
	Result setGroupByMenu(String accessToken, Long id, String menuId);

	/**
	 * 设置组的角色
	 * @param accessToken
	 * @param id
	 * @param roleIds
	 * @return
	 */
	Result setGroupByRole(String accessToken, Long id, String roleIds);

	/**
	 * 查询组的权限
	 * @param accessToken
	 * @param groupId
	 * @param page
	 * @param size
	 * @param sort
	 * @return
	 */
	Result findGroupToMenu(String accessToken, Long groupId, Integer page, Integer size, String sort);

	/**
	 * 查询组的角色
	 * @param accessToken
	 * @param groupId
	 * @param page
	 * @param size
	 * @param sort
	 * @return
	 */
	Result findGroupToRole(String accessToken, Long groupId, Integer page, Integer size, String sort);
}
