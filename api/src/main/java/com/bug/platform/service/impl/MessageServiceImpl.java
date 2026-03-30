package com.bug.platform.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bug.platform.entity.Message;
import com.bug.platform.mapper.MessageMapper;
import com.bug.platform.service.IMessageService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class MessageServiceImpl extends ServiceImpl<MessageMapper, Message> implements IMessageService {

    @Override
    public int getUnreadCount(Long userId) {
        return lambdaQuery()
                .eq(Message::getUserId, userId)
                .eq(Message::getIsRead, 0)
                .count().intValue();
    }

    @Override
    public List<Message> getList(Long userId) {
        return lambdaQuery()
                .eq(Message::getUserId, userId)
                .orderByDesc(Message::getCreateTime)
                .list();
    }

    @Override
    public void read(String id) {
        lambdaUpdate()
                .eq(Message::getId, id)
                .set(Message::getIsRead, 1)
                .update();
    }

    // 发送消息（转派时调用）
    @Override
    public void send(Long userId, String title, Long issueId) {
        Message msg = new Message();
        msg.setId(UUID.randomUUID().toString());
        msg.setUserId(userId);
        msg.setTitle(title);
        msg.setIssueId(issueId);
        save(msg);
    }
}
