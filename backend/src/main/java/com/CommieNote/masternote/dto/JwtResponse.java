package com.CommieNote.masternote.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class JwtResponse {
    private String token;
    private String type = "Bearer";
    private UUID id;
    private String username;

    public JwtResponse(String accessToken, UUID id, String username) {
        this.token = accessToken;
        this.id = id;
        this.username = username;
    }
}
