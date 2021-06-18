package com.epam.esm.web.controller;

import com.epam.esm.persistence.model.entity.User;
import com.epam.esm.service.logic.jwt.JwtTokenProvider;
import com.epam.esm.service.logic.user.UserService;
import com.epam.esm.web.dto.*;
import com.epam.esm.web.dto.converter.RoleDtoConverter;
import com.epam.esm.web.dto.converter.UserDtoConverter;
import com.epam.esm.web.dto.entity.RoleDto;
import com.epam.esm.web.dto.entity.UserDto;
import com.epam.esm.web.link.LinkAdder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ResolvableType;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
public class AuthenticationController {
    private static final String OAUTH_BASE_URL = "/oauth2/authorization/";

    private final UserService userService;
    private final UserDtoConverter userDtoConverter;
    private final RoleDtoConverter roleDtoConverter;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManager authenticationManager;
    private final ClientRegistrationRepository clientRegistrationRepository;
    private final LinkAdder<UserDto> userDtoLinkAdder;
    private final LinkAdder<GeneratedJwtDto> jwtDtoLinkAdder;

    @Autowired
    public AuthenticationController(UserService userService, UserDtoConverter userDtoConverter,
                                    RoleDtoConverter roleDtoConverter,
                                    JwtTokenProvider jwtTokenProvider,
                                    AuthenticationManager authenticationManager,
                                    ClientRegistrationRepository clientRegistrationRepository,
                                    LinkAdder<UserDto> userDtoLinkAdder,
                                    LinkAdder<GeneratedJwtDto> jwtDtoLinkAdder) {
        this.userService = userService;
        this.userDtoConverter = userDtoConverter;
        this.roleDtoConverter = roleDtoConverter;
        this.jwtTokenProvider = jwtTokenProvider;
        this.authenticationManager = authenticationManager;
        this.clientRegistrationRepository = clientRegistrationRepository;
        this.userDtoLinkAdder = userDtoLinkAdder;
        this.jwtDtoLinkAdder = jwtDtoLinkAdder;
    }

    @PostMapping("/signup")
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto signUp(@RequestBody @Valid UserDto userDto) {
        User user = userDtoConverter.convertToEntity(userDto);
        user = userService.create(user);

        userDto = userDtoConverter.convertToDto(user);
        userDtoLinkAdder.addLinks(userDto);
        return userDto;
    }

    @PostMapping("/login")
    @ResponseStatus(HttpStatus.OK)
    public GeneratedJwtDto login(@RequestBody LoginRequestDto loginDto) {
        String username = loginDto.getUsername();
        User user = userService.getByUsername(username);
        String password = loginDto.getPassword();
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(username, password);
        authenticationManager.authenticate(authenticationToken);

        Set<RoleDto> roleDtoSet = user.getRoles().stream()
                .map(roleDtoConverter::convertToDto)
                .collect(Collectors.toSet());
        Map<String, Object> payloadObjects = new HashMap<>();
        payloadObjects.put("roles", roleDtoSet);
        String jwt = jwtTokenProvider.createToken(username, payloadObjects);
        GeneratedJwtDto generatedJwtDto = new GeneratedJwtDto(username, roleDtoSet, jwt);
        jwtDtoLinkAdder.addLinks(generatedJwtDto);
        return generatedJwtDto;
    }

    @GetMapping("/oauth/signup")
    @ResponseStatus(HttpStatus.OK)
    public OAuthSignUpDto getOAuthRefs() {
        Iterable<ClientRegistration> clientRegistrations = null;
        ResolvableType type = ResolvableType.forInstance(clientRegistrationRepository)
                .as(Iterable.class);
        if (type != ResolvableType.NONE &&
                ClientRegistration.class.isAssignableFrom(type.resolveGenerics()[0])) {
            clientRegistrations = (Iterable<ClientRegistration>) clientRegistrationRepository;
        }

        OAuthSignUpDto oauthLogin = new OAuthSignUpDto();
        if (clientRegistrations != null) {
            clientRegistrations.forEach(registration ->
                    oauthLogin.getRefs().put(registration.getClientName(),
                            OAUTH_BASE_URL + registration.getRegistrationId()));
        }
        return oauthLogin;
    }

    @PostMapping("change-password")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAuthority('change-password')")
    public void changePassword(@RequestBody ChangePasswordDto changePasswordDto) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        userService.changePassword(username, changePasswordDto.getCurrentPassword(),
                changePasswordDto.getNewPassword());
    }

    @GetMapping("jwt-public-key")
    @ResponseStatus(HttpStatus.OK)
    public Map<String, Object> getJwtPublicKey() {
        String publicKeyBase64 = jwtTokenProvider.getPublicKey();

        return Collections.singletonMap("key", publicKeyBase64);
    }
}
