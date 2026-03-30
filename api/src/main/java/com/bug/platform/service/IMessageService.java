package com.bug.platform.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.bug.platform.entity.Message;

import java.util.List;

public interface IMessageService extends IService<Message> {
    int getUnreadCount(Long userId);

    List<Message> getList(Long userId);

    void read(String id);

    void send(Long userId, String title, Long issueId);
}