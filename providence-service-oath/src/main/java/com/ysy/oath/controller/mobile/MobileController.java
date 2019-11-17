package com.ysy.oath.controller.mobile;

import com.github.surpassm.common.jackson.Result;
import com.ysy.oath.service.common.MobileService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotEmpty;

/**
 * @author mc
 * Create date 2019/3/12 10:55
 * Version 1.0
 * Description
 */
@CrossOrigin
@RestController
@RequestMapping("/mobile/")
@Api(tags = "移动端Api")
public class MobileController {

	@Resource
	private MobileService mobileService;

	@PostMapping("v1/auth/getPhone")
	@ApiOperation(value = "发送短信验证码")
	@ApiImplicitParam(name = "surpassm", value = "设备ID参数不能为空,具体自定义永久字符", required = true, dataType = "string", paramType = "header")
	public Result sendPhoneMsgCode(HttpServletRequest request,
								   @ApiParam(value = "手机号码", required = true) @RequestParam(value = "phone") @NotEmpty String phone) {
		return mobileService.sendPhoneMsgCode(request, phone);
	}

}
