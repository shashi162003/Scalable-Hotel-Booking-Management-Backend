package com.devshashi.AirBnBApp.dto;

import lombok.Data;

@Data
public class LoginResponseDTO {
    private String accessToken;

    public LoginResponseDTO() {}

    public LoginResponseDTO(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }
}
