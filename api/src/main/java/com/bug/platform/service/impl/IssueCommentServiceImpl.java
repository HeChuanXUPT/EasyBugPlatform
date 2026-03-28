package com.bug.platform.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bug.platform.entity.IssueComment;
import com.bug.platform.mapper.IssueCommentMapper;
import com.bug.platform.service.IIssueCommentService;
import org.springframework.stereotype.Service;

@Service
public class IssueCommentServiceImpl extends ServiceImpl<IssueCommentMapper, IssueComment> implements IIssueCommentService {
}
