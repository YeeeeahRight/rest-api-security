package com.epam.esm.web.dto.entity;

import java.util.Objects;

public class RoleDto {

    private String name;

    public RoleDto(String name) {
        this.name = name;
    }

    public RoleDto() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        RoleDto roleDto = (RoleDto) o;

        return Objects.equals(name, roleDto.name);
    }

    @Override
    public int hashCode() {
        return name != null ? name.hashCode() : 0;
    }
}
