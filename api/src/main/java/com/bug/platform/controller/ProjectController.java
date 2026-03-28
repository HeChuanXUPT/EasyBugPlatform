package com.bug.platform.controller;

import com.bug.platform.entity.Project;
import com.bug.platform.service.IProjectService;
import com.bug.platform.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/project")
public class ProjectController {
    @Autowired
    private IProjectService projectService;

    @GetMapping("/list")
    public Result list() {
        List<Project> list = projectService.list();
        return Result.success(list);
    }

    @PostMapping("/add")
    public Result add(@RequestBody Project project) {
        project.setCreateTime(new Date());
        projectService.save(project);
        return Result.success();
    }

    @DeleteMapping("/delete/{id}")
    public Result delete(@PathVariable Long id) {
        projectService.removeById(id);
        return Result.success();
    }
}