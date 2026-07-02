package com.duhy.auth;

public enum AppFeature {
    TOURNAMENTS("Giai dau"),
    TEAMS("Doi bong"),
    TRAVEL("Du lich"),
    PERMISSIONS("Phan quyen");

    private final String label;

    AppFeature(String label) {
        this.label = label;
    }

    public String label() {
        return label;
    }
}
