package com.ysy.oath.entity.common;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ysy.oath.entity.user.UserInfo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import tk.mybatis.mapper.annotation.KeySql;
import tk.mybatis.mapper.annotation.NameStyle;
import tk.mybatis.mapper.code.Style;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Log
 *
 * @author zhangquanli
 */
@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "f_log")
@ApiModel(value = "日志管理")
@NameStyle(Style.camelhump)
@org.hibernate.annotations.Table(appliesTo = "f_log", comment = "日志管理")
public class Log implements Serializable {
	@Id
	@KeySql(useGeneratedKeys = true)
	@Column(columnDefinition="bigint COMMENT '系统标识'")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
    @ApiModelProperty(value = "系统标识", position = 1, example = "1")
    private Long id;

    @ApiModelProperty(value = "模块", position = 2, example = "用户管理")
    private String module;
    @ApiModelProperty(value = "功能", position = 3, example = "新增")
    private String function;
    @ApiModelProperty(value = "接口", position = 4, example = "/user/insert")
    private String uri;
    @Lob
    @ApiModelProperty(value = "数据", position = 5)
    private String data;
    @ApiModelProperty(value = "客户端IP", position = 6)
    private String clientIp;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "操作开始时间", position = 7)
    private LocalDateTime operateStartTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "操作结束时间", position = 8)
    private LocalDateTime operateEndTime;

    @ManyToOne(targetEntity = UserInfo.class)
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    @ApiModelProperty(value = "用户主键")
    private Long userId;
}
