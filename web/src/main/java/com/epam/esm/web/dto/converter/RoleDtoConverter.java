package com.epam.esm.web.dto.converter;

import com.epam.esm.persistence.model.entity.Role;
import com.epam.esm.web.dto.RoleDto;
import org.springframework.stereotype.Component;

@Component
public class RoleDtoConverter implements DtoConverter<Role, RoleDto> {

    @Override
    public Role convertToEntity(RoleDto dto) {
        return new Role(dto.getName());
    }

    @Override
    public RoleDto convertToDto(Role entity) {
        return new RoleDto(entity.getName());
    }
}
