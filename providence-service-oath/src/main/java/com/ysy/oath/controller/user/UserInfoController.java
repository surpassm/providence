package com.ysy.oath.controller.user;

import com.github.surpassm.common.constant.Constant;
import com.github.surpassm.common.jackson.Result;
import com.github.surpassm.common.service.InsertView;
import com.github.surpassm.common.service.UpdateView;
import com.github.surpassm.config.annotation.AuthorizationToken;
import com.ysy.oath.entity.user.UserInfo;
import com.ysy.oath.service.user.UserInfoService;
import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.concurrent.Callable;

/**
  * @author mc
  * Create date 2019-03-14 20:41:03
  * Version 1.0
  * Description 控制层
  */
@Slf4j
@CrossOrigin
@RestController
@RequestMapping("/userInfo/")
@Api(tags  =  "用户Api")
public class UserInfoController {

    @Resource
    private UserInfoService userInfoService;

    @PostMapping("v1/insert")
    @ApiOperation(value = "新增")
    @ApiResponses({
            @ApiResponse(code=Constant.SUCCESS_CODE,message=Constant.SUCCESS_MSG),
            @ApiResponse(code=Constant.FAIL_SESSION_CODE,message=Constant.FAIL_SESSION_MSG),
            @ApiResponse(code=Constant.FAIL_CODE,message=Constant.FAIL_MSG,response=Result.class)})
    public Result insert(@ApiParam(hidden = true)@AuthorizationToken String accessToken,
						 @Validated(InsertView.class)@RequestBody UserInfo userInfo, BindingResult errors) {
        if (errors.hasErrors()){
			return Result.fail(errors.getAllErrors());
		}
        return userInfoService.insert(accessToken,userInfo);

    }

    @PostMapping("v1/update")
    @ApiOperation(value = "修改")
    @ApiResponses({
            @ApiResponse(code=Constant.SUCCESS_CODE,message=Constant.SUCCESS_MSG),
            @ApiResponse(code=Constant.FAIL_SESSION_CODE,message=Constant.FAIL_SESSION_MSG),
            @ApiResponse(code=Constant.FAIL_CODE,message=Constant.FAIL_MSG,response=Result.class)})
    public Result update(@ApiParam(hidden = true)@AuthorizationToken String accessToken,
						 @Validated(UpdateView.class)@RequestBody UserInfo userInfo, BindingResult errors) {
        if (errors.hasErrors()){
			return Result.fail(errors.getAllErrors());
		}
        return userInfoService.update(accessToken,userInfo);
    }

    @PostMapping("v1/getById")
    @ApiOperation(value = "根据主键删除")
    @ApiResponses({
            @ApiResponse(code=Constant.SUCCESS_CODE,message=Constant.SUCCESS_MSG),
            @ApiResponse(code=Constant.FAIL_SESSION_CODE,message=Constant.FAIL_SESSION_MSG),
            @ApiResponse(code=Constant.FAIL_CODE,message=Constant.FAIL_MSG,response=Result.class)})
    public Result deleteGetById(@ApiParam(hidden = true)@AuthorizationToken String accessToken,
                                @ApiParam(value = "主键",required = true)@RequestParam(value = "id") Long id) {
        return userInfoService.deleteGetById(accessToken,id);
    }

    @PostMapping("v1/findById")
    @ApiOperation(value = "根据主键查询")
    @ApiResponses({
            @ApiResponse(code=Constant.FAIL_SESSION_CODE,message=Constant.FAIL_SESSION_MSG),
            @ApiResponse(code=Constant.SUCCESS_CODE,message=Constant.SUCCESS_MSG,response=UserInfo.class),
            @ApiResponse(code=Constant.FAIL_CODE,message=Constant.FAIL_MSG,response=Result.class)})
    public Result findById(@ApiParam(hidden = true)@AuthorizationToken String accessToken,
                           @ApiParam(value = "主键",required = true)@RequestParam(value = "id") Long id) {
        return userInfoService.findById(accessToken,id);
    }

    @PostMapping("v1/pageQuery")
    @ApiOperation(value = "条件分页查询")
    @ApiResponses({@ApiResponse(code=Constant.SUCCESS_CODE,message=Constant.SUCCESS_MSG,response=UserInfo.class),
                   @ApiResponse(code=Constant.FAIL_SESSION_CODE,message=Constant.FAIL_SESSION_MSG)})
    public Result pageQuery(@ApiParam(hidden = true)@AuthorizationToken String accessToken,
                            @ApiParam(value = "第几页", required = true) @RequestParam(value = "page") Integer page,
                            @ApiParam(value = "多少条",required = true)@RequestParam(value = "size") Integer size,
                            @ApiParam(value = "排序字段")@RequestParam(value = "sort",required = false) String sort,
							@RequestBody UserInfo userInfo) {
        return userInfoService.pageQuery(accessToken,page, size, sort, userInfo);
    }

