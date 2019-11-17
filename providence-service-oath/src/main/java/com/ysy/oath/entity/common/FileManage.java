package com.ysy.oath.entity.common;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import tk.mybatis.mapper.annotation.KeySql;
import tk.mybatis.mapper.annotation.NameStyle;
import tk.mybatis.mapper.code.Style;

import javax.persistence.*;
import java.io.Serializable;

/**
 * @author mc
 * Create date 2019/4/1 9:21
 * Version 1.0
 * Description
 */
@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(value = "文件管理")
@NameStyle(Style.camelhump)
@Table(name = "f_file_manage")
@org.hibernate.annotations.Table(appliesTo = "f_file_manage", comment = "文件管理")
public class FileManage implements Serializable {

	@Id
	@KeySql(useGeneratedKeys = true)
	@ApiModelProperty(value = "系统标识")
	@Column(columnDefinition="int(11) COMMENT '系统标识'")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	/**
	 * 文件名称
	 */
	@ApiModelProperty(value = "文件旧名称",hidden = true)
	@Column(columnDefinition="varchar(255) COMMENT '文件名称'")
	private String fileOldName;
	/**
	 * 文件名称
	 */
	@ApiModelProperty(value = "文件新名称",hidden = true)
	@Column(columnDefinition="varchar(255) COMMENT '文件名称'")
	private String fileNewName;
	/**
	 * 文件后缀
	 */
	@ApiModelProperty(value = "文件后缀",hidden = true)
	@Column(columnDefinition="varchar(255) COMMENT '文件后缀'")
	private String fileSuffix;
	/**
	 * 文件路径
	 */
	@ApiModelProperty(value = "文件路径",hidden = true)
	@Column(columnDefinition="varchar(255) COMMENT '文件路径'")
	private String url;
}
