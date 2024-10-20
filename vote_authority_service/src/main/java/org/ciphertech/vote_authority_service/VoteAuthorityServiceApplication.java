package org.ciphertech.vote_authority_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(
        scanBasePackages = "org.ciphertech"
)
public class VoteAuthorityServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(VoteAuthorityServiceApplication.class, args);
    }

}
