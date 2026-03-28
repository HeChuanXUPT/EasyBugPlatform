package com.bug.platform.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.bug.platform.entity.IssueFlow;
import com.bug.platform.service.IIssueFlowService;
import com.bug.platform.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/flow")
public class IssueFlowController {
    @Autowired
    private IIssueFlowService flowService;

    @GetMapping("/list/{issueId}")
    public Result list(@PathVariable Long issueId) {
        List<IssueFlow> list = flowService.list(new LambdaQueryWrapper<IssueFlow>()
                .eq(IssueFlow::getIssueId, issueId).orderByAsc(IssueFlow::getCreateTime));
        return Result.success(list);
    }
}