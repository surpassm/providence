package com.ysy.oath.service.user.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.surpassm.common.jackson.Result;
import com.github.surpassm.common.jackson.ResultCode;
import com.ysy.oath.entity.user.Role;
import com.ysy.oath.entity.user.RoleMenu;
import com.ysy.oath.entity.user.UserInfo;
import com.ysy.oath.mapper.user.RoleMapper;
import com.ysy.oath.mapper.user.RoleMenuMapper;
import com.ysy.oath.security.BeanConfig;
import com.ysy.oath.service.user.RoleService;
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
import static com.ysy.oath.service.user.impl.CommonImpl.roleMenuDeleteUpdata;


/**
 * @author mc
 * Create date 2019-03-14 20:41:03
 * Version 1.0
 * Description 实现类
 */
@Slf4j
@Service
@Transactional(rollbackFor = {RuntimeException.class, Exception.class})
public class RoleServiceImpl implements RoleService {
	@Resource
	private RoleMapper roleMapper;
	@Resource
	private BeanConfig beanConfig;
	@Resource
	private RoleMenuMapper roleMenuMapper;

	@Override
	public Result insert(String accessToken, Role role) {
		if (role == null) {
			return fail(ResultCode.PARAM_IS_INVALID.getMsg());
		}
		UserInfo loginUserInfo = beanConfig.getAccessToken(accessToken);

		Role build = Role.builder().name(role.getName()).build();
		build.setIsDelete(0);
		int selectCount = roleMapper.selectCount(build);
		if (selectCount != 0) {
			return fail(ResultCode.DATA_ALREADY_EXISTED.getMsg());
		}
		role.setIsDelete(0);
		roleMapper.insert(role);
		return ok();
	}

	@Override
	public Result update(String accessToken, Role role) {
		if (role == null) {
			return fail(ResultCode.PARAM_IS_INVALID.getMsg());
		}
		if (role.getIsDelete() == 1) {
			return fail(ResultCode.PARAM_IS_INVALID.getMsg());
		}
		UserInfo loginUserInfo = beanConfig.getAccessToken(accessToken);

		Example.Builder builder = new Example.Builder(Role.class);
		builder.where(WeekendSqls.<Role>custom().andEqualTo(Role::getIsDelete, 0));
		builder.where(WeekendSqls.<Role>custom().andEqualTo(Role::getName, role.getName()));
		builder.where(WeekendSqls.<Role>custom().andNotIn(Role::getId, Collections.singletonList(role.getId())));

		int selectCount = roleMapper.selectCountByExample(builder.build());
		if (selectCount != 0) {
			return fail(ResultCode.DATA_ALREADY_EXISTED.getMsg());
		}

		roleMapper.updateByPrimaryKeySelective(role);
		return ok();
	}

	@Override
	public Result deleteGetById(String accessToken, Long id) {
		if (id == null) {
			return fail(ResultCode.PARAM_IS_INVALID.getMsg());
		}
		Role role = roleMapper.selectByPrimaryKey(id);
		if (role == null) {
			return fail(ResultCode.RESULE_DATA_NONE.getMsg());
		}
		UserInfo loginUserInfo = beanConfig.getAccessToken(accessToken);

		RoleMenu build = RoleMenu.builder().roleId(id).build();
		build.setIsDelete(0);
		int roleMenuCount = roleMenuMapper.selectCount(build);
		//角色权限查询
		roleMenuDeleteUpdata(loginUserInfo, build, roleMenuCount, roleMenuMapper);

		role.setIsDelete(1);
		roleMapper.updateByPrimaryKeySelective(role);
		return ok();
	}


	@Override
	public Result findById(String accessToken, Long id) {
		if (id == null) {
			return fail(ResultCode.PARAM_IS_INVALID.getMsg());
		}
		Role role = roleMapper.selectByPrimaryKey(id);
		if (role == null) {
			return fail(ResultCode.RESULE_DATA_NONE.getMsg());
		}
		return ok(role);

	}

	@Override
	public Result pageQuery(String accessToken, Integer page, Integer size, String sort, Role role) {
		page = null == page ? 1 : page;
		size = null == size ? 10 : size;
		PageHelper.startPage(page, size);
		Example.Builder builder = new Example.Builder(Role.class);
		builder.where(WeekendSqls.<Role>custom().andEqualTo(Role::getIsDelete, 0));
		if (role != null) {
			if (role.getId() != null) {
				builder.where(WeekendSqls.<Role>custom().andEqualTo(Role::getId, role.getId()));
			}
			if (role.getDescribes() != null && !"".equals(role.getDescribes().trim())) {
				builder.where(WeekendSqls.<Role>custom().andLike(Role::getDescribes, "%" + role.getDescribes() + "%"));
			}
			if (role.getName() != null && !"".equals(role.getName().trim())) {
				builder.where(WeekendSqls.<Role>custom().andLike(Role::getName, "%" + role.getName() + "%"));
			}
			if (role.getRoleIndex() != null) {
				builder.where(WeekendSqls.<Role>custom().andEqualTo(Role::getRoleIndex, role.getRoleIndex()));
			}
			if (role.getSecurityRoles() != null && !"".equals(role.getSecurityRoles().trim())) {
				builder.where(WeekendSqls.<Role>custom().andLike(Role::getSecurityRoles, "%" + role.getSecurityRoles() + "%"));
			}
		}
		Page<Role> all = (Page<Role>) roleMapper.selectByExample(builder.build());
		return ok(all.toPageInfo());
	}

	@Override
	public Result findMenus(String accessToken, Long id) {
		if (id == null) {
			return fail(ResultCode.PARAM_IS_INVALID.getMsg());
		}
		Role role = roleMapper.findByMenus(id);
		if (role == null) {
			return fail(ResultCode.RESULE_DATA_NONE.getMsg());
		}
		return ok(role);
	}

	@Override
	public Result setRoleByMenu(String accessToken, Long id, String menuId) {
		UserInfo loginUser = beanConfig.getAccessToken(accessToken);
		String[] splits = StringUtils.split(menuId,",");
		if (splits == null || splits.length == 0){
			return fail(ResultCode.PARAM_IS_INVALID.getMsg());
		}
		Role role = Role.builder().id(id).build();
		role.setIsDelete(0);
		int roleCount = roleMapper.selectCount(role);
		if (roleCount == 0){
			return fail(ResultCode.RESULE_DATA_NONE.getMsg());
		}
		//删除原有角色对应的权限
		Example.Builder builder = new Example.Builder(RoleMenu.class);
		builder.where(WeekendSqls.<RoleMenu>custom().andEqualTo(RoleMenu::getIsDelete, 0));
		builder.where(WeekendSqls.<RoleMenu>custom().andEqualTo(RoleMenu::getRoleId, id));
		roleMenuMapper.deleteByExample(builder.build());
		//新增现有的角色权限
		for(String menui : splits){
			RoleMenu build = RoleMenu.builder().roleId(id).menuId(Long.valueOf(menui)).build();
			build.setIsDelete(0);
			build.setMenuType(1);
			roleMenuMapper.insert(build);
		}

		return ok();
	}
}

