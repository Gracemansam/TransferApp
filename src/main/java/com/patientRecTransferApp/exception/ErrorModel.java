package com.patientRecTransferApp.exception;

import lombok.*;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ErrorModel {

    private String code;
    private String message;

}
