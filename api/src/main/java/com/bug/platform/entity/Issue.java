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
    Long id;
    String title;
    Long projectId;
    String module;
    String issueType;
    String severity;
    String priority;
    String content;
    Long reporterId;
    Long assigneeId;
    String status;
    String reason;
    String solution;
    LocalDateTime createTime;
    LocalDateTime updateTime;
}