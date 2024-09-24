package com.patientRecTransferApp.utils;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;

@Component
public class ApplicationUrl {
    // public static final String APPLICATION_URL = "http://localhost:8080";
    public static final String BASE_URL = "http://localhost:5454";

    public String applicationUrl(HttpServletRequest request) {
        return "http://"+request.getServerName()+":"+request.getServerPort()+request.getContextPath();
    }
}
