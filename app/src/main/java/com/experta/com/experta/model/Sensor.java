package com.experta.com.experta.model;

public enum Sensor {

    CO("ALARMA DE CO. En forma urgente abra ventanas y puertas totalmente, evacue a todas las personas del lugar y llame al 911.\n"),
    GAS("ALARMA DE GAS. No encienda ni apague la luz ni ningún otro dispositivo. Abra ventanas y puertas y cierre el flujo de gas desde la llave de paso. Evacue el lugar y llame al 911."),
    SMOKE("ALARMA DE HUMO. Por favor evacúe el lugar lo antes posible y llame al 911.");

    private String recommendation;

    Sensor(String recommendation) {
        this.recommendation = recommendation;
    }

    public String getRecommendation() {
        return this.recommendation;
    }
}
