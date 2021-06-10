package com.epam.esm.web.dto;

import com.epam.esm.web.dto.entity.RoleDto;
import org.springframework.hateoas.RepresentationModel;

import java.util.Set;

public class GeneratedJwtDto extends RepresentationModel<GeneratedJwtDto> {
    private String username;
    private Set<RoleDto> roles;
    private String jwt;

    public GeneratedJwtDto(String username, Set<RoleDto> roles, String jwt) {
        this.username = username;
        this.roles = roles;
        this.jwt = jwt;
    }

    public GeneratedJwtDto() {
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Set<RoleDto> getRoles() {
        return roles;
    }

    public void setRoles(Set<RoleDto> roles) {
        this.roles = roles;
    }

    public String getJwt() {
        return jwt;
    }

    public void setJwt(String jwt) {
        this.jwt = jwt;
    }
}
