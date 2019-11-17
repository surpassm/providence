package com.ysy.oath.service.user.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.surpassm.common.jackson.Result;
import com.github.surpassm.common.jackson.ResultCode;
import com.github.surpassm.common.tool.util.ValidateUtil;
import com.ysy.oath.entity.user.*;
import com.ysy.oath.mapper.user.*;
import com.ysy.oath.security.BeanConfig;
import com.ysy.oath.service.user.UserInfoService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;
import tk.mybatis.mapper.weekend.WeekendSqls;

import javax.annotation.Resource;

import static com.github.surpassm.common.jackson.Result.fail;
import static com.github.surpassm.common.jackson.Result.ok;


/**
 * @author mc
 * Create date 2019-03-14 20:41:03
 * Version 1.0
 * Description 实现类
 */
@Slf4j
@Service
@Transactional(rollbackFor = {RuntimeException.class, Exception.class})
public class UserInfoServiceImpl implements UserInfoService {
	@Resource
	private UserInfoMapper userInfoMapper;
	@Resource
	private BeanConfig beanConfig;
	@Resource
	private UserGroupMapper userGroupMapper;
	@Resource
	private UserMenuMapper userMenuMapper;
	@Resource
	private UserRoleMapper userRoleMapper;
	@Resource
	private BCryptPasswordEncoder bCryptPasswordEncoder;
	@Resource
	private DepartmentMapper departmentMapper;


	@Override
	public Result insert(String accessToken, UserInfo userInfo) {
		if (userInfo == null) {
			return fail(ResultCode.PARAM_IS_INVALID.getMsg());
		}
		if (userInfo.getMobile() != null){
			if (!ValidateUtil.isMobilePhone(userInfo.getMobile())){
				return fail(ResultCode.PARAM_IS_INVALID.getMsg());
			}
		}
		//效验手姓名
		if (!ValidateUtil.isRealName(userInfo.getName())){
			return fail("姓名格式错误");
		}
		if (userInfo.getUsername() == null || "".equals(userInfo.getUsername().trim())){
			return fail(ResultCode.PARAM_IS_INVALID.getMsg());
		}
		if (userInfo.getPassword() == null || "".equals(userInfo.getPassword().trim())){
			return fail(ResultCode.PARAM_IS_INVALID.getMsg());
		}
		if (!ValidateUtil.isPassword(userInfo.getPassword())){
			return fail(ResultCode.PARAM_IS_INVALID.getMsg());
		}
		Department department = departmentMapper.selectByPrimaryKey(userInfo.getDepartmentId());
		if (department == null) {
			return fail(ResultCode.RESULE_DATA_NONE.getMsg());
		}

		int count = userInfoMapper.selectCount(UserInfo.builder().username(userInfo.getUsername().trim()).isDelete(0).build());
		if (count != 0){
			return fail("账号已存在");
		}
		userInfo.setPassword(bCryptPasswordEncoder.encode(userInfo.getPassword().trim()));
		UserInfo loginUserInfo = beanConfig.getAccessToken(accessToken);
		userInfo.setUsername(userInfo.getUsername().trim());
		userInfo.setIsDelete(0);
		userInfoMapper.insert(userInfo);
		return ok();
	}

	@Override
	public Result update(String accessToken, UserInfo userInfo) {
		if (userInfo == null) {
			return fail(ResultCode.PARAM_IS_INVALID.getMsg());
		}
		UserInfo loginUserInfo = beanConfig.getAccessToken(accessToken);

		if (userInfo.getMobile() != null){
			if (!ValidateUtil.isMobilePhone(userInfo.getMobile())){
				return fail(ResultCode.PARAM_IS_INVALID.getMsg());
			}
		}
		//效验手姓名
		if (!ValidateUtil.isRealName(userInfo.getName())){
			return fail("姓名格式错误");
		}
		if (userInfo.getUsername() == null || "".equals(userInfo.getUsername().trim())){
			return fail(ResultCode.PARAM_IS_INVALID.getMsg());
		}
		if (userInfo.getPassword() == null || "".equals(userInfo.getPassword().trim())){
			return fail(ResultCode.PARAM_IS_INVALID.getMsg());
		}
		if (!ValidateUtil.isPassword(userInfo.getPassword())){
			return fail(ResultCode.PARAM_IS_INVALID.getMsg());
		}

		Department queryDepartment = Department.builder().id(userInfo.getDepartmentId()).build();
		queryDepartment.setIsDelete(0);
		Department department = departmentMapper.selectOne(queryDepartment);
		if (department == null) {
			return fail(ResultCode.RESULE_DATA_NONE.getMsg());
		}


		UserInfo user = userInfoMapper.selectByPrimaryKey(userInfo.getId());
		String password = userInfo.getPassword();
		//密码效验
		if (userInfo.getPassword() != null && !"".equals(userInfo.getPassword())) {
			if (!ValidateUtil.isPassword(userInfo.getPassword())) {
				return fail(ResultCode.PARAM_IS_INVALID.getMsg());
			}
			if (!bCryptPasswordEncoder.matches(password,user.getPassword())){
				String passwordNew = bCryptPasswordEncoder.encode(password);
				userInfo.setPassword(passwordNew);
			}
		}

		userInfoMapper.updateByPrimaryKeySelective(userInfo);
		return ok();
	}

	@Override
	public Result deleteGetById(String accessToken, Long id) {
		if (id == null) {
			return fail(ResultCode.PARAM_IS_INVALID.getMsg());
		}
		UserInfo userInfo = userInfoMapper.selectByPrimaryKey(id);
		if (userInfo == null) {
			return fail(ResultCode.RESULE_DATA_NONE.getMsg());
		}
		UserInfo loginUserInfo = beanConfig.getAccessToken(accessToken);
		userInfo.setIsDelete(1);
		userInfoMapper.updateByPrimaryKeySelective(userInfo);
		return ok();
	}


	@Override
	public Result findById(String accessToken, Long id) {
		if (id == null) {
			return fail(ResultCode.PARAM_IS_INVALID.getMsg());
		}
		UserInfo userInfo = userInfoMapper.selectByPrimaryKey(id);
		if (userInfo == null) {
			return fail(ResultCode.RESULE_DATA_NONE.getMsg());
		}
		return ok(userInfo);

	}

	@Override
	public Result pageQuery(String accessToken, Integer page, Integer size, String sort, UserInfo userInfo) {
		page = null == page ? 1 : page;
		size = null == size ? 10 : size;
		PageHelper.startPage(page, size);
		Example.Builder builder = new Example.Builder(UserInfo.class);
		builder.where(WeekendSqls.<UserInfo>custom().andEqualTo(UserInfo::getIsDelete, 0));
		if (userInfo != null) {
			if (userInfo.getId() != null) {
				builder.where(WeekendSqls.<UserInfo>custom().andEqualTo(UserInfo::getId, userInfo.getId()));
			}
			if (userInfo.getDepartmentId() != null) {
				builder.where(WeekendSqls.<UserInfo>custom().andEqualTo(UserInfo::getDepartmentId, userInfo.getDepartmentId()));
			}
			if (userInfo.getHeadUrl() != null && !"".equals(userInfo.getHeadUrl().trim())) {
				builder.where(WeekendSqls.<UserInfo>custom().andLike(UserInfo::getHeadUrl, "%" + userInfo.getHeadUrl() + "%"));
			}
			if (userInfo.getLandingTime() != null) {
				builder.where(WeekendSqls.<UserInfo>custom().andEqualTo(UserInfo::getLandingTime, userInfo.getLandingTime()));
			}
			if (userInfo.getMobile() != null && !"".equals(userInfo.getMobile().trim())) {
				builder.where(WeekendSqls.<UserInfo>custom().andLike(UserInfo::getMobile, "%" + userInfo.getMobile() + "%"));
			}
			if (userInfo.getName() != null && !"".equals(userInfo.getName().trim())) {
				builder.where(WeekendSqls.<UserInfo>custom().andLike(UserInfo::getName, "%" + userInfo.getName() + "%"));
			}
			if (userInfo.getPassword() != null && !"".equals(userInfo.getPassword().trim())) {
				builder.where(WeekendSqls.<UserInfo>custom().andLike(UserInfo::getPassword, "%" + userInfo.getPassword() + "%"));
			}
			if (userInfo.getUserInfoIndex() != null) {
				builder.where(WeekendSqls.<UserInfo>custom().andEqualTo(UserInfo::getUserInfoIndex, userInfo.getUserInfoIndex()));
			}
			if (userInfo.getUsername() != null && !"".equals(userInfo.getUsername().trim())) {
				builder.where(WeekendSqls.<UserInfo>custom().andLike(UserInfo::getUsername, "%" + userInfo.getUsername() + "%"));
			}
		}
		Page<UserInfo> all = (Page<UserInfo>) userInfoMapper.selectByExample(builder.build());
		return ok(all.toPageInfo());
	}

