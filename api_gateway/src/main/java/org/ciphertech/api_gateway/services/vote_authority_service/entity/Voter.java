package org.ciphertech.api_gateway.services.vote_authority_service.entity;

import jakarta.persistence.*;
import org.ciphertech.api_gateway.services.auth_service.models.User;

@Entity
public class Voter {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private Long userId; // Unique identifier for the user (linked to the User entity)

    // Store the public key of the voter (in base64 encoded format)
    @Lob
    @Column(length = 4096)
    private String publicKey;

    // Boolean flag to track if the voter has voted
    private Boolean hasVoted = false;

    // Link to the election
    @ManyToOne
    @JoinColumn(name = "election_id", nullable = false)
    private Election election;

    // Constructors, Getters, and Setters

    public Voter() {}

    // Constructor for initializing a voter with the required fields
    public Voter(Long userId, String publicKey, Election election) {
        this.userId = userId;
        this.publicKey = publicKey;
        this.election = election;
    }

    // Constructor that takes a User object and Election and generates default encrypted identity
    public Voter(User user, String publicKey, Election election) {
        this.userId = user.getId();
        this.publicKey = publicKey;
        this.election = election;
    }

    public Voter(User user) {
        this.userId = user.getId();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
    }

    public Boolean getHasVoted() {
        return hasVoted;
    }

    public void setHasVoted(Boolean hasVoted) {
        this.hasVoted = hasVoted;
    }

    public Election getElection() {
        return election;
    }

    public void setElection(Election election) {
        this.election = election;
    }
}
