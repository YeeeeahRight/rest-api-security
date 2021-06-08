package com.epam.esm.service.logic.security;

public enum Permission {
    TAGS_CREATE("tags:create"),
    TAGS_GET("tags:get"),
    TAGS_DELETE("tags:delete"),
    BEST_TAG_GET("bestTag:get"),
    CERTIFICATES_CREATE("certificates:create"),
    CERTIFICATES_GET("certificates:get"),
    CERTIFICATES_UPDATE("certificates:update"),
    CERTIFICATES_DELETE("certificates:delete"),
    USERS_GET("users:get"),
    ORDERS_GET("orders:get"),
    ORDERS_CREATE("orders:create"),
    SIGNUP("signup"),
    LOGIN("login");

    private final String permission;

    Permission(String permission) {
        this.permission = permission;
    }

    public String getPermission() {
        return permission;
    }
}
