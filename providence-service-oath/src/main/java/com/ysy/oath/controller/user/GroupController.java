package com.ysy.oath.controller.user;

import com.github.surpassm.common.constant.Constant;
import com.github.surpassm.common.jackson.Result;
import com.github.surpassm.common.service.InsertView;
import com.github.surpassm.common.service.UpdateView;
import com.github.surpassm.config.annotation.AuthorizationToken;
import com.ysy.oath.entity.user.Group;
import com.ysy.oath.service.user.GroupService;
import io.swagger.annotations.*;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

/**
 * @author mc
 * Create date 2019-03-14 20:41:03
 * Version 1.0
 * Description 权限控制层
 */
@CrossOrigin
@RestController
@RequestMapping("/group/")
@Api(tags = "组Api")
public class GroupController {

	@Resource
	private GroupService groupService;

	@PostMapping("v1/insert")
	@ApiOperation(value = "新增")
	@ApiResponses({
			@ApiResponse(code = Constant.SUCCESS_CODE, message = Constant.SUCCESS_MSG),
			@ApiResponse(code = Constant.FAIL_SESSION_CODE, message = Constant.FAIL_SESSION_MSG),
			@ApiResponse(code = Constant.FAIL_CODE, message = Constant.FAIL_MSG, response = Result.class)})
	public Result insert(@ApiParam(hidden = true) @AuthorizationToken String accessToken,
						 @Validated(InsertView.class) @RequestBody Group group, BindingResult errors) {
		if (errors.hasErrors()) {
			return Result.fail(errors.getAllErrors());
		}
		return groupService.insert(accessToken, group);
	}

	@PostMapping("v1/update")
	@ApiOperation(value = "修改")
	@ApiResponses({
			@ApiResponse(code = Constant.SUCCESS_CODE, message = Constant.SUCCESS_MSG),
			@ApiResponse(code = Constant.FAIL_SESSION_CODE, message = Constant.FAIL_SESSION_MSG),
			@ApiResponse(code = Constant.FAIL_CODE, message = Constant.FAIL_MSG, response = Result.class)})
	public Result update(@ApiParam(hidden = true) @AuthorizationToken String accessToken,
						 @Validated(UpdateView.class) @RequestBody Group group, BindingResult errors) {
		if (errors.hasErrors()) {
			return Result.fail(errors.getAllErrors());
		}
		return groupService.update(accessToken, group);
	}

	@PostMapping("v1/getById")
	@ApiOperation(value = "根据主键删除")
	@ApiResponses({
			@ApiResponse(code = Constant.SUCCESS_CODE, message = Constant.SUCCESS_MSG),
			@ApiResponse(code = Constant.FAIL_SESSION_CODE, message = Constant.FAIL_SESSION_MSG),
			@ApiResponse(code = Constant.FAIL_CODE, message = Constant.FAIL_MSG, response = Result.class)})
	public Result deleteGetById(@ApiParam(hidden = true) @AuthorizationToken String accessToken,
								@ApiParam(value = "主键", required = true) @RequestParam(value = "id") Long id) {
		return groupService.deleteGetById(accessToken, id);
	}

	@PostMapping("v1/findById")
	@ApiOperation(value = "根据主键查询")
	@ApiResponses({
			@ApiResponse(code = Constant.FAIL_SESSION_CODE, message = Constant.FAIL_SESSION_MSG),
			@ApiResponse(code = Constant.SUCCESS_CODE, message = Constant.SUCCESS_MSG, response = Group.class),
			@ApiResponse(code = Constant.FAIL_CODE, message = Constant.FAIL_MSG, response = Result.class)})
	public Result findById(@ApiParam(hidden = true) @AuthorizationToken String accessToken,
						   @ApiParam(value = "主键", required = true) @RequestParam(value = "id") Long id) {
		return groupService.findById(accessToken, id);
	}

	@PostMapping("v1/pageQuery")
	@ApiOperation(value = "条件分页查询")
	@ApiResponses({@ApiResponse(code = Constant.SUCCESS_CODE, message = Constant.SUCCESS_MSG, response = Group.class),
			@ApiResponse(code = Constant.FAIL_SESSION_CODE, message = Constant.FAIL_SESSION_MSG)})
	public Result pageQuery(@ApiParam(hidden = true) @AuthorizationToken String accessToken,
							@ApiParam(value = "第几页", required = true) @RequestParam(value = "page") Integer page,
							@ApiParam(value = "多少条", required = true) @RequestParam(value = "size") Integer size,
							@ApiParam(value = "排序字段") @RequestParam(value = "sort", required = false) String sort,
							@RequestBody Group group) {
		return groupService.pageQuery(accessToken, page, size, sort, group);
	}

	@PostMapping("v1/findChildren")
	@ApiOperation(value = "根据父级Id查询所有子级")
	@ApiResponses({
			@ApiResponse(code = Constant.FAIL_SESSION_CODE, message = Constant.FAIL_SESSION_MSG),
			@ApiResponse(code = Constant.SUCCESS_CODE, message = Constant.SUCCESS_MSG, response = Group.class),
			@ApiResponse(code = Constant.FAIL_CODE, message = Constant.FAIL_MSG, response = Result.class)})
	public Result getParentId(@ApiParam(hidden = true) @AuthorizationToken String accessToken,
							  @ApiParam(value = "主键", required = true) @RequestParam(value = "parentId") @NotNull Long parentId) {
		return groupService.getParentId(accessToken, parentId);
	}

	@PostMapping("v1/findByOnlyAndChildren")
	@ApiOperation(value = "根据主键查询自己和所有子级")
	@ApiResponses({
			@ApiResponse(code = Constant.FAIL_SESSION_CODE, message = Constant.FAIL_SESSION_MSG),
			@ApiResponse(code = Constant.SUCCESS_CODE, message = Constant.SUCCESS_MSG, response = Group.class),
			@ApiResponse(code = Constant.FAIL_CODE, message = Constant.FAIL_MSG, response = Result.class)})
	public Result findByOnlyAndChildren(@ApiParam(hidden = true) @AuthorizationToken String accessToken,
										@ApiParam(value = "主键", required = true) @RequestParam(value = "id") @NotNull Long id) {
		return groupService.findByOnlyAndChildren(accessToken, id);
	}

	@PostMapping("v1/setGroupByMenu")
	@ApiOperation(value = "设置组的权限", notes = "每次均需传全部权限ID，会把组原有的所有权限做物理删除")
	@ApiResponses({@ApiResponse(code = Constant.FAIL_SESSION_CODE, message = Constant.FAIL_SESSION_MSG),
			@ApiResponse(code = Constant.SUCCESS_CODE, message = Constant.SUCCESS_MSG),
			@ApiResponse(code = Constant.FAIL_CODE, message = Constant.FAIL_MSG, response = Result.class)})
	public Result setGroupByMenu(@ApiParam(hidden = true) @AuthorizationToken String accessToken,
								 @ApiParam(value = "角色系统标识", required = true) @RequestParam(value = "id") @NotNull Long id,
								 @ApiParam(value = "权限系统标识 多个权限请使用 ，分割", required = true) @RequestParam(value = "menuId") @NotEmpty String menuId) {
		return groupService.setGroupByMenu(accessToken, id, menuId);
	}

	@PostMapping("v1/setGroupByRole")
	@ApiOperation(value = "设置组的角色", notes = "每次均需传全部角色ID，会把组原有的所有角色做物理删除")
	@ApiResponses({@ApiResponse(code = Constant.FAIL_SESSION_CODE, message = Constant.FAIL_SESSION_MSG),
			@ApiResponse(code = Constant.SUCCESS_CODE, message = Constant.SUCCESS_MSG),
			@ApiResponse(code = Constant.FAIL_CODE, message = Constant.FAIL_MSG, response = Result.class)})
	public Result setGroupByRole(@ApiParam(hidden = true) @AuthorizationToken String accessToken,
								 @ApiParam(value = "角色系统标识", required = true) @RequestParam(value = "id") @NotNull Long id,
								 @ApiParam(value = "角色系统标识 多个角色请使用 ，分割", required = true) @RequestParam(value = "roleIds") @NotEmpty String roleIds) {
		return groupService.setGroupByRole(accessToken, id, roleIds);
	}

	@PostMapping("v1/findGroupToMenu")
	@ApiOperation(value = "查询组的权限")
	public Result findGroupToMenu(@ApiParam(hidden = true) @AuthorizationToken String accessToken,
								  @ApiParam(value = "主键", required = true) @RequestParam(value = "groupId") @NotNull Long groupId,
								  @ApiParam(value = "第几页", required = true) @RequestParam(value = "page") Integer page,
								  @ApiParam(value = "多少条", required = true) @RequestParam(value = "size") Integer size,
								  @ApiParam(value = "排序字段") @RequestParam(value = "sort", required = false) String sort) {
		return groupService.findGroupToMenu(accessToken, groupId, page, size, sort);
	}

	@PostMapping("v1/findGroupToRole")
	@ApiOperation(value = "查询组的角色")
	public Result findGroupToRole(@ApiParam(hidden = true) @AuthorizationToken String accessToken,
								  @ApiParam(value = "主键", required = true) @RequestParam(value = "groupId") @NotNull Long groupId,
								  @ApiParam(value = "第几页", required = true) @RequestParam(value = "page") Integer page,
								  @ApiParam(value = "多少条", required = true) @RequestParam(value = "size") Integer size,
								  @ApiParam(value = "排序字段") @RequestParam(value = "sort", required = false) String sort) {
		return groupService.findGroupToRole(accessToken, groupId, page, size, sort);
	}
}
