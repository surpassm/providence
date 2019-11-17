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
import java.io.Serializable;

/**
 * @author mc
 * Create date 2019/3/14 18:06
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
@ApiModel(value = "组权限")
@NameStyle(Style.camelhump)
@Table(name = "m_group_menu")
@org.hibernate.annotations.Table(appliesTo = "m_group_menu", comment = "组权限")
public class GroupMenu implements Serializable {



	@Id
	@Min(0)
	@KeySql(useGeneratedKeys = true)
	@ApiModelProperty(value = "系统标识")
	@Column(columnDefinition="bigint COMMENT '系统标识'")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@NotNull(groups = UpdateView.class,message = "参数不能为空")
	private Long id;

	@ApiModelProperty(value="组系统标识",example = "1")
	@ManyToOne(targetEntity = Group.class)
	@JoinColumn(name = "group_id", referencedColumnName = "id")
	@NotNull(groups = {InsertView.class,UpdateView.class},message = "参数不能为为空")
	private Long groupId;

	@ApiModelProperty(value="权限系统标识",example = "1")
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

	@Min(0)
	@Max(1)
	@ApiModelProperty(value = "是否删除",hidden = true)
	private Integer isDelete;
}
