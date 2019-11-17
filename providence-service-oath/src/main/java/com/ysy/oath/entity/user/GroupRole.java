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
 * Create date 2019/3/14 18:09
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
@ApiModel(value = "组角色")
@NameStyle(Style.camelhump)
@Table(name = "m_group_role")
@org.hibernate.annotations.Table(appliesTo = "m_group_role", comment = "组角色")
public class GroupRole implements Serializable {

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
	@ApiModelProperty(value="角色系统标识",example = "1")
	@ManyToOne(targetEntity = Role.class)
	@JoinColumn(name = "role_id", referencedColumnName = "id")
	@NotNull(groups = {InsertView.class,UpdateView.class},message = "参数不能为为空")
	private Long roleId;

	@Min(0)
	@Max(1)
	@ApiModelProperty(value = "是否删除",hidden = true)
	private Integer isDelete;
}
