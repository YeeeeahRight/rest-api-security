package com.epam.esm.service.logic.security;

import com.epam.esm.persistence.model.entity.Role;
import com.epam.esm.persistence.model.entity.User;
import org.springframework.security.core.GrantedAuthority;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public final class SecurityUserDetailsFactory {

    public static SecurityUserDetails create(User user) {
        return new SecurityUserDetails(
                user.getUsername(),
                user.getPassword(),
                mapToGrantedAuthorities(user.getRoles()),
                true
        );
    }

    private static List<GrantedAuthority> mapToGrantedAuthorities(Set<Role> userRoles) {
        List<GrantedAuthority> authorities = new ArrayList<>();
        for (Role role : userRoles) {
            Set<GrantedAuthority> roleAuthorities = RoleAuthority.valueOf(role.getName()).getAuthorities();
            authorities.addAll(roleAuthorities);
        }

        return authorities;
    }
}
