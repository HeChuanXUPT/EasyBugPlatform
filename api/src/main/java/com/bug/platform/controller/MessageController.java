package com.bug.platform.controller;

import com.bug.platform.entity.Message;
import com.bug.platform.service.IMessageService;
import com.bug.platform.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/msg")
public class MessageController {

    @Autowired
    private IMessageService messageService;

    // 未读数量（红点）
    @GetMapping("/unread/count")
    public Result<Integer> unreadCount(@RequestParam Long userId) {
        return Result.success(messageService.getUnreadCount(userId));
    }

    // 消息列表
    @GetMapping("/list")
    public Result<List<Message>> list(@RequestParam Long userId) {
        return Result.success(messageService.getList(userId));
    }

    // 标记已读
    @PostMapping("/read/{id}")
    public Result<?> read(@PathVariable String id) {
        messageService.read(id);
        return Result.success();
    }

    @PostMapping("/read/all")
    public Result<?> readAll(@RequestParam Long userId) {
        messageService.lambdaUpdate()
                .eq(Message::getUserId, userId)
                .set(Message::getIsRead, 1)
                .update();
        return Result.success();
    }

}