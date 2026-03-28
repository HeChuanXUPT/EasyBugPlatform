// ProjectServiceImpl
package com.bug.platform.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bug.platform.entity.Project;
import com.bug.platform.mapper.ProjectMapper;
import com.bug.platform.service.IProjectService;
import org.springframework.stereotype.Service;

@Service
public class ProjectServiceImpl extends ServiceImpl<ProjectMapper, Project> implements IProjectService {
}