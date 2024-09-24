package com.patientRecTransferApp.exception;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BusinessException extends RuntimeException {

    private ErrorModel errors;


}
