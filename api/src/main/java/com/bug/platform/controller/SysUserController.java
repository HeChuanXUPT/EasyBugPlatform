package com.bug.platform.controller;

import com.bug.platform.entity.SysUser;
import com.bug.platform.service.ISysUserService;
import com.bug.platform.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/user")
public class SysUserController {
    @Autowired
    private ISysUserService userService;

    @GetMapping("/list")
    public Result list() {
        List<SysUser> list = userService.list();
        return Result.success(list);
    }

    @PostMapping("/add")
    public Result add(@RequestBody SysUser user) {
        user.setCreateTime(new Date());
        if (user.getRole() == null) user.setRole("USER");
        userService.save(user);
        return Result.success();
    }

    @DeleteMapping("/delete/{id}")
    public Result delete(@PathVariable Long id) {
        userService.removeById(id);
        return Result.success();
    }

    @PostMapping("/login")
    public Result login(@RequestBody SysUser user) {
        SysUser one = userService.lambdaQuery().eq(SysUser::getUsername, user.getUsername())
                .eq(SysUser::getPassword, user.getPassword()).one();
        return one != null ? Result.success(one) : Result.error("账号或密码错误");
    }
}