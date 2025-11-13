package com.pm.dozapp.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class UserResponse {

    @JsonProperty("data")
    private UserDTO data;

    public UserResponse(UserDTO data) {
        this.data = data;
    }

    public UserResponse() {
    }

    public UserDTO getData() {
        return data;
    }

    public void setData(UserDTO data) {
        this.data = data;
    }

}