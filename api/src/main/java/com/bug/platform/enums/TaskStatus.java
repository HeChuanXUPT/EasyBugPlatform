package com.bug.platform.enums;

import lombok.Getter;

/**
 * 任务状态枚举
 */
@Getter
public enum TaskStatus {
    WAITING_START("待开始"),
    IN_PROGRESS("进行中"),
    WAITING("等待"),
    COMPLETED("完成"),
    ABANDONED("废弃");

    private final String desc;

    TaskStatus(String desc) {
        this.desc = desc;
    }

}