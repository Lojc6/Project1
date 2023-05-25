package com.example.emos.wx.controller;


import cn.hutool.core.date.DateUtil;
import com.example.emos.wx.common.util.R;
import com.example.emos.wx.config.shiro.JwtUtil;
import com.example.emos.wx.controller.form.LoginForm;
import com.example.emos.wx.controller.form.RegisterForm;
import com.example.emos.wx.controller.form.leave_requestForm;
import com.example.emos.wx.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.Date;
import java.util.HashMap;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/user")
@Api("用户模块Web接口")
public class UserController {
    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private RedisTemplate redisTemplate;

    @Value("${emos.jwt.cache-expire}")
    private int cacheExpire;

    @PostMapping("/register")
    @ApiOperation("注册用户")
    public R register(@Valid @RequestBody RegisterForm form) {
        int userId = userService.register(form.getRegisterCode(), form.getCode(), form.getNickname(), form.getPhoto());
        String token = jwtUtil.createToken(userId);
        Set<String> permsSet = userService.searchUserPermissions(userId);
        saveCacheToken(token, userId);
        return R.ok("用户注册成功").put("token", token).put("permission", permsSet);
    }

    @PostMapping("/login")
    @ApiOperation("登录系统")
    public R login(@Valid @RequestBody LoginForm form) {
        int id = userService.login(form.getCode());
        String token = jwtUtil.createToken(id);
        saveCacheToken(token, id);
        Set<String> permsSet = userService.searchUserPermissions(id);
        return R.ok("登录成功").put("token", token).put("permission", permsSet);
    }

    @GetMapping("/searchUserSummary")
    public R searchUserSummary(@RequestHeader("token") String token) {
        int userId = jwtUtil.getUserId(token);
        HashMap map = userService.searchUserSummary(userId);
        return R.ok().put("result", map);
    }

    @PostMapping("/leaveRequest")
    @ApiOperation(("请假申请"))
    public R leaveRequest(@Valid @RequestBody leave_requestForm form ) {

        Date startdate = form.getStartdate();
        Date enddate = form.getEnddate();
        int submitLeave = userService.SubmitLeave(form.getEmployeeId(), startdate, enddate, form.getLeavetype(), form.getReason());
        if (submitLeave == 1) {
            return R.ok().put("result", "申请提交成功");
        } else {
             return R.ok().put("result","提交失败");
        }
    }


    private void saveCacheToken(String token,int userId){
        redisTemplate.opsForValue().set(token,userId+"",cacheExpire, TimeUnit.DAYS);
    }

}
