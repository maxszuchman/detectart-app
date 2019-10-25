package com.experta.com.experta.model;

public class Device {

    private String createdAt;
    private String updatedAt;
    private String macAddress;
    private String alias;
    private String model;
    private double latitude;
    private double longitude;
    private double accuracy;
    private Status sensor1Status;
    private Status sensor2Status;
    private Status sensor3Status;
    private Status generalStatus;

    public Device() {}

    // TODO DESPUES SACAR
    public Device(String alias, Status status) {
        this.alias = alias;
        this.generalStatus = status;
    }

    public Device(String createdAt, String updatedAt, String macAddress, String alias, String model, double latitude, double longitude, double accuracy, Status sensor1Status, Status sensor2Status, Status sensor3Status, Status generalStatus) {
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.macAddress = macAddress;
        this.alias = alias;
        this.model = model;
        this.latitude = latitude;
        this.longitude = longitude;
        this.accuracy = accuracy;
        this.sensor1Status = sensor1Status;
        this.sensor2Status = sensor2Status;
        this.sensor3Status = sensor3Status;
        this.generalStatus = generalStatus;
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

    public String getMacAddress() {
        return macAddress;
    }

    public void setMacAddress(String macAddress) {
        this.macAddress = macAddress;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getAccuracy() {
        return accuracy;
    }

    public void setAccuracy(double accuracy) {
        this.accuracy = accuracy;
    }

    public Status getSensor1Status() {
        return sensor1Status;
    }

    public void setSensor1Status(Status sensor1Status) {
        this.sensor1Status = sensor1Status;
    }

    public Status getSensor2Status() {
        return sensor2Status;
    }

    public void setSensor2Status(Status sensor2Status) {
        this.sensor2Status = sensor2Status;
    }

    public Status getSensor3Status() {
        return sensor3Status;
    }

    public void setSensor3Status(Status sensor3Status) {
        this.sensor3Status = sensor3Status;
    }

    public Status getGeneralStatus() {
        return generalStatus;
    }

    public void setGeneralStatus(Status generalStatus) {
        this.generalStatus = generalStatus;
    }

    @Override
    public String toString() {
        return "Device{" +
                "createdAt='" + createdAt + '\'' +
                ", updatedAt='" + updatedAt + '\'' +
                ", macAddress='" + macAddress + '\'' +
                ", alias='" + alias + '\'' +
                ", model='" + model + '\'' +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", accuracy=" + accuracy +
                ", sensor1Status=" + sensor1Status +
                ", sensor2Status=" + sensor2Status +
                ", sensor3Status=" + sensor3Status +
                ", generalStatus=" + generalStatus +
                '}';
    }
}
