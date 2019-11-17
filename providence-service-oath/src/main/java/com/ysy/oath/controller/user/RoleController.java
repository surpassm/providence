package com.ysy.oath.controller.user;

import com.github.surpassm.common.constant.Constant;
import com.github.surpassm.common.jackson.Result;
import com.github.surpassm.common.service.InsertView;
import com.github.surpassm.common.service.UpdateView;
import com.github.surpassm.config.annotation.AuthorizationToken;
import com.ysy.oath.entity.user.Role;
import com.ysy.oath.service.user.RoleService;
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
  * Description 控制层
  */
@CrossOrigin
@RestController
@RequestMapping("/role/")
@Api(tags  =  "角色Api")
public class RoleController {

    @Resource
    private RoleService roleService;

    @PostMapping("v1/insert")
    @ApiOperation(value = "新增")
    @ApiResponses({
            @ApiResponse(code=Constant.SUCCESS_CODE,message=Constant.SUCCESS_MSG),
            @ApiResponse(code=Constant.FAIL_SESSION_CODE,message=Constant.FAIL_SESSION_MSG),
            @ApiResponse(code=Constant.FAIL_CODE,message=Constant.FAIL_MSG,response=Result.class)})
    public Result insert(@ApiParam(hidden = true)@AuthorizationToken String accessToken,
						 @Validated(InsertView.class)@RequestBody Role role, BindingResult errors) {
        if (errors.hasErrors()){
			return Result.fail(errors.getAllErrors());
		}
        return roleService.insert(accessToken,role);
    }

    @PostMapping("v1/update")
    @ApiOperation(value = "修改")
    @ApiResponses({
            @ApiResponse(code=Constant.SUCCESS_CODE,message=Constant.SUCCESS_MSG),
            @ApiResponse(code=Constant.FAIL_SESSION_CODE,message=Constant.FAIL_SESSION_MSG),
            @ApiResponse(code=Constant.FAIL_CODE,message=Constant.FAIL_MSG,response=Result.class)})
    public Result update(@ApiParam(hidden = true)@AuthorizationToken String accessToken,
						 @Validated(UpdateView.class) @RequestBody Role role, BindingResult errors) {
        if (errors.hasErrors()){
			return Result.fail(errors.getAllErrors());
		}
        return roleService.update(accessToken,role);
    }

    @PostMapping("v1/getById")
    @ApiOperation(value = "根据主键删除")
    @ApiResponses({
            @ApiResponse(code=Constant.SUCCESS_CODE,message=Constant.SUCCESS_MSG),
            @ApiResponse(code=Constant.FAIL_SESSION_CODE,message=Constant.FAIL_SESSION_MSG),
            @ApiResponse(code=Constant.FAIL_CODE,message=Constant.FAIL_MSG,response=Result.class)})
    public Result deleteGetById(@ApiParam(hidden = true)@AuthorizationToken String accessToken,
                                @ApiParam(value = "主键",required = true)@RequestParam(value = "id") Long id) {
        return roleService.deleteGetById(accessToken,id);
    }

    @PostMapping("v1/findById")
    @ApiOperation(value = "根据主键查询")
    @ApiResponses({
            @ApiResponse(code=Constant.FAIL_SESSION_CODE,message=Constant.FAIL_SESSION_MSG),
            @ApiResponse(code=Constant.SUCCESS_CODE,message=Constant.SUCCESS_MSG,response=Role.class),
            @ApiResponse(code=Constant.FAIL_CODE,message=Constant.FAIL_MSG,response=Result.class)})
    public Result findById(@ApiParam(hidden = true)@AuthorizationToken String accessToken,
                           @ApiParam(value = "主键",required = true)@RequestParam(value = "id") Long id) {
        return roleService.findById(accessToken,id);
    }

    @PostMapping("v1/pageQuery")
    @ApiOperation(value = "条件分页查询")
    @ApiResponses({@ApiResponse(code=Constant.SUCCESS_CODE,message=Constant.SUCCESS_MSG,response=Role.class),
                   @ApiResponse(code=Constant.FAIL_SESSION_CODE,message=Constant.FAIL_SESSION_MSG)})
    public Result pageQuery(@ApiParam(hidden = true)@AuthorizationToken String accessToken,
                            @ApiParam(value = "第几页", required = true) @RequestParam(value = "page") Integer page,
                            @ApiParam(value = "多少条",required = true)@RequestParam(value = "size") Integer size,
                            @ApiParam(value = "排序字段")@RequestParam(value = "sort",required = false) String sort,
							@RequestBody Role role) {
        return roleService.pageQuery(accessToken,page, size, sort, role);
    }

	@PostMapping("v1/findMenus")
	@ApiOperation(value = "根据主键查询角色及权限列表")
	@ApiResponses({@ApiResponse(code=Constant.FAIL_SESSION_CODE,message=Constant.FAIL_SESSION_MSG),
				   @ApiResponse(code=Constant.SUCCESS_CODE,message=Constant.SUCCESS_MSG,response=Role.class),
				   @ApiResponse(code=Constant.FAIL_CODE,message=Constant.FAIL_MSG,response=Result.class)})
	public Result findMenus(@ApiParam(hidden = true)@AuthorizationToken String accessToken,
						    @ApiParam(value = "主键",required = true)@RequestParam(value = "id")@NotNull Long id) {
		return roleService.findMenus(accessToken,id);
	}

	@PostMapping("v1/setRoleByMenu")
	@ApiOperation(value = "设置角色权限",notes = "每次均需传全部权限ID，会把角色原有的所有权限做物理删除")
	@ApiResponses({@ApiResponse(code=Constant.FAIL_SESSION_CODE,message=Constant.FAIL_SESSION_MSG),
			@ApiResponse(code=Constant.SUCCESS_CODE,message=Constant.SUCCESS_MSG),
			@ApiResponse(code=Constant.FAIL_CODE,message=Constant.FAIL_MSG,response=Result.class)})
	public Result setRoleByMenu(@ApiParam(hidden = true)@AuthorizationToken String accessToken,
								@ApiParam(value = "角色系统标识",required = true)@RequestParam(value = "id")@NotNull Long id,
								@ApiParam(value = "权限系统标识 多个权限请使用 ，分割",required = true)@RequestParam(value = "menuId")@NotEmpty String menuId) {
		return roleService.setRoleByMenu(accessToken,id,menuId);
	}

}