	@PostMapping("v1/findRolesAndMenus")
	@ApiOperation(value = "根据主键查询用户及角色、权限列表")
	@ApiResponses({@ApiResponse(code=Constant.FAIL_SESSION_CODE,message=Constant.FAIL_SESSION_MSG),
			@ApiResponse(code=Constant.SUCCESS_CODE,message=Constant.SUCCESS_MSG,response=UserInfo.class),
			@ApiResponse(code=Constant.FAIL_CODE,message=Constant.FAIL_MSG,response=Result.class)})
	public Result findRolesAndMenus(@ApiParam(hidden = true)@AuthorizationToken String accessToken,
									@ApiParam(value = "用户系统标识",required = true)@RequestParam(value = "id")@NotNull Long id) {
		return userInfoService.findRolesAndMenus(accessToken,id);
	}

	@PostMapping("v1/setUserByGroups")
	@ApiOperation(value = "设置用户、组",notes = "每次均需传全部组ID，会把用户原有的所有组做物理删除")
	@ApiResponses({@ApiResponse(code=Constant.FAIL_SESSION_CODE,message=Constant.FAIL_SESSION_MSG),
			@ApiResponse(code=Constant.SUCCESS_CODE,message=Constant.SUCCESS_MSG),
			@ApiResponse(code=Constant.FAIL_CODE,message=Constant.FAIL_MSG,response=Result.class)})
	public Result setUserByGroup(@ApiParam(hidden = true)@AuthorizationToken String accessToken,
										 @ApiParam(value = "用户系统标识",required = true)@RequestParam(value = "id")@NotNull Long id,
										 @ApiParam(value = "组系统标识 多个组请使用 ，分割",required = true)@RequestParam(value = "groupId")@NotEmpty String groupIds) {
    	return userInfoService.setUserByGroup(accessToken,id,groupIds);
	}

	@PostMapping("v1/setUserByMenus")
	@ApiOperation(value = "设置用户、权限",notes = "每次均需传全部权限ID，会把用户原有的所有权限做物理删除")
	@ApiResponses({@ApiResponse(code=Constant.FAIL_SESSION_CODE,message=Constant.FAIL_SESSION_MSG),
			@ApiResponse(code=Constant.SUCCESS_CODE,message=Constant.SUCCESS_MSG),
			@ApiResponse(code=Constant.FAIL_CODE,message=Constant.FAIL_MSG,response=Result.class)})
	public Result setUserByMenu(@ApiParam(hidden = true)@AuthorizationToken String accessToken,
								@ApiParam(value = "用户系统标识",required = true)@RequestParam(value = "id")@NotNull Long id,
								@ApiParam(value = "权限系统标识 多个权限请使用 ，分割",required = true)@RequestParam(value = "menuIds")@NotEmpty String menuIds) {
		return userInfoService.setUserByMenu(accessToken,id,menuIds);
	}
	@PostMapping("v1/setUserByRoles")
	@ApiOperation(value = "设置用户、角色",notes = "每次均需传全部角色ID，会把用户原有的所有角色做物理删除")
	@ApiResponses({@ApiResponse(code=Constant.FAIL_SESSION_CODE,message=Constant.FAIL_SESSION_MSG),
			@ApiResponse(code=Constant.SUCCESS_CODE,message=Constant.SUCCESS_MSG),
			@ApiResponse(code=Constant.FAIL_CODE,message=Constant.FAIL_MSG,response=Result.class)})
	public Callable setUserByRoles(@ApiParam(hidden = true)@AuthorizationToken String accessToken,
								 @ApiParam(value = "用户系统标识",required = true)@RequestParam(value = "id")@NotNull Long id,
								 @ApiParam(value = "角色系统标识 多个权限请使用 ，分割",required = true)@RequestParam(value = "roleIds")@NotEmpty String roleIds) {
		log.info("主线程开始");
		Callable callable = () -> {
			log.info("副线程开始");
			Result result = userInfoService.setUserByRoles(accessToken,id,roleIds);
			log.info("副线程返回");
			return result;
		};
		log.info("主线程返回");
		return callable ;

	}
}
