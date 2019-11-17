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
import java.util.List;

/**
 * @author mc
 * Create date 2019/11/17 19:54
 * Version 1.0
 * Description
 */
@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(value = "后台功能接口")
@NameStyle(Style.camelhump)
@Table(name = "t_operation")
@org.hibernate.annotations.Table(appliesTo = "t_operation", comment = "后台功能接口")
public class Operations {
	@Id
	@Min(0)
	@KeySql(useGeneratedKeys = true)
	@ApiModelProperty(value = "系统标识")
	@Column(columnDefinition="bigint COMMENT '系统标识'")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@NotNull(groups = UpdateView.class,message = "参数不能为空")
	private Long id;

	@ApiModelProperty(value = "父级菜单ID")
	@OneToOne(targetEntity = Menu.class)
	@JoinColumn(name = "parent_id", referencedColumnName = "id")
	private Long parentId;

	@ApiModelProperty(value = "排序")
	@Column(columnDefinition="int(11) COMMENT '排序'")
	private Integer menuIndex;

	@ApiModelProperty(value = "权限分类（0 菜单；1 功能）",allowableValues = "0,1")
	@Column(columnDefinition="int(11) COMMENT '权限分类（0 菜单；1 功能）'")
	private Integer type;

	@ApiModelProperty(value = "名称")
	@Column(columnDefinition="varchar(255) COMMENT '名称'")
	@NotBlank(groups = {InsertView.class, UpdateView.class},message = "参数不能为为空或空串")
	private String name;

	@ApiModelProperty(value = "描述")
	@NotBlank(message = "参数不能为为空或空串")
	@Column(columnDefinition="varchar(255) COMMENT '描述'")
	private String describes;

	@ApiModelProperty(value = "API地址")
	@Column(columnDefinition="varchar(255) COMMENT '菜单url后台权限控制'")
	private String apiUrl;

	@Min(0)
	@Max(1)
	@ApiModelProperty(value = "是否删除",hidden = true)
	private Integer isDelete;

	@Transient
	@ApiModelProperty(value = "下级列表",hidden = true)
	private List<Operations> children;

}
