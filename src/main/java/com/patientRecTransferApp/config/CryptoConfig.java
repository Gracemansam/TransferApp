package com.patientRecTransferApp.config;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.springframework.context.annotation.Configuration;

import java.security.Security;

@Configuration
public class CryptoConfig {
    static {
        Security.addProvider(new BouncyCastleProvider());
    }
}