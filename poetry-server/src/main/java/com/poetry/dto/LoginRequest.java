package com.poetry.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * 用户登录请求
 */
public class LoginRequest {

    @NotBlank(message = "用户名/邮箱不能为空")
    private String account;     // 用户名或邮箱

    @NotBlank(message = "密码不能为空")
    private String password;

    private Boolean rememberMe = false;

    // ======== Getter / Setter ========

    public String getAccount() { return account; }
    public void setAccount(String account) { this.account = account; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public Boolean getRememberMe() { return rememberMe; }
    public void setRememberMe(Boolean rememberMe) { this.rememberMe = rememberMe; }
}