	/**
	 * 根据主键查询用户及角色、权限列表
	 *
	 * @param accessToken token
	 * @param id 系统标识
	 * @return 返回数据
	 */
	@Override
	public Result findRolesAndMenus(String accessToken, Long id) {
		beanConfig.getAccessToken(accessToken);
		UserInfo userInfo = userInfoMapper.selectByUserInfoAndRolesAndMenus(id);
		if (userInfo == null){
			return fail(ResultCode.RESULE_DATA_NONE.getMsg());
		}
		return ok(userInfo);
	}

	/**
	 * 设置用户、组
	 * @param accessToken token
	 * @param id 用户系统标识
	 * @param groupIds 组系统标识
	 * @return 返回数据
	 */
	@Override
	public Result setUserByGroup(String accessToken, Long id, String groupIds) {
		beanConfig.getAccessToken(accessToken);
		String[] splits = StringUtils.split(groupIds,",");
		if (splits == null || splits.length == 0){
			return fail(ResultCode.PARAM_IS_INVALID.getMsg());
		}
		int count = userInfoMapper.selectCount(UserInfo.builder().id(id).isDelete(0).build());
		if (count == 0){
			return fail(ResultCode.RESULE_DATA_NONE.getMsg());
		}
		//删除原有用户对应的组
		Example.Builder builder = new Example.Builder(UserGroup.class);
		builder.where(WeekendSqls.<UserGroup>custom().andEqualTo(UserGroup::getIsDelete, 0));
		builder.where(WeekendSqls.<UserGroup>custom().andEqualTo(UserGroup::getUserId, id));
		userGroupMapper.deleteByExample(builder.build());
		//新增现有的用户组
		for(String split: splits){
			UserGroup build = UserGroup.builder().userId(id).groupId(Long.valueOf(split)).build();
			build.setIsDelete(0);
			userGroupMapper.insert(build);
		}
		return ok();
	}

	/**
	 * 设置用户权限
	 * @param accessToken token
	 * @param id 用户系统标识
	 * @param menuIds 组系统标识
	 * @return 返回数据
	 */
	@Override
	public Result setUserByMenu(String accessToken, Long id, String menuIds) {
		beanConfig.getAccessToken(accessToken);
		String[] splits = StringUtils.split(menuIds,",");
		if (splits == null || splits.length == 0){
			return fail(ResultCode.PARAM_IS_INVALID.getMsg());
		}
		int count = userInfoMapper.selectCount(UserInfo.builder().id(id).isDelete(0).build());
		if (count == 0){
			return fail(ResultCode.RESULE_DATA_NONE.getMsg());
		}
		//删除原有用户对应的权限
		Example.Builder builder = new Example.Builder(UserMenu.class);
		builder.where(WeekendSqls.<UserMenu>custom().andEqualTo(UserMenu::getIsDelete, 0));
		builder.where(WeekendSqls.<UserMenu>custom().andEqualTo(UserMenu::getUserId, id));
		userMenuMapper.deleteByExample(builder.build());
		//新增现有的用户权限
		for(String split: splits){
			UserMenu build = UserMenu.builder().userId(id).menuId(Long.valueOf(split)).build();
			build.setIsDelete(0);
			userMenuMapper.insert(build);
		}
		return ok();
	}

	/**
	 * 设置用户、角色
	 * @param accessToken token
	 * @param id 用户系统标识
	 * @param roleIds 组系统标识
	 * @return 返回数据
	 */
	@Override
	public Result setUserByRoles(String accessToken, Long id, String roleIds) {
		UserInfo loginUser = beanConfig.getAccessToken(accessToken);
		String[] splits = StringUtils.split(roleIds,",");
		if (splits == null || splits.length == 0){
			return fail(ResultCode.PARAM_IS_INVALID.getMsg());
		}
		int count = userInfoMapper.selectCount(UserInfo.builder().id(id).isDelete(0).build());
		if (count == 0){
			return fail(ResultCode.RESULE_DATA_NONE.getMsg());
		}
		//删除原有用户对应的角色
		Example.Builder builder = new Example.Builder(UserRole.class);
		builder.where(WeekendSqls.<UserRole>custom().andEqualTo(UserRole::getIsDelete, 0));
		builder.where(WeekendSqls.<UserRole>custom().andEqualTo(UserRole::getUserId, id));
		userRoleMapper.deleteByExample(builder.build());
		//新增现有的用户角色
		for(String split: splits){
			UserRole build = UserRole.builder().userId(id).roleId(Long.valueOf(split)).build();
			build.setIsDelete(0);
			userRoleMapper.insert(build);
		}
		return ok();
	}
}

