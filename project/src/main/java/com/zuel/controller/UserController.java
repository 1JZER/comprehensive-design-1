package com.zuel.controller;

import cn.hutool.core.bean.BeanUtil;
import com.zuel.convention.result.Result;
import com.zuel.convention.result.Results;
import com.zuel.dto.req.UserLoginReqDTO;
import com.zuel.dto.req.UserRegisterReqDTO;
import com.zuel.dto.req.UserUpdateReqDTO;
import com.zuel.dto.resp.UserActualRespDTO;
import com.zuel.dto.resp.UserLoginRespDTO;
import com.zuel.dto.resp.UserRespDTO;
import com.zuel.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;


    /**
     * 根据用户名查询用户信息
     */
    @GetMapping("/api/comprehensive-design/admin/v1/user/{username}")
    public Result<UserRespDTO> getUserByUsername(@PathVariable("username") String username) {
        return Results.success(userService.getUserByUsername(username));
    }


    /**
     * 根据用户名查询无脱敏用户信息
     */
    @GetMapping("/api/comprehensive-design/admin/v1/actual/user/{username}")
    public Result<UserActualRespDTO> getActualUserByUsername(@PathVariable("username") String username) {
        return Results.success(BeanUtil.toBean(userService.getUserByUsername(username), UserActualRespDTO.class));
    }


    /**
     * 查询用户名是否存在
     */
    @GetMapping("/api/comprehensive-design/admin/v1/user/has-username")
    public Result<Boolean> hasUsername(@RequestParam("username") String username) {
        return Results.success(userService.hasUsername(username));
    }


    /**
     * 注册用户
     */
    @PostMapping("/api/comprehensive-design/admin/v1/user")
    public Result<Void> register(@RequestBody UserRegisterReqDTO requestParam) {
        userService.register(requestParam);
        return Results.success();
    }


    /**
     * 修改用户
     */

    @PutMapping("/api/comprehensive-design/admin/v1/user")
    public Result<Void> update(@RequestBody UserUpdateReqDTO requestParam) {
        userService.update(requestParam);
        return Results.success();
    }


    /**
     * 用户登录
     */
    @PostMapping("/api/comprehensive-design/admin/v1/user/login")
    public Result<UserLoginRespDTO> login(@RequestBody UserLoginReqDTO requestParam) {
        return Results.success(userService.login(requestParam));
    }

    /**
     * 检查用户是否登录
     */
    @GetMapping("/api/comprehensive-design/admin/v1/user/check-login")
    public Result<Boolean> checkLogin(@RequestParam("username") String username, @RequestParam("token") String token) {
        return Results.success(userService.checkLogin(username, token));
    }

    /**
     * 用户退出登录
     */
    @DeleteMapping("/api/comprehensive-design/admin/v1/user/logout")
    public Result<Void> logout(@RequestParam("username") String username, @RequestParam("token") String token) {
        userService.logout(username, token);
        return Results.success();
    }




}
