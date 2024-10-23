package org.ciphertech.api_gateway.services.vote_authority_service.entity;

import jakarta.persistence.*;
import org.ciphertech.api_gateway.services.auth_service.models.User;

@Entity
public class Voter {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long voterId; // Unique identifier for the voter

    @ManyToOne
    @JoinColumn(name = "election_id", nullable = false)
    private Election election;

    // Constructors, Getters, and Setters

    public Voter() {}

    public Voter(Long voterId, Election election) {
        this.voterId = voterId;
        this.election = election;
    }

    public Voter(User user) {
        this.voterId = user.getId();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getVoterId() {
        return voterId;
    }

    public void setVoterId(Long voterId) {
        this.voterId = voterId;
    }

    public Election getElection() {
        return election;
    }

    public void setElection(Election election) {
        this.election = election;
    }
}
