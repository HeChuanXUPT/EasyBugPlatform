package com.bug.platform.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.bug.platform.entity.IssueComment;
import com.bug.platform.service.IIssueCommentService;
import com.bug.platform.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/comment")
public class IssueCommentController {

    @Autowired
    private IIssueCommentService commentService;

    /**
     * 新增评论（前端调用）
     */
    @PostMapping("/add")
    public Result add(@RequestBody IssueComment comment) {
        comment.setCreateTime(LocalDateTime.now());
        commentService.save(comment);
        return Result.success("评论成功");
    }

    /**
     * 根据问题ID查询所有评论
     */
    @GetMapping("/list/{issueId}")
    public Result list(@PathVariable Long issueId) {
        LambdaQueryWrapper<IssueComment> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(IssueComment::getIssueId, issueId)
                .orderByAsc(IssueComment::getCreateTime);

        List<IssueComment> list = commentService.list(wrapper);
        return Result.success(list);
    }
}