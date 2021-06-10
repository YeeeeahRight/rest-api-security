package com.epam.esm.web.dto.converter;

import com.epam.esm.persistence.model.entity.User;
import com.epam.esm.web.dto.entity.RoleDto;
import com.epam.esm.web.dto.entity.UserDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;

@Component
public class UserDtoConverter implements DtoConverter<User, UserDto> {

    private final RoleDtoConverter roleDtoConverter;

    @Autowired
    public UserDtoConverter(RoleDtoConverter roleDtoConverter) {
        this.roleDtoConverter = roleDtoConverter;
    }

    @Override
    public User convertToEntity(UserDto dto) {
        User user = new User();

        user.setId(dto.getId());
        user.setUsername(dto.getUsername());
        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());
        user.setEmail(dto.getEmail());
        user.setPassword(dto.getPassword());

        return user;
    }

    @Override
    public UserDto convertToDto(User entity) {
        UserDto userDto = new UserDto();

        userDto.setId(entity.getId());
        userDto.setUsername(entity.getUsername());
        userDto.setFirstName(entity.getFirstName());
        userDto.setLastName(entity.getLastName());
        userDto.setEmail(entity.getEmail());
        Set<RoleDto> roleDtoSet = entity.getRoles().stream()
                .map(roleDtoConverter::convertToDto)
                .collect(Collectors.toSet());
        userDto.setRoles(roleDtoSet);

        return userDto;
    }
}
