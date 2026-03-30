package com.bug.platform.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("message")
public class Message {
    String id; // uuid
    Long userId;
    String title;
    Long issueId;
    Integer isRead;
    LocalDateTime createTime;
}