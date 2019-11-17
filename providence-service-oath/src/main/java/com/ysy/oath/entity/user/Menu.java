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
 * @version 1.0
 * @date 2018/9/25 9:24
 * @description
 */
@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@SuppressWarnings("serial")
@ApiModel(value = "权限")
@NameStyle(Style.camelhump)
@Table(name = "t_menu")
@org.hibernate.annotations.Table(appliesTo = "t_menu", comment = "权限")
public class Menu implements Serializable {

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

    @ApiModelProperty(value = "菜单排序")
	@Column(columnDefinition="int(11) COMMENT '菜单排序'")
    private Integer menuIndex;

    @ApiModelProperty(value = "权限分类（0 菜单；1 功能）",allowableValues = "0,1")
	@Column(columnDefinition="int(11) COMMENT '权限分类（0 菜单；1 功能）'")
    private Integer type;


    @ApiModelProperty(value = "名称")
	@Column(columnDefinition="varchar(255) COMMENT '名称'")
	@NotBlank(groups = {InsertView.class,UpdateView.class},message = "参数不能为为空或空串")
    private String name;

    @ApiModelProperty(value = "描述")
	@NotBlank(message = "参数不能为为空或空串")
	@Column(columnDefinition="varchar(255) COMMENT '描述'")
    private String describes;

    @ApiModelProperty(value = "路由路径 前端使用")
	@Column(columnDefinition="varchar(255) COMMENT '路由路径 前端使用'")
    private String path;

    @ApiModelProperty(value = "菜单图标名称")
	@Column(columnDefinition="varchar(255) COMMENT '菜单图标名称'")
    private String menuIcon;

    @ApiModelProperty(value = "菜单url后台权限控制")
	@Column(columnDefinition="varchar(255) COMMENT '菜单url后台权限控制'")
    private String menuUrl;

	@Min(0)
	@Max(1)
	@ApiModelProperty(value = "是否删除",hidden = true)
	private Integer isDelete;

	@Transient
	@ApiModelProperty(value = "下级列表",hidden = true)
	private List<Menu> children;


}
