package com.bug.platform.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bug.platform.entity.IssueFlow;
import com.bug.platform.mapper.IssueFlowMapper;
import com.bug.platform.service.IIssueFlowService;
import org.springframework.stereotype.Service;

@Service
public class IssueFlowServiceImpl extends ServiceImpl<IssueFlowMapper, IssueFlow> implements IIssueFlowService {
}
