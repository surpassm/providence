package com.ysy.oath.service.user.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.surpassm.common.jackson.Result;
import com.github.surpassm.common.jackson.ResultCode;
import com.ysy.oath.entity.user.Department;
import com.ysy.oath.entity.user.Region;
import com.ysy.oath.entity.user.UserInfo;
import com.ysy.oath.mapper.user.DepartmentMapper;
import com.ysy.oath.mapper.user.RegionMapper;
import com.ysy.oath.security.BeanConfig;
import com.ysy.oath.service.user.RegionService;
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
 * Description 区域实现类
 */
@Slf4j
@Service
@Transactional(rollbackFor = {RuntimeException.class, Exception.class})
public class RegionServiceImpl implements RegionService {
	@Resource
	private RegionMapper regionMapper;
	@Resource
	private BeanConfig beanConfig;
	@Resource
	private DepartmentMapper departmentMapper;

	@Override
	public Result insert(String accessToken, Region region) {
		if (region == null) {
			return fail(ResultCode.PARAM_IS_INVALID.getMsg());
		}
		UserInfo loginUserInfo = beanConfig.getAccessToken(accessToken);
		//效验名称是否重复
		Region build = Region.builder().name(region.getName()).build();
		build.setIsDelete(0);
		int groupCount = regionMapper.selectCount(build);
		if (groupCount != 0) {
			return fail(ResultCode.DATA_ALREADY_EXISTED.getMsg());
		}
		//查看父级是否存在
		if (isEnableParent(region)) {
			return fail(ResultCode.PARAM_TYPE_BIND_ERROR.getMsg());
		}

		region.setIsDelete(0);
		regionMapper.insert(region);
		return ok();
	}

	@Override
	public Result update(String accessToken, Region region) {
		if (region == null) {
			return fail(ResultCode.PARAM_IS_INVALID.getMsg());
		}
		if (region.getIsDelete() == 1){
			return fail(ResultCode.PARAM_IS_INVALID.getMsg());
		}
		UserInfo loginUserInfo = beanConfig.getAccessToken(accessToken);

		Example.Builder builder = new Example.Builder(Region.class);
		builder.where(WeekendSqls.<Region>custom().andEqualTo(Region::getIsDelete, 0));
		builder.where(WeekendSqls.<Region>custom().andEqualTo(Region::getName, region.getName()));
		builder.where(WeekendSqls.<Region>custom().andNotIn(Region::getId, Collections.singletonList(region.getId())));

		List<Region> selectCount = regionMapper.selectByExample(builder.build());
		if (selectCount.size() != 0) {
			return fail(ResultCode.DATA_ALREADY_EXISTED.getMsg());
		}
		if (isEnableParent(region)) {
			return fail(ResultCode.PARAM_TYPE_BIND_ERROR.getMsg());
		}


		regionMapper.updateByPrimaryKeySelective(region);
		return ok();
	}

	private boolean isEnableParent(Region region) {
		if (region.getParentId() != null) {
			Region buildRegion = Region.builder().id(region.getParentId()).build();
			buildRegion.setIsDelete(0);
			int buildRegionCount = regionMapper.selectCount(buildRegion);
			return buildRegionCount == 0;
		}
		return false;
	}

	@Override
	public Result deleteGetById(String accessToken, Long id) {
		if (id == null) {
			return fail(ResultCode.PARAM_IS_INVALID.getMsg());
		}
		Region region = regionMapper.selectByPrimaryKey(id);
		if (region == null) {
			return fail(ResultCode.RESULE_DATA_NONE.getMsg());
		}
		UserInfo loginUserInfo = beanConfig.getAccessToken(accessToken);
		//子级查询
		Region regionBuild = Region.builder().parentId(id).build();
		regionBuild.setIsDelete(0);
		int regionCount = regionMapper.selectCount(regionBuild);
		if (regionCount != 0){
			return fail("存在下级关联，无法删除");
		}
		Department department = new Department();
		department.setIsDelete(0);
		int departmentCount = departmentMapper.selectCount(department);
		if (departmentCount !=0){
			return fail("存在关联用户，无法删除");
		}
		region.setIsDelete(1);
		regionMapper.updateByPrimaryKeySelective(region);
		return ok();
	}


	@Override
	public Result findById(String accessToken, Long id) {
		if (id == null) {
			return fail(ResultCode.PARAM_IS_INVALID.getMsg());
		}
		Region region = regionMapper.selectByPrimaryKey(id);
		if (region == null) {
			return fail(ResultCode.RESULE_DATA_NONE.getMsg());
		}
		return ok(region);

	}

	@Override
	public Result pageQuery(String accessToken, Integer page, Integer size, String sort, Region region) {
		page = null == page ? 1 : page;
		size = null == size ? 10 : size;
		PageHelper.startPage(page, size);
		Example.Builder builder = new Example.Builder(Region.class);
		builder.where(WeekendSqls.<Region>custom().andEqualTo(Region::getIsDelete, 0));
		if (region != null) {
			if (region.getId() != null) {
				builder.where(WeekendSqls.<Region>custom().andEqualTo(Region::getId, region.getId()));
			}
			if (region.getDepartmentIndex() != null) {
				builder.where(WeekendSqls.<Region>custom().andEqualTo(Region::getDepartmentIndex, region.getDepartmentIndex()));
			}
			if (region.getName() != null && !"".equals(region.getName().trim())) {
				builder.where(WeekendSqls.<Region>custom().andLike(Region::getName, "%" + region.getName() + "%"));
			}
			if (region.getParentId() != null) {
				builder.where(WeekendSqls.<Region>custom().andEqualTo(Region::getParentId, region.getParentId()));
			} else {
				builder.where(WeekendSqls.<Region>custom().andIsNull(Region::getParentId));
			}
		} else {
			builder.where(WeekendSqls.<Region>custom().andIsNull(Region::getParentId));
		}
		Page<Region> all = (Page<Region>) regionMapper.selectByExample(builder.build());
		return ok(all.toPageInfo());
	}

	@Override
	public Result getParentId(String accessToken, Long parentId) {
		List<Region> regions = regionMapper.selectChildByParentId(parentId);
		return ok(regions);
	}

	@Override
	public Result findByOnlyAndChildren(String accessToken, Long id) {
		List<Region> regions = regionMapper.selectSelfAndChildByParentId(id);
		return ok(regions);
	}
}

