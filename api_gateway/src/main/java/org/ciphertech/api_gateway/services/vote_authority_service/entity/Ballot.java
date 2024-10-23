package org.ciphertech.api_gateway.services.vote_authority_service.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "ballots")
public class Ballot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String encryptedVote; // Encrypted vote

    @Lob
    private String voterSignature; // Signature for verification

    @ElementCollection
    private List<String> multiSignatures; // List of authorities

    @Column(nullable = false)
    private LocalDateTime issuedAt;

    @Column(nullable = false)
    private LocalDateTime submittedAt;

    @ManyToOne
    @JoinColumn(name = "voter_id", nullable = false)
    private Voter voter;

    @ManyToOne
    @JoinColumn(name = "election_id", nullable = false)
    private Election election;

    // Constructors, Getters, and Setters

    public Ballot() {}
    public Ballot(String encryptedVote, String voterSignature, List<String> multiSignatures, LocalDateTime issuedAt, LocalDateTime submittedAt, Voter voter, Election election) {
        this.encryptedVote = encryptedVote;
        this.voterSignature = voterSignature;
        this.multiSignatures = multiSignatures;
        this.issuedAt = issuedAt;
        this.submittedAt = submittedAt;
        this.voter = voter;
        this.election = election;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEncryptedVote() {
        return encryptedVote;
    }

    public void setEncryptedVote(String encryptedVote) {
        this.encryptedVote = encryptedVote;
    }

    public String getVoterSignature() {
        return voterSignature;
    }

    public void setVoterSignature(String voterSignature) {
        this.voterSignature = voterSignature;
    }

    public LocalDateTime getIssuedAt() {
        return issuedAt;
    }

    public void setIssuedAt(LocalDateTime issuedAt) {
        this.issuedAt = issuedAt;
    }

    public List<String> getMultiSignatures() {
        return multiSignatures;
    }

    public void setMultiSignatures(List<String> multiSignatures) {
        this.multiSignatures = multiSignatures;
    }

    public void addMultiSignature(String multiSignature) {
        this.multiSignatures.add(multiSignature);
    }

    public LocalDateTime getSubmittedAt() {
        return submittedAt;
    }

    public void setSubmittedAt(LocalDateTime submittedAt) {
        this.submittedAt = submittedAt;
    }

    public Voter getVoter() {
        return voter;
    }

    public void setVoter(Voter voter) {
        this.voter = voter;
    }

    public Election getElection() {
        return election;
    }

    public void setElection(Election election) {
        this.election = election;
    }

    public byte[] getBallotContent() {
        return (this.encryptedVote + this.issuedAt.toString()).getBytes();
    }
}