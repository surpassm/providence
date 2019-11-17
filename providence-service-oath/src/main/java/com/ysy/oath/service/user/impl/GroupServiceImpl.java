package com.ysy.oath.service.user.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.surpassm.common.jackson.Result;
import com.github.surpassm.common.jackson.ResultCode;
import com.ysy.oath.entity.user.*;
import com.ysy.oath.mapper.user.*;
import com.ysy.oath.security.BeanConfig;
import com.ysy.oath.service.user.GroupService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;
import tk.mybatis.mapper.weekend.WeekendSqls;

import javax.annotation.Resource;
import java.util.*;

import static com.github.surpassm.common.jackson.Result.fail;
import static com.github.surpassm.common.jackson.Result.ok;


/**
 * @author mc
 * Create date 2019-03-14 20:41:03
 * Version 1.0
 * Description 权限实现类
 */
@Slf4j
@Service
@Transactional(rollbackFor = {RuntimeException.class, Exception.class})
public class GroupServiceImpl implements GroupService {
	@Resource
	private GroupMapper groupMapper;
	@Resource
	private BeanConfig beanConfig;
	@Resource
	private GroupRoleMapper groupRoleMapper;
	@Resource
	private UserGroupMapper userGroupMapper;
	@Resource
	private MenuMapper menuMapper;
	@Resource
	private RoleMapper roleMapper;

	@Override
	public Result insert(String accessToken, Group group) {
		if (group == null) {
			return fail(ResultCode.PARAM_IS_INVALID.getMsg());
		}
		UserInfo loginUserInfo = beanConfig.getAccessToken(accessToken);
		//效验名称是否重复
		Group build = Group.builder().name(group.getName()).build();
		build.setIsDelete(0);
		int groupCount = groupMapper.selectCount(build);
		if (groupCount != 0) {
			return fail(ResultCode.RESULE_DATA_NONE.getMsg());
		}
		//查看父级是否存在
		if (isEnableParent(group)) {
			return fail(ResultCode.PARAM_TYPE_BIND_ERROR.getMsg());
		}
		group.setIsDelete(0);
		groupMapper.insert(group);
		return ok();
	}

	@Override
	public Result update(String accessToken, Group group) {
		if (group == null) {
			return fail(ResultCode.PARAM_IS_INVALID.getMsg());
		}
		if (group.getIsDelete() == 1){
			return fail(ResultCode.PARAM_IS_INVALID.getMsg());
		}
		UserInfo loginUserInfo = beanConfig.getAccessToken(accessToken);

		Example.Builder builder = new Example.Builder(Group.class);
		builder.where(WeekendSqls.<Group>custom().andEqualTo(Group::getIsDelete, 0));
		builder.where(WeekendSqls.<Group>custom().andEqualTo(Group::getName, group.getName()));
		builder.where(WeekendSqls.<Group>custom().andNotIn(Group::getId, Collections.singletonList(group.getId())));

		List<Group> selectCount = groupMapper.selectByExample(builder.build());
		if (selectCount.size() != 0) {
			return fail(ResultCode.RESULE_DATA_NONE.getMsg());
		}
		if (isEnableParent(group)) {
			return fail(ResultCode.PARAM_TYPE_BIND_ERROR.getMsg());
		}


		groupMapper.updateByPrimaryKeySelective(group);
		return ok();
	}

	private boolean isEnableParent(Group group) {
		if (group.getParentId() != null) {
			Group buildGroup = Group.builder().id(group.getParentId()).build();
			buildGroup.setIsDelete(0);
			int buildGroupCount = groupMapper.selectCount(buildGroup);
			return buildGroupCount == 0;
		}
		return false;
	}

	@Override
	public Result deleteGetById(String accessToken, Long id) {
		if (id == null) {
			return fail(ResultCode.PARAM_IS_INVALID.getMsg());
		}
		Group group = groupMapper.selectByPrimaryKey(id);
		if (group == null) {
			return fail(ResultCode.RESULE_DATA_NONE.getMsg());
		}
		UserInfo loginUserInfo = beanConfig.getAccessToken(accessToken);
		Group groupBuild = Group.builder().parentId(id).build();
		groupBuild.setIsDelete(0);
		int groupCount = groupMapper.selectCount(groupBuild);
		if (groupCount != 0){
			return fail("存在下级关联数据无法删除");
		}
		//组角色查询
		GroupRole groupRole = GroupRole.builder().groupId(id).build();
		groupRole.setIsDelete(0);
		int groupRoleCount = groupRoleMapper.selectCount(groupRole);
		CommonImpl.groupRoleDeleteUpdata(loginUserInfo,groupRole,groupRoleCount,groupRoleMapper);
		//用户组查询
		UserGroup userGroup = UserGroup.builder().groupId(id).build();
		userGroup.setIsDelete(0);
		int userGroupCount = userGroupMapper.selectCount(userGroup);
		CommonImpl.userGroupDeleteUpdata(loginUserInfo,userGroup,userGroupCount,userGroupMapper);
		group.setIsDelete(1);
		groupMapper.updateByPrimaryKeySelective(group);
		return ok();
	}


	@Override
	public Result findById(String accessToken, Long id) {
		if (id == null) {
			return fail(ResultCode.PARAM_IS_INVALID.getMsg());
		}
		Group group = groupMapper.selectByPrimaryKey(id);
		if (group == null) {
			return fail(ResultCode.RESULE_DATA_NONE.getMsg());
		}
		return ok(group);

	}

	@Override
	public Result pageQuery(String accessToken, Integer page, Integer size, String sort, Group group) {
		page = null == page ? 1 : page;
		size = null == size ? 10 : size;
		PageHelper.startPage(page, size);
		Example.Builder builder = new Example.Builder(Group.class);
		builder.where(WeekendSqls.<Group>custom().andEqualTo(Group::getIsDelete, 0));
		if (group != null) {
			if (group.getId() != null) {
				builder.where(WeekendSqls.<Group>custom().andEqualTo(Group::getId, group.getId()));
			}
			if (group.getDescribes() != null && !"".equals(group.getDescribes().trim())) {
				builder.where(WeekendSqls.<Group>custom().andLike(Group::getDescribes, "%" + group.getDescribes() + "%"));
			}
			if (group.getName() != null && !"".equals(group.getName().trim())) {
				builder.where(WeekendSqls.<Group>custom().andLike(Group::getName, "%" + group.getName() + "%"));
			}
			if (group.getParentId() != null) {
				builder.where(WeekendSqls.<Group>custom().andEqualTo(Group::getParentId, group.getParentId()));
			} else {
				builder.where(WeekendSqls.<Group>custom().andIsNull(Group::getParentId));
			}
		} else {
			builder.where(WeekendSqls.<Group>custom().andIsNull(Group::getParentId));
		}
		Page<Group> all = (Page<Group>) groupMapper.selectByExample(builder.build());
		return ok(all.toPageInfo());
	}

	@Override
	public Result getParentId(String accessToken, Long parentId) {
		List<Group> groups = groupMapper.selectChildByParentId(parentId);
		return ok(groups);
	}

	@Override
	public Result findByOnlyAndChildren(String accessToken, Long id) {
		List<Group> groups = groupMapper.selectSelfAndChildByParentId(id);
		return ok(groups);
	}

	@Override
	public Result setGroupByMenu(String accessToken, Long id, String menuId) {
		UserInfo loginUser = beanConfig.getAccessToken(accessToken);
		String[] splits = StringUtils.split(menuId,",");
		if (splits == null || splits.length == 0){
			return fail(ResultCode.PARAM_IS_INVALID.getMsg());
		}
		Group group = Group.builder().id(id).build();
		group.setIsDelete(0);
		int groupCount = groupMapper.selectCount(group);
		if (groupCount == 0){
			return fail(ResultCode.RESULE_DATA_NONE.getMsg());
		}
		return ok();
	}

	@Override
	public Result setGroupByRole(String accessToken, Long id, String roleIds) {
		UserInfo loginUser = beanConfig.getAccessToken(accessToken);
		String[] splits = StringUtils.split(roleIds,",");
		if (splits == null || splits.length == 0){
			return fail(ResultCode.PARAM_IS_INVALID.getMsg());
		}
		Group group = Group.builder().id(id).build();
		group.setIsDelete(0);
		int groupCount = groupMapper.selectCount(group);
		if (groupCount == 0){
			return fail(ResultCode.RESULE_DATA_NONE.getMsg());
		}
		//删除原有组对应的角色
		Example.Builder builder = new Example.Builder(GroupRole.class);
		builder.where(WeekendSqls.<GroupRole>custom().andEqualTo(GroupRole::getIsDelete, 0));
		builder.where(WeekendSqls.<GroupRole>custom().andEqualTo(GroupRole::getGroupId, id));
		groupRoleMapper.deleteByExample(builder.build());
		//新增现有的角色
		for(String split : splits){
			GroupRole build = GroupRole.builder().groupId(id).roleId(Long.valueOf(split)).build();
			build.setIsDelete(0);
			groupRoleMapper.insert(build);
		}
		return ok();
	}

	@Override
	public Result findGroupToMenu(String accessToken, Long groupId, Integer page, Integer size, String sort) {
		//根据组查询有所权限列表ID
//		List<GroupMenu> select = groupMenuMapper.select(GroupMenu.builder().groupId(groupId).isDelete(0).build());
//		if (select.size() == 0){
//			return Result.ok(new Page<>());
//		}
//		page = null == page ? 1 : page;
//		size = null == size ? 10 : size;
//		PageHelper.startPage(page, size);
//		Example.Builder builder = new Example.Builder(Menu.class);
//		builder.where(WeekendSqls.<Menu>custom().andEqualTo(Menu::getIsDelete, 0));
//		builder.where(WeekendSqls.<Menu>custom().andIn(Menu::getId, select));
//		Page<Menu> all = (Page<Menu>) menuMapper.selectByExample(builder.build());
//		return ok(all.toPageInfo());
		return null;
	}

	@Override
	public Result findGroupToRole(String accessToken, Long groupId, Integer page, Integer size, String sort) {
		List<GroupRole> select = groupRoleMapper.select(GroupRole.builder().groupId(groupId).isDelete(0).build());
		if (select.size() == 0){
			return Result.ok(new Page<>());
		}
		page = null == page ? 1 : page;
		size = null == size ? 10 : size;
		PageHelper.startPage(page, size);
		Example.Builder builder = new Example.Builder(Role.class);
		builder.where(WeekendSqls.<Role>custom().andEqualTo(Role::getIsDelete, 0));
		builder.where(WeekendSqls.<Role>custom().andIn(Role::getId, select));
		Page<Role> all = (Page<Role>) roleMapper.selectByExample(builder.build());
		return ok(all.toPageInfo());
	}
}

