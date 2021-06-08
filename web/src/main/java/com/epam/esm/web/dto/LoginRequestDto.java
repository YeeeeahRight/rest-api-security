package com.epam.esm.web.dto;

import javax.validation.constraints.Size;

public class LoginRequestDto {
    @Size(min = 1, max = 60, message = "user.username.invalid")
    private String username;
    @Size(min = 5, max = 200, message = "user.pass.invalid")
    private String password;

    public LoginRequestDto() {
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
