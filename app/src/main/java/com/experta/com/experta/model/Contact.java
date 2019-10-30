package com.experta.com.experta.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

public class Contact {

    @JsonIgnore
    private String createdAt;
    @JsonIgnore
    private String updatedAt;
    @JsonIgnore
    private int id;

    private String firstName;
    private String lastName;
    private String phone;

    public Contact() {}

    public Contact(String firstName, String lastName, String phone) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.phone = phone;
    }

    public Contact(String createdAt, String updatedAt, int id, String firstName, String lastName, String phone) {
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phone = phone;
    }

    public String getFullName() {
        String fullName = firstName != null? firstName : "";
        fullName += " ";
        fullName += lastName != null? lastName : "";

        return fullName;
    }

    @JsonIgnore
    public String getCreatedAt() {
        return createdAt;
    }

    @JsonProperty
    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    @JsonIgnore
    public String getUpdatedAt() {
        return updatedAt;
    }

    @JsonProperty
    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    @JsonIgnore
    public int getId() {
        return id;
    }

    @JsonProperty
    public void setId(int id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
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

    @Override
    public String toString() {
        return "Contact{" +
                "createdAt='" + createdAt + '\'' +
                ", updatedAt='" + updatedAt + '\'' +
                ", id=" + id +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", phone='" + phone + '\'' +
                '}';
    }
}
