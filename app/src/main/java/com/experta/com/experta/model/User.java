package com.experta.com.experta.model;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

public class User {

    private String fullName, id, applicationToken;

    public User() {}

    public User(String id, String fullName, String applicationToken) {
        this.fullName = fullName;
        this.id = id;
        this.applicationToken = applicationToken;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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
