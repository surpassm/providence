package com.ysy.oath.service.user.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.surpassm.common.jackson.Result;
import com.github.surpassm.common.jackson.ResultCode;
import com.ysy.oath.entity.user.Department;
import com.ysy.oath.entity.user.UserInfo;
import com.ysy.oath.mapper.user.DepartmentMapper;
import com.ysy.oath.mapper.user.RegionMapper;
import com.ysy.oath.mapper.user.UserInfoMapper;
import com.ysy.oath.security.BeanConfig;
import com.ysy.oath.service.user.DepartmentService;
import lombok.extern.slf4j.Slf4j;
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
 * Description 部门实现类
 */
@Slf4j
@Service
@Transactional(rollbackFor = {RuntimeException.class, Exception.class})
public class DepartmentServiceImpl implements DepartmentService {
	@Resource
	private DepartmentMapper departmentMapper;
	@Resource
	private BeanConfig beanConfig;
	@Resource
	private UserInfoMapper userInfoMapper;
	@Resource
	private RegionMapper regionMapper;
	@Override
	public Result insert(String accessToken, Department department) {
		if (department == null) {
			return fail(ResultCode.PARAM_IS_INVALID.getMsg());
		}

		UserInfo loginUserInfo = beanConfig.getAccessToken(accessToken);
		Department build = Department.builder().name(department.getName()).build();
		build.setIsDelete(0);
		int selectCount = departmentMapper.selectCount(build);
		if (selectCount != 0) {
			return fail(ResultCode.RESULE_DATA_NONE.getMsg());
		}
		if (isEnableParent(department)){ return fail(ResultCode.RESULE_DATA_NONE.getMsg());}
		department.setIsDelete(0);
		departmentMapper.insert(department);
		return ok();
	}

	@Override
	public Result update(String accessToken, Department department) {
		if (department == null) {
			return fail(ResultCode.PARAM_IS_INVALID.getMsg());
		}
		Department department1 = departmentMapper.selectByPrimaryKey(department.getId());
		if (department1.getIsDelete() == 1){
			return fail(ResultCode.RESULE_DATA_NONE.getMsg());
		}

		UserInfo loginUserInfo = beanConfig.getAccessToken(accessToken);

		Example.Builder builder = new Example.Builder(Department.class);
		builder.where(WeekendSqls.<Department>custom().andEqualTo(Department::getIsDelete, 0));
		builder.where(WeekendSqls.<Department>custom().andEqualTo(Department::getName, department.getName()));
		builder.where(WeekendSqls.<Department>custom().andNotIn(Department::getId, Collections.singletonList(department.getId())));

		List<Department> selectCount = departmentMapper.selectByExample(builder.build());
		if (selectCount.size() != 0) {
			return fail(ResultCode.RESULE_DATA_NONE.getMsg());
		}
		if (isEnableParent(department)){ return fail(ResultCode.RESULE_DATA_NONE.getMsg());}

		departmentMapper.updateByPrimaryKeySelective(department);
		return ok();
	}

	private boolean isEnableParent(Department department) {
		if (department.getParentId() != null) {
			Department buildDepartment = Department.builder().id(department.getParentId()).build();
			buildDepartment.setIsDelete(0);
			int buildDepartmentCount = departmentMapper.selectCount(buildDepartment);
			return buildDepartmentCount == 0;
		}
		return false;
	}

	@Override
	public Result deleteGetById(String accessToken, Long id) {
		if (id == null) {
			return fail(ResultCode.PARAM_IS_INVALID.getMsg());
		}
		Department department = departmentMapper.selectByPrimaryKey(id);
		if (department == null) {
			return fail(ResultCode.RESULE_DATA_NONE.getMsg());
		}
		UserInfo loginUserInfo = beanConfig.getAccessToken(accessToken);

		Department build = Department.builder().parentId(department.getId()).build();
		build.setIsDelete(0);
		int selectCount = departmentMapper.selectCount(build);
		if (selectCount != 0) {
			return fail("存在下级关联数据无法删除");
		}
		int userCount = userInfoMapper.selectCount(UserInfo.builder().id(id).isDelete(0).build());
		if (userCount != 0) {
			return fail("存在关联用户，无法删除");
		}
		department.setIsDelete(1);
		departmentMapper.updateByPrimaryKeySelective(department);
		return ok();
	}


	@Override
	public Result findById(String accessToken, Long id) {
		if (id == null) {
			return fail(ResultCode.PARAM_IS_INVALID.getMsg());
		}
		Department department = departmentMapper.selectByPrimaryKey(id);
		if (department == null) {
			return fail(ResultCode.RESULE_DATA_NONE.getMsg());
		}
		return ok(department);

	}

	@Override
	public Result pageQuery(String accessToken, Integer page, Integer size, String sort, Department department) {
		page = null == page ? 1 : page;
		size = null == size ? 10 : size;
		PageHelper.startPage(page, size);
		Example.Builder builder = new Example.Builder(Department.class);
		builder.where(WeekendSqls.<Department>custom().andEqualTo(Department::getIsDelete, 0));
		if (department != null) {
			if (department.getId() != null) {
				builder.where(WeekendSqls.<Department>custom().andEqualTo(Department::getId, department.getId()));
			}
			if (department.getDepartmentIndex() != null) {
				builder.where(WeekendSqls.<Department>custom().andEqualTo(Department::getDepartmentIndex, department.getDepartmentIndex()));
			}
			if (department.getName() != null && !"".equals(department.getName().trim())) {
				builder.where(WeekendSqls.<Department>custom().andLike(Department::getName, "%" + department.getName() + "%"));
			}
			if (department.getDepartmentIndex() == null){
				builder.where(WeekendSqls.<Department>custom().andIsNull(Department::getParentId));
			}else {
				builder.where(WeekendSqls.<Department>custom().andEqualTo(Department::getParentId, department.getParentId()));
			}
		}else {
			builder.where(WeekendSqls.<Department>custom().andIsNull(Department::getParentId));
		}
		Page<Department> all = (Page<Department>) departmentMapper.selectByExample(builder.build());
		return ok(all.toPageInfo());
	}

	@Override
	public Result getParentId(String accessToken, Integer parentId) {
		List<Department> departments = departmentMapper.selectChildByParentId(parentId);
		return ok(departments);
	}

	@Override
	public Result findByOnlyAndChildren(String accessToken, Integer id) {
		List<Department> departments = departmentMapper.selectSelfAndChildByParentId(id);
		return ok(departments);
	}
}

