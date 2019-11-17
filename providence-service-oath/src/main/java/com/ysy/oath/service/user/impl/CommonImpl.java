package com.ysy.oath.service.user.impl;

import com.ysy.oath.entity.user.*;
import com.ysy.oath.mapper.user.*;


/**
 * @author mc
 * Create date 2019/3/15 13:20
 * Version 1.0
 * Description
 */
public class CommonImpl {


	static void userMenuDeleteUpdata(UserInfo loginUserInfo, UserMenu userMenu, int userMenuCount, UserMenuMapper userMenuMapper) {
		if (userMenuCount!=0){
			userMenu.setIsDelete(1);
			userMenuMapper.updateByPrimaryKeySelective(userMenu);
		}
	}
	static void groupRoleDeleteUpdata(UserInfo loginUserInfo, GroupRole groupRole, int groupRoleCount, GroupRoleMapper groupRoleMapper) {
		if (groupRoleCount != 0){
			groupRole.setIsDelete(1);
			groupRoleMapper.updateByPrimaryKeySelective(groupRole);
		}
	}
	static void userGroupDeleteUpdata(UserInfo loginUserInfo, UserGroup userGroup, int userGroupCount, UserGroupMapper userGroupMapper) {
		if (userGroupCount != 0){
			userGroup.setIsDelete(1);
			userGroupMapper.updateByPrimaryKeySelective(userGroup);
		}
	}
	static void userInfoDeleteUpdata(UserInfo loginUserInfo, UserInfo userinfo, int userCount, UserInfoMapper userInfoMapper) {
		if (userCount != 0){
			userinfo.setIsDelete(1);
			userInfoMapper.updateByPrimaryKeySelective(userinfo);
		}
	}
}
