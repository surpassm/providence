package com.ysy.oath.mapper.user;


import com.ysy.oath.entity.user.Menu;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
  * @author mc
  * Create date 2019-03-14 20:41:03
  * Version 1.0
  * Description 权限持久层
  */
public interface MenuMapper extends tk.mybatis.mapper.common.Mapper<Menu> {

	/**
	 * 根据父级Id查询
	 * @param parentId
	 * @return
	 */
	List<Menu> selectChildByParentId(@Param("parentId")Long parentId);
	/**
	 * 根据主键查询自己和所有子级
	 * @param id
	 * @return
	 */
	List<Menu> selectSelfAndChildByParentId(@Param("id") Long id);
}
