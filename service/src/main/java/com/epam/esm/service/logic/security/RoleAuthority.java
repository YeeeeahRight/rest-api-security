package com.epam.esm.service.logic.security;


import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public enum RoleAuthority {
    USER(permissionSetOf(
            Permission.ORDERS_GET, Permission.TAGS_GET, Permission.USERS_GET, Permission.BEST_TAG_GET,
            Permission.CERTIFICATES_GET, Permission.ORDERS_CREATE, Permission.CHANGE_PASSWORD)),
    ADMIN(permissionSetOf(
            Permission.ORDERS_GET,Permission.BEST_TAG_GET,
            Permission.TAGS_GET, Permission.TAGS_CREATE, Permission.TAGS_DELETE,
            Permission.USERS_GET, Permission.CERTIFICATES_GET, Permission.CERTIFICATES_CREATE,
            Permission.CERTIFICATES_UPDATE, Permission.CERTIFICATES_DELETE,
            Permission.CHANGE_PASSWORD));

    private final Set<Permission> permissions;

    RoleAuthority(Set<Permission> permissions) {
        this.permissions = permissions;
    }

    public Set<Permission> getPermissions() {
        return permissions;
    }

    public Set<GrantedAuthority> getAuthorities() {
        return getPermissions().stream()
                .map(permission -> new SimpleGrantedAuthority(permission.getPermission()))
                .collect(Collectors.toSet());
    }

    private static Set<Permission> permissionSetOf(Permission... permissions) {
        return new HashSet<>(Arrays.asList(permissions));
    }
}
