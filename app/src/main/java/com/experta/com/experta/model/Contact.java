package com.experta.com.experta.model;

public class Contact {

    private String createdAt;
    private String updatedAt;
    private int id;
    private String firstName;
    private String lastName;
    private String phone;

    public Contact() {}

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

    public int getId() {
        return id;
    }

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
