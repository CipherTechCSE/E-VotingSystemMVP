package org.ciphertech.api_gateway.services.vote_authority_service.entity;

import jakarta.persistence.*;

@Entity
public class Voter {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String voterId; // Unique identifier for the voter

    @Lob
    private String encryptedGroupKey; // Store encrypted group key

    private Boolean isEligible;

    @ManyToOne
    @JoinColumn(name = "election_id", nullable = false)
    private Election election;

    // Constructors, getters, setters
}
