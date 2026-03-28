package com.bug.platform.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.bug.platform.entity.IssueComment;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface IssueCommentMapper extends BaseMapper<IssueComment> {
}