package org.ciphertech.api_gateway.services.vote_authority_service.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
public class SecretKey {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String type;

    @Lob
    private String encryptedSecretKey; // Encrypted secret key used for vote encryption

    private LocalDateTime generatedAt;

    // Constructors, getters, setters
}
