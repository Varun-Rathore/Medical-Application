package com.example.medicalappv3.UserDefinedClasses;

public class ActivatedUser {
    private static String activated = null;

    public ActivatedUser() {
        activated = null;
    }

    public static String getActivated() {
        return activated;
    }

    public static void setActivated(String activated) {
        ActivatedUser.activated = activated;
    }
}
