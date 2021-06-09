package com.epam.esm.web.security;

import com.epam.esm.persistence.model.entity.User;
import com.epam.esm.service.exception.DuplicateEntityException;
import com.epam.esm.service.exception.NoSuchEntityException;
import com.epam.esm.service.logic.user.UserService;
import com.epam.esm.web.dto.UserDto;
import com.epam.esm.web.dto.converter.UserDtoConverter;
import com.epam.esm.web.exception.ExceptionResponse;
import com.epam.esm.web.exception.GlobalExceptionControllerHandler;
import com.epam.esm.web.filter.ServletJsonResponseSender;
import com.epam.esm.web.link.LinkAdder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

@Component
public class OauthAuthSuccessHandler {
    private final UserService userService;
    private final UserDtoConverter userDtoConverter;
    private final ServletJsonResponseSender jsonResponseSender;
    private final GlobalExceptionControllerHandler resolver;
    private final LinkAdder<UserDto> userDtoLinkAdder;

    @Autowired
    public OauthAuthSuccessHandler(UserService userService, UserDtoConverter userDtoConverter,
                                   ServletJsonResponseSender jsonResponseSender,
                                   GlobalExceptionControllerHandler resolver,
                                   LinkAdder<UserDto> userDtoLinkAdder) {
        this.userService = userService;
        this.userDtoConverter = userDtoConverter;
        this.jsonResponseSender = jsonResponseSender;
        this.resolver = resolver;
        this.userDtoLinkAdder = userDtoLinkAdder;
    }

    public void handle(HttpServletRequest request, HttpServletResponse response, Authentication auth)
            throws IOException {
        if (auth.getPrincipal() instanceof OidcUser) {
            OidcUser principalUser = ((OidcUser) auth.getPrincipal());
            User user = mapOidcUserToEntityUser(principalUser);
            String decodedPassword = user.getPassword();
            try {
                user = userService.create(user);
                UserDto userDto = userDtoConverter.convertToDto(user);
                userDtoLinkAdder.addLinks(userDto);
                Map<String, Object> responseObject = new HashMap<>();
                responseObject.put("userInfo", userDto);
                responseObject.put("generatedPassword", decodedPassword);
                jsonResponseSender.send(response, responseObject);
            } catch (DuplicateEntityException e) {
                Locale locale = request.getLocale();
                ExceptionResponse responseObject = resolver.handleDuplicateEntityException(e, locale).getBody();
                jsonResponseSender.send(response, responseObject);
            } catch (NoSuchEntityException e) {
                Locale locale = request.getLocale();
                ExceptionResponse responseObject = resolver.handleNoSuchEntityException(e, locale).getBody();
                jsonResponseSender.send(response, responseObject);
            }
        }
    }

    private User mapOidcUserToEntityUser(OidcUser oidcUser) {
        User user = new User();
        user.setUsername(oidcUser.getEmail());
        user.setFirstName(oidcUser.getGivenName());
        user.setLastName(oidcUser.getFamilyName());
        user.setEmail(oidcUser.getEmail());
        user.setPassword(String.valueOf(user.hashCode()));
        return user;
    }
}
