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
 * Create date 2019/1/21 13:39
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
@ApiModel(value = "部门")
@NameStyle(Style.camelhump)
@Table(name = "t_department")
@org.hibernate.annotations.Table(appliesTo = "t_department", comment = "部门")
public class Department implements Serializable {

	@Id
	@Min(0)
	@KeySql(useGeneratedKeys = true)
	@ApiModelProperty(value = "系统标识")
	@Column(columnDefinition="bigint COMMENT '系统标识'")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@NotNull(groups = UpdateView.class,message = "参数不能为空")
	private Long id;

	@NotBlank(groups = {InsertView.class,UpdateView.class},message = "参数不能为为空或空串")
	@ApiModelProperty(value = "名称",example = "研发部")
	@Column(columnDefinition="varchar(255) COMMENT '名称'")
	private String name ;

	@ApiModelProperty("父级Id")
	@OneToOne(targetEntity = Department.class)
	@JoinColumn(name = "parent_id", referencedColumnName = "id")
	private Long parentId ;

	@ApiModelProperty("排序字段")
	@Column(columnDefinition="int(11) COMMENT '排序字段'")
	private Integer departmentIndex ;

	@Transient
	@ApiModelProperty(value = "下级列表",hidden = true)
	private List<Department> children;

	@Min(0)
	@Max(1)
	@ApiModelProperty(value = "是否删除",hidden = true)
	private Integer isDelete;

}
