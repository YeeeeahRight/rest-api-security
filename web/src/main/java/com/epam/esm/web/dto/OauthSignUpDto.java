package com.epam.esm.web.dto;


import java.util.HashMap;
import java.util.Map;

public class OauthSignUpDto {
    private Map<String, String> refs = new HashMap<>();

    public OauthSignUpDto() {
    }

    public Map<String, String> getRefs() {
        return refs;
    }

    public void setRefs(Map<String, String> refs) {
        this.refs = refs;
    }
}
