package com.experta.com.experta.model;

public enum Sensor {

    CO("Se detectó que el MONÓXIDO DE CARBONO en tu ambiente está por encima del límite seguro, por lo tanto lorem ipsum..."),
    GAS("Se detectó que el GAS NATURAL en tu ambiente está por encima del límite seguro, por lo tanto lorem ipsum..."),
    SMOKE("Se detectó HUMO en tu ambiente, lorem ipsum");

    private String recommendation;

    Sensor(String recommendation) {
        this.recommendation = recommendation;
    }

    public String getRecommendation() {
        return this.recommendation;
    }
}
