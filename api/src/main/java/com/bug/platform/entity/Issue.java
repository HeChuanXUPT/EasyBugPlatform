package com.bug.platform.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("issue")
public class Issue {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String title;
    private Long projectId;
    private String issueType;
    private String severity;
    private String priority;
    private String content;
    private Long reporterId;
    private Long assigneeId;
    private String status;
    private String solution;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}