package com.ysy.oath.mapper.user;


import com.ysy.oath.entity.user.Group;

import java.util.List;

/**
  * @author mc
  * Create date 2019-03-14 20:41:03
  * Version 1.0
  * Description 权限持久层
  */
public interface GroupMapper extends tk.mybatis.mapper.common.Mapper<Group> {

	/**
	 * 根据父级Id查询
	 * @param parentId
	 * @return
	 */
	List<Group> selectChildByParentId(Long parentId);

	/**
	 * 根据主键查询自己和所有子级
	 * @param id
	 * @return
	 */
	List<Group> selectSelfAndChildByParentId(Long id);

}
