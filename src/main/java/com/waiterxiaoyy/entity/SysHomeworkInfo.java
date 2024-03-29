package com.waiterxiaoyy.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

/**
 * 功能描述：
 *
 * @Author WaiterXiaoYY
 * @Date 2022/3/17 16:37
 * @Version 1.0
 */
@Data
public class SysHomeworkInfo extends BaseEntity {
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    private static final long serialVersionUID = 1L;

    private Long homeworkId;
    private String studentId;
    private String fileUrl;

    @TableField(exist = false)
    private String studentName;

}
