package com.bug.platform.dto;

import lombok.Data;

@Data
public class TransferDto {
    Long id;
    Long assigneeId;
    Long operatorId;
    String remark;
}