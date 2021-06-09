package com.epam.esm.web.controller;

import com.epam.esm.persistence.model.entity.User;
import com.epam.esm.service.logic.jwt.JwtTokenProvider;
import com.epam.esm.service.logic.user.UserService;
import com.epam.esm.web.dto.GeneratedJwtDto;
import com.epam.esm.web.dto.LoginRequestDto;
import com.epam.esm.web.dto.RoleDto;
import com.epam.esm.web.dto.UserDto;
import com.epam.esm.web.dto.converter.RoleDtoConverter;
import com.epam.esm.web.dto.converter.UserDtoConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
public class AuthenticationController {

    private final UserService userService;
    private final UserDtoConverter userDtoConverter;
    private final RoleDtoConverter roleDtoConverter;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManager authenticationManager;

    @Autowired
    public AuthenticationController(UserService userService, UserDtoConverter userDtoConverter,
                                    RoleDtoConverter roleDtoConverter, JwtTokenProvider jwtTokenProvider,
                                    AuthenticationManager authenticationManager) {
        this.userService = userService;
        this.userDtoConverter = userDtoConverter;
        this.roleDtoConverter = roleDtoConverter;
        this.jwtTokenProvider = jwtTokenProvider;
        this.authenticationManager = authenticationManager;
    }

    @PostMapping("/signup")
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto signUp(@RequestBody @Valid UserDto userDto) {
        User user = userDtoConverter.convertToEntity(userDto);
        user = userService.create(user);

        return userDtoConverter.convertToDto(user);
    }

    @PostMapping("/login")
    @ResponseStatus(HttpStatus.OK)
    public GeneratedJwtDto login(@RequestBody LoginRequestDto loginDto) {
        String username = loginDto.getUsername();
        User user = userService.getByUsername(username);
        String password = loginDto.getPassword();
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(username, password);
        authenticationManager.authenticate(token);

        String jwt = jwtTokenProvider.createToken(username, user.getRoles());
        Set<RoleDto> roleDtoSet = user.getRoles().stream()
                .map(roleDtoConverter::convertToDto)
                .collect(Collectors.toSet());
        return new GeneratedJwtDto(username, roleDtoSet, jwt);
    }
}
