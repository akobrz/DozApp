package com.pm.dozapp.dto;

public class UserResponse {

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