package com.epam.esm.web.link;

import com.epam.esm.web.controller.AuthenticationController;
import com.epam.esm.web.dto.GeneratedJwtDto;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@Component
public class GeneratedJwtDtoLinkAdder extends AbstractLinkAdder<GeneratedJwtDto> {
    private static final Class<AuthenticationController> CONTROLLER = AuthenticationController.class;

    @Override
    public void addLinks(GeneratedJwtDto entity) {
        entity.add(linkTo(CONTROLLER).slash("change-password").withRel("change_pass"));
    }
}
