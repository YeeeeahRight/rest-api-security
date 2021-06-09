package com.epam.esm.web.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class ServletJsonResponseSender {
    private static final String RESPONSE_CHAR_ENCODING = "UTF-8";
    private static final String RESPONSE_CONTENT_TYPE = "application/json";

    public void send(HttpServletResponse httpServletResponse, Object responseObject) throws IOException {
        httpServletResponse.setCharacterEncoding(RESPONSE_CHAR_ENCODING);
        httpServletResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        httpServletResponse.setContentType(RESPONSE_CONTENT_TYPE);
        String json = new ObjectMapper().writeValueAsString(responseObject);
        httpServletResponse.getWriter().write(json);
        httpServletResponse.flushBuffer();
    }
}
