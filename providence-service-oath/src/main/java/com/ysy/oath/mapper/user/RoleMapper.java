package com.ysy.oath.mapper.user;


import com.ysy.oath.entity.user.Role;

/**
  * @author mc
  * Create date 2019-03-14 20:41:03
  * Version 1.0
  * Description 持久层
  */
public interface RoleMapper extends tk.mybatis.mapper.common.Mapper<Role> {


	/**
	 * 根据系统标识查询角色列表
	 * @param id
	 * @return
	 */
	Role findByMenus(Long id);
}
