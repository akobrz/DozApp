package com.pm.dozapp.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class UserDTO {

    @JsonProperty("id")
    private String id;

    @JsonProperty("username")
    private String username;

    @JsonProperty("name")
    private String name;

    public UserDTO() {
    }

    public UserDTO(String id, String username, String name) {
        this.id = id;
        this.username = username;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}