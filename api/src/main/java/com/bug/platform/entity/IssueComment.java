package com.bug.platform.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Date;

@Data
@TableName("issue_comment")
public class IssueComment {
    @TableId(type = IdType.AUTO)
    Long id;
    Long issueId;
    Long userId;
    String content;
    LocalDateTime createTime;
}