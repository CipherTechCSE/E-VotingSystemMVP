package org.ciphertech.api_gateway.services.vote_authority_service.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "ballots")
public class Ballot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Lob
    private String encryptedVote; // Encrypted vote

    @Lob
    private String signature; // Signature for verification

    private LocalDateTime castAt;

    @ManyToOne
    @JoinColumn(name = "voter_id", nullable = false)
    private Voter voter;

    @ManyToOne
    @JoinColumn(name = "election_id", nullable = false)
    private Election election;

    // Constructors, getters, setters
}