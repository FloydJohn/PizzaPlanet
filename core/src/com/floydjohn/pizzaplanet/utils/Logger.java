package com.floydjohn.pizzaplanet.utils;

public class Logger {
    public static void error(Exception e) {
        System.out.println("[ERROR]: " + e);
    }

    public static void warn(String message) {
        System.out.println("[WARN]: " + message);
    }

    public static void info(String message) {
        System.out.println("[INFO]: " + message);
    }

    public static void debug(String message) {
        System.out.println("[DEBUG]: " + message);
    }
}
