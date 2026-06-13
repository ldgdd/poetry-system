package com.poetry.dto;

import com.poetry.model.User;

/**
 * 登录成功返回
 */
public class LoginResponse {

    private String token;
    private UserInfo user;

    public static LoginResponse of(String token, User user) {
        LoginResponse resp = new LoginResponse();
        resp.token = token;
        resp.user = new UserInfo(user);
        return resp;
    }

    // ======== Getter / Setter ========

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }

    public UserInfo getUser() { return user; }
    public void setUser(UserInfo user) { this.user = user; }

    /**
     * 用户信息（脱敏）
     */
    public static class UserInfo {
        private Long id;
        private String username;
        private String nickname;
        private String avatar;
        private String bio;
        private String role;

        public UserInfo(User u) {
            this.id = u.getId();
            this.username = u.getUsername();
            this.nickname = u.getNickname() != null ? u.getNickname() : u.getUsername();
            this.avatar = u.getAvatar();
            this.bio = u.getBio();
            this.role = u.getRole();
        }

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        public String getNickname() { return nickname; }
        public void setNickname(String nickname) { this.nickname = nickname; }
        public String getAvatar() { return avatar; }
        public void setAvatar(String avatar) { this.avatar = avatar; }
        public String getBio() { return bio; }
        public void setBio(String bio) { this.bio = bio; }
        public String getRole() { return role; }
        public void setRole(String role) { this.role = role; }
    }
}
