package com.example.emos.wx.controller;


import com.example.emos.wx.common.util.R;
import com.example.emos.wx.controller.form.TestSayHelloForm;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/test")
@Api("测试web接口")//用在请求类上的,表示对类的说明
public class TestController {
    @PostMapping("/sayHello")
    @ApiOperation("测试方法")
    public R sayHello(@Valid @RequestBody TestSayHelloForm form){//@Valid开启校验
        return R.ok().put("message","Hello,"+form.getName());
    }

    @PostMapping("/addUser")
    @ApiOperation("添加用户")
    @RequiresPermissions(value={"A","B"},logical= Logical.OR)
    public R addUser(){
         return R.ok("用户添加成功");
    }

}
