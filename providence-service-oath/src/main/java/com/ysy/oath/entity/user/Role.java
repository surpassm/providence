package com.ysy.oath.entity.user;

import com.github.surpassm.common.service.InsertView;
import com.github.surpassm.common.service.UpdateView;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import tk.mybatis.mapper.annotation.KeySql;
import tk.mybatis.mapper.annotation.NameStyle;
import tk.mybatis.mapper.code.Style;

import javax.persistence.*;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

/**
 * @author mc
 * Create date 2019/1/21 13:24
 * Version 1.0
 * Description
 */
@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@SuppressWarnings("serial")
@ApiModel(value = "角色")
@NameStyle(Style.camelhump)
@Table(name = "t_role")
@org.hibernate.annotations.Table(appliesTo = "t_role", comment = "角色")
public class Role implements Serializable {



	@Id
	@Min(0)
	@KeySql(useGeneratedKeys = true)
	@ApiModelProperty(value = "系统标识")
	@Column(columnDefinition="bigint COMMENT '系统标识'")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@NotNull(groups = UpdateView.class,message = "参数不能为空")
	private Long id;


	@ApiModelProperty(value = "security标识")
	@Column(columnDefinition="varchar(255) COMMENT 'security标识'")
	@NotBlank(groups = {InsertView.class,UpdateView.class},message = "参数不能为为空或空串")
	private String securityRoles;

	@ApiModelProperty(value = "名称")
	@Column(columnDefinition="varchar(255) COMMENT '名称'")
	@NotBlank(groups = {InsertView.class,UpdateView.class},message = "参数不能为为空或空串")
	private String name;

	@ApiModelProperty(value = "描述")
	@Column(columnDefinition="varchar(255) COMMENT '描述'")
	private String describes;

	@ApiModelProperty("排序字段")
	@Column(columnDefinition="int(11) COMMENT '排序字段'")
	private Integer roleIndex ;

	@Min(0)
	@Max(1)
	@ApiModelProperty(value = "是否删除",hidden = true)
	private Integer isDelete;

	@Transient
	@ApiModelProperty(value = "权限列表",hidden = true)
	private List<Menu> menus ;

}
