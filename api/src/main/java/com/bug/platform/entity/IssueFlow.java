package com.bug.platform.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Date;

@Data
@TableName("issue_flow")
public class IssueFlow {
    @TableId(type = IdType.AUTO)
    Long id;
    Long issueId;
    Long operatorId;
    String oldStatus;
    String newStatus;
    String remark;
    LocalDateTime createTime;

    public IssueFlow() {
    }

    public IssueFlow(Long issueId, Long operatorId, String oldStatus, String newStatus, String remark) {
        this.issueId = issueId;
        this.operatorId = operatorId;
        this.oldStatus = oldStatus;
        this.newStatus = newStatus;
        this.remark = remark;
    }
}