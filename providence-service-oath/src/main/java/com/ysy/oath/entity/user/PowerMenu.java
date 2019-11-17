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
import javax.validation.constraints.NotNull;

/**
 * @author mc
 * Create date 2019/11/17 19:44
 * Version 1.0
 * Description 权限与菜单关联表
 */
@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(value = "权限与菜单关联表")
@NameStyle(Style.camelhump)
@Table(name = "m_power_menu")
@org.hibernate.annotations.Table(appliesTo = "m_power_menu", comment = "权限与菜单关联表")
public class PowerMenu {
	@Id
	@Min(0)
	@KeySql(useGeneratedKeys = true)
	@ApiModelProperty(value = "系统标识")
	@Column(columnDefinition="bigint COMMENT '系统标识'")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@NotNull(groups = UpdateView.class,message = "参数不能为空")
	private Long id;

	@ApiModelProperty(value="权限系统标识",example = "1")
	@ManyToOne(targetEntity = Power.class)
	@JoinColumn(name = "power_id", referencedColumnName = "id")
	@NotNull(groups = {InsertView.class,UpdateView.class},message = "参数不能为为空")
	private Long powerId;
	@ApiModelProperty(value="菜单系统标识",example = "1")
	@ManyToOne(targetEntity = Menu.class)
	@JoinColumn(name = "menu_id", referencedColumnName = "id")
	@NotNull(groups = {InsertView.class,UpdateView.class},message = "参数不能为为空")
	private Long menuId;
	@Min(0)
	@Max(1)
	@ApiModelProperty(value="权限类型0=可访问、1=可授权",example = "1",allowableValues = "0,1")
	@Column(columnDefinition="int(1) COMMENT '权限类型'",nullable = false)
	@NotNull(groups = {InsertView.class,UpdateView.class},message = "参数不能为为空")
	private Integer menuType;
}
