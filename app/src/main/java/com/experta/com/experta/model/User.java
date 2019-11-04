package com.experta.com.experta.model;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

public class User {

    private String createdAt, updatedAt, id, fullName, applicationToken;

    public User() {}

    public User(String id, String fullName, String applicationToken) {
        this.id = id;
        this.fullName = fullName;
        this.applicationToken = applicationToken;
    }

    public User(String createdAt, String updatedAt, String id, String fullName, String applicationToken) {
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.id = id;
        this.fullName = fullName;
        this.applicationToken = applicationToken;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getApplicationToken() {
        return applicationToken;
    }

    public void setApplicationToken(String applicationToken) {
        this.applicationToken = applicationToken;
    }

    public String jsonString() {
        ObjectMapper mapper = new ObjectMapper();

        String json = "";
        try {
            json = mapper.writeValueAsString(this);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return json;
    }
}
