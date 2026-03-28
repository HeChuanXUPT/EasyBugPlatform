package com.bug.platform.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bug.platform.entity.Issue;
import com.bug.platform.mapper.IssueMapper;
import com.bug.platform.service.IIssueService;
import org.springframework.stereotype.Service;

@Service
public class IssueServiceImpl extends ServiceImpl<IssueMapper, Issue> implements IIssueService {
}
