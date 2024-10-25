package org.ciphertech.api_gateway.services.vote_authority_service.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "private_keys")
public class PrivateKeyEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Lob // Use Lob for large objects like keys
    private byte[] privateKey;

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public byte[] getPrivateKey() {
        return privateKey;
    }

    public void setPrivateKey(byte[] privateKey) {
        this.privateKey = privateKey;
    }
}