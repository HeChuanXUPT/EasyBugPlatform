package com.bug.platform.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.bug.platform.entity.Issue;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface IssueMapper extends BaseMapper<Issue> {
}