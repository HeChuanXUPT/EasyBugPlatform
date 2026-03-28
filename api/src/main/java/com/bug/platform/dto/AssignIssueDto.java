package com.bug.platform.dto;

import lombok.Data;

@Data
public class AssignIssueDto {
    Long id;
    Long assigneeId;
    Long operatorId;
}