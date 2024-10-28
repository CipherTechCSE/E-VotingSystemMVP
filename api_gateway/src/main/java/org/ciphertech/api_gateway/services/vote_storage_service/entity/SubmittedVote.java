package org.ciphertech.api_gateway.services.vote_storage_service.entity;

import jakarta.persistence.*;
import org.ciphertech.api_gateway.services.vote_authority_service.entity.Election;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "SubmittedVotes")
public class SubmittedVote {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String hash;

    private String encryptedVote; // Encrypted vote will not be stored in the database

    @Lob
    private String voterSignature; // Signature for verification

    @ElementCollection
    private List<String> multiSignatures; // List of authorities

    @Column(nullable = false)
    private LocalDateTime issuedAt;

    private LocalDateTime submittedAt;

    @ManyToOne
    @JoinColumn(name = "election_id", nullable = false)
    private Election election;

    // Constructors, Getters, and Setters
    public SubmittedVote() {}
    public SubmittedVote(String encryptedVote, String voterSignature, List<String> multiSignatures, LocalDateTime issuedAt, LocalDateTime submittedAt, Election election, String hash) {
        this.encryptedVote = encryptedVote;
        this.voterSignature = voterSignature;
        this.multiSignatures = multiSignatures;
        this.issuedAt = issuedAt;
        this.submittedAt = submittedAt;
        this.election = election;
        this.hash = hash;
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

    public Election getElection() {
        return election;
    }

    public void setElection(Election election) {
        this.election = election;
    }

    public byte[] getBallotContent() {
        return (this.encryptedVote + this.issuedAt.toString()).getBytes();
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }
}
