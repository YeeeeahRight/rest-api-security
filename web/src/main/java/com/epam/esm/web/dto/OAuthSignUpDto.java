package com.epam.esm.web.dto;


import org.springframework.hateoas.RepresentationModel;

import java.util.HashMap;
import java.util.Map;

public class OAuthSignUpDto {
    private Map<String, String> refs = new HashMap<>();

    public OAuthSignUpDto() {
    }

    public Map<String, String> getRefs() {
        return refs;
    }

    public void setRefs(Map<String, String> refs) {
        this.refs = refs;
    }
}
