package com.ysy.oath.entity.user;

import com.fasterxml.jackson.annotation.JsonFormat;
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
import java.time.LocalDateTime;

/**
 * @author mc
 * Create date 2019/3/14 18:20
 * Version 1.0
 * Description
 */
@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "t_operations_log")
@ApiModel(value = "日志管理")
@NameStyle(Style.camelhump)
@org.hibernate.annotations.Table(appliesTo = "t_operations_log", comment = "日志管理")
public class OperationsLog {
	@Id
	@KeySql(useGeneratedKeys = true)
	@Column(columnDefinition="bigint COMMENT '系统标识'")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@ApiModelProperty(value = "系统标识")
	private Long id;

	@ApiModelProperty(value = "模块")
	private String module;
	@ApiModelProperty(value = "功能")
	private String function;
	@ApiModelProperty(value = "接口")
	private String uri;
	@Lob
	@ApiModelProperty(value = "数据")
	private String data;
	@ApiModelProperty(value = "客户端IP")
	private String clientIp;
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@ApiModelProperty(value = "操作开始时间")
	private LocalDateTime operateStartTime;
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@ApiModelProperty(value = "操作结束时间")
	private LocalDateTime operateEndTime;

	@ManyToOne(targetEntity = UserInfo.class)
	@JoinColumn(name = "user_id", referencedColumnName = "id")
	@ApiModelProperty(value = "用户主键")
	private Long userId;
}
