package com.ysy.oath.mapper.user;

import com.ysy.oath.entity.user.Operations;
import com.ysy.oath.entity.user.OperationsLog;
import tk.mybatis.mapper.common.Mapper;
import tk.mybatis.mapper.common.MySqlMapper;

/**
 * @author mc
 * Create date 2019/11/17 20:44
 * Version 1.0
 * Description
 */
public interface OperationsLogMapper extends Mapper<OperationsLog>, MySqlMapper<OperationsLog> {
}
