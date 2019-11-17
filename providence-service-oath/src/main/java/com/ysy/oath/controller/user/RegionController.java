package com.ysy.oath.controller.user;

import com.github.surpassm.common.constant.Constant;
import com.github.surpassm.common.jackson.Result;
import com.github.surpassm.common.service.InsertView;
import com.github.surpassm.common.service.UpdateView;
import com.github.surpassm.config.annotation.AuthorizationToken;
import com.ysy.oath.entity.user.Region;
import com.ysy.oath.service.user.RegionService;
import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.constraints.NotNull;
import java.util.concurrent.Callable;

/**
  * @author mc
  * Create date 2019-03-14 20:41:03
  * Version 1.0
  * Description 区域控制层
  */
@Slf4j
@CrossOrigin
@RestController
@RequestMapping("/region/")
@Api(tags  =  "区域Api")
public class RegionController {

    @Resource
    private RegionService regionService;

    @PostMapping("v1/insert")
    @ApiOperation(value = "新增")
    @ApiResponses({
            @ApiResponse(code=Constant.SUCCESS_CODE,message=Constant.SUCCESS_MSG),
            @ApiResponse(code=Constant.FAIL_SESSION_CODE,message=Constant.FAIL_SESSION_MSG),
            @ApiResponse(code=Constant.FAIL_CODE,message=Constant.FAIL_MSG,response=Result.class)})
    public Result insert(@ApiParam(hidden = true)@AuthorizationToken String accessToken,
						 @Validated(InsertView.class)@RequestBody Region region, BindingResult errors) {
        if (errors.hasErrors()){
			return Result.fail(errors.getAllErrors());
		}
        return regionService.insert(accessToken,region);
    }

    @PostMapping("v1/update")
    @ApiOperation(value = "修改")
    @ApiResponses({
            @ApiResponse(code=Constant.SUCCESS_CODE,message=Constant.SUCCESS_MSG),
            @ApiResponse(code=Constant.FAIL_SESSION_CODE,message=Constant.FAIL_SESSION_MSG),
            @ApiResponse(code=Constant.FAIL_CODE,message=Constant.FAIL_MSG,response=Result.class)})
    public Result update(@ApiParam(hidden = true)@AuthorizationToken String accessToken,
						 @Validated(UpdateView.class)@RequestBody Region region, BindingResult errors) {
        if (errors.hasErrors()){
			return Result.fail(errors.getAllErrors());
		}
        return regionService.update(accessToken,region);
    }

    @PostMapping("v1/getById")
    @ApiOperation(value = "根据主键删除")
    @ApiResponses({
            @ApiResponse(code=Constant.SUCCESS_CODE,message=Constant.SUCCESS_MSG),
            @ApiResponse(code=Constant.FAIL_SESSION_CODE,message=Constant.FAIL_SESSION_MSG),
            @ApiResponse(code=Constant.FAIL_CODE,message=Constant.FAIL_MSG,response=Result.class)})
    public Result deleteGetById(@ApiParam(hidden = true)@AuthorizationToken String accessToken,
                                @ApiParam(value = "主键",required = true)@RequestParam(value = "id") Long id) {
        return regionService.deleteGetById(accessToken,id);
    }

    @PostMapping("v1/findById")
    @ApiOperation(value = "根据主键查询")
    @ApiResponses({
            @ApiResponse(code=Constant.FAIL_SESSION_CODE,message=Constant.FAIL_SESSION_MSG),
            @ApiResponse(code=Constant.SUCCESS_CODE,message=Constant.SUCCESS_MSG,response=Region.class),
            @ApiResponse(code=Constant.FAIL_CODE,message=Constant.FAIL_MSG,response=Result.class)})
    public Result findById(@ApiParam(hidden = true)@AuthorizationToken String accessToken,
                           @ApiParam(value = "主键",required = true)@RequestParam(value = "id") Long id) {
        return regionService.findById(accessToken,id);
    }

    @PostMapping("v1/pageQuery")
    @ApiOperation(value = "条件分页查询")
    @ApiResponses({@ApiResponse(code=Constant.SUCCESS_CODE,message=Constant.SUCCESS_MSG,response=Region.class),
                   @ApiResponse(code=Constant.FAIL_SESSION_CODE,message=Constant.FAIL_SESSION_MSG)})
    public Result pageQuery(@ApiParam(hidden = true)@AuthorizationToken String accessToken,
                            @ApiParam(value = "第几页", required = true) @RequestParam(value = "page") Integer page,
                            @ApiParam(value = "多少条",required = true)@RequestParam(value = "size") Integer size,
                            @ApiParam(value = "排序字段")@RequestParam(value = "sort",required = false) String sort,
							@RequestBody Region region) {
        return regionService.pageQuery(accessToken,page, size, sort, region);
    }

	@PostMapping("v1/findChildren")
	@ApiOperation(value = "根据父级Id查询所有子级")
	@ApiResponses({
			@ApiResponse(code=Constant.FAIL_SESSION_CODE,message=Constant.FAIL_SESSION_MSG),
			@ApiResponse(code=Constant.SUCCESS_CODE,message=Constant.SUCCESS_MSG,response=Region.class),
			@ApiResponse(code=Constant.FAIL_CODE,message=Constant.FAIL_MSG,response=Result.class)})
	public Callable  getParentId(@ApiParam(hidden = true)@AuthorizationToken String accessToken,
							  @ApiParam(value = "主键",required = true)@RequestParam(value = "parentId")@NotNull Long parentId) {
		log.info("主线程开始");
    	Callable callable = () -> {
			log.info("副线程开始");
			Result result = regionService.getParentId(accessToken, parentId);
			log.info("副线程返回");
			return result;
		};
		log.info("主线程返回");
    	return callable ;
	}

	@PostMapping("v1/findByOnlyAndChildren")
	@ApiOperation(value = "根据主键查询自己和所有子级")
	@ApiResponses({
			@ApiResponse(code=Constant.FAIL_SESSION_CODE,message=Constant.FAIL_SESSION_MSG),
			@ApiResponse(code=Constant.SUCCESS_CODE,message=Constant.SUCCESS_MSG,response=Region.class),
			@ApiResponse(code=Constant.FAIL_CODE,message=Constant.FAIL_MSG,response=Result.class)})
	public Result findByOnlyAndChildren(@ApiParam(hidden = true)@AuthorizationToken String accessToken,
										@ApiParam(value = "主键",required = true)@RequestParam(value = "id")@NotNull Long id) {
		return regionService.findByOnlyAndChildren(accessToken,id);
	}
}
