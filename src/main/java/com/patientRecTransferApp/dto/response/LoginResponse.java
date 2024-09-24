package com.patientRecTransferApp.dto.response;


public record LoginResponse (
    String name,
    String email,
    String jwt) {

}
