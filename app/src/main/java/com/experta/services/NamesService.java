package com.experta.services;

public class NamesService {

    public String getFirstNameFromDisplayName(String displayName) {

        if (displayName.isEmpty()) return "";

        String[] names = displayName.trim().split(" ");
        if (names.length == 1) return "";

        String firstName = "";

        for (int i = 0; i < names.length - 1; i++) {
            firstName += names[i];

            if (i < names.length - 2) {
                firstName += " ";
            }
        }

        return firstName;
    }

    public String getLastNameFromDisplayName(String displayName) {

        if (displayName.isEmpty()) return "";

        String[] names = displayName.trim().split(" ");
        if (names.length == 1) return displayName;

        return names[names.length - 1];
    }
}
