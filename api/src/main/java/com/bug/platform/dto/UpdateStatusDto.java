package com.bug.platform.dto;

import lombok.Data;

@Data
public class UpdateStatusDto {
    Long id;
    String status;
    Long operatorId;
    String remark;
    String solution;
}