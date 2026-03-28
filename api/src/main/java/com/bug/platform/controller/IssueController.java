package com.bug.platform.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bug.platform.dto.AssignIssueDto;
import com.bug.platform.dto.TransferDto;
import com.bug.platform.dto.UpdateStatusDto;
import com.bug.platform.entity.Issue;
import com.bug.platform.entity.IssueComment;
import com.bug.platform.entity.IssueFlow;
import com.bug.platform.service.IIssueCommentService;
import com.bug.platform.service.IIssueFlowService;
import com.bug.platform.service.IIssueService;
import com.bug.platform.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/issue")
public class IssueController {
    @Autowired
    IIssueService issueService;
    @Autowired
    IIssueFlowService flowService;
    @Autowired
    IIssueCommentService commentService;

    // 上报问题
    @PostMapping("/report")
    public Result report(@RequestBody Issue issue) {
        issue.setStatus("待指派");
        issue.setCreateTime(LocalDateTime.now());
        issue.setUpdateTime(LocalDateTime.now());
        issueService.save(issue);
        // 记录流转
        flowService.save(new IssueFlow(issue.getId(), issue.getReporterId(), null, "待指派", "问题上报成功"));
        return Result.success();
    }

    // 删除问题
    @PostMapping("/delete/{id}")
    public Result delete(@PathVariable Long id) {
        issueService.removeById(id);
        flowService.remove(new LambdaQueryWrapper<IssueFlow>().eq(IssueFlow::getIssueId, id));
        commentService.remove(new LambdaQueryWrapper<IssueComment>().eq(IssueComment::getIssueId, id));
        return Result.success();
    }

    // 指派问题
    @PostMapping("/assign")
    public Result assign(@RequestBody AssignIssueDto assignIssueDto) {
        Issue issue = issueService.getById(assignIssueDto.getId());
        String oldStatus = issue.getStatus();
        issue.setId(assignIssueDto.getId());
        issue.setAssigneeId(assignIssueDto.getAssigneeId());
        issue.setStatus("处理中");
        issue.setUpdateTime(LocalDateTime.now());
        issueService.updateById(issue);
        flowService.save(new IssueFlow(assignIssueDto.getId(), assignIssueDto.getOperatorId(), oldStatus, "处理中", "问题已指派"));
        return Result.success();
    }

    // 转派
    @PostMapping("/transfer")
    public Result transfer(@RequestBody TransferDto dto) {
        Issue issue = issueService.getById(dto.getId());
        String old = issue.getStatus();
        issue.setAssigneeId(dto.getAssigneeId());
        issueService.updateById(issue);
        flowService.save(new IssueFlow(dto.getId(), dto.getOperatorId(), old, "处理中", "转派：" + dto.getRemark()));
        return Result.success();
    }

    // 更新状态
    @PostMapping("/updateStatus")
    public Result updateStatus(@RequestBody UpdateStatusDto dto) {
        Issue issue = issueService.getById(dto.getId());
        String old = issue.getStatus();
        issue.setStatus(dto.getStatus());
        if ("已关闭".equals(dto.getStatus())) issue.setSolution(dto.getRemark());
        issue.setUpdateTime(LocalDateTime.now());
        if (dto.getSolution() != null) {
            issue.setSolution(dto.getSolution());
        }
        issueService.updateById(issue);
        flowService.save(new IssueFlow(dto.getId(), dto.getOperatorId(), old, dto.getStatus(), dto.getRemark()));
        return Result.success();
    }

    // 分页多条件查询
    @GetMapping("/page")
    public Result page(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) Long projectId,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String issueType,
            @RequestParam(required = false) String severity,
            @RequestParam(required = false) String title) {
        LambdaQueryWrapper<Issue> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(projectId != null, Issue::getProjectId, projectId)
                .eq(StringUtils.hasText(status), Issue::getStatus, status)
                .eq(StringUtils.hasText(issueType), Issue::getIssueType, issueType)
                .eq(StringUtils.hasText(severity), Issue::getSeverity, severity)
                .like(StringUtils.hasText(title), Issue::getTitle, title)
                .orderByDesc(Issue::getCreateTime);
        Page<Issue> page = issueService.page(new Page<>(current, size), wrapper);
        return Result.success(page);
    }

    // 详情
    @GetMapping("/detail/{id}")
    public Result detail(@PathVariable Long id) {
        return Result.success(issueService.getById(id));
    }
}