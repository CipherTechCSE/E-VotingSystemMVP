package org.ciphertech.api_gateway.services.vote_authority_service.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
public class Election {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String electionName;

    private LocalDateTime startDate;

    private LocalDateTime endDate;

    private Boolean isActive;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Candidate> candidates;

    // Bidirectional relationship: one election can have many ballots
    @OneToMany(mappedBy = "election", cascade = CascadeType.ALL)
    private List<Ballot> ballots;

    // Bidirectional relationship: one election can have many voters
    @OneToMany(mappedBy = "election", cascade = CascadeType.ALL)
    private List<Voter> voters;


    // Constructors, getters, setters
    public Election() {
    }

    public Election(String electionName, LocalDateTime startDate, LocalDateTime endDate, Boolean isActive) {
        this.electionName = electionName;
        this.startDate = startDate;
        this.endDate = endDate;
        this.isActive = isActive;
    }

    // Getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getElectionName() {
        return electionName;
    }

    public void setElectionName(String electionName) {
        this.electionName = electionName;
    }

    public LocalDateTime getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDateTime startDate) {
        this.startDate = startDate;
    }

    public LocalDateTime getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDateTime endDate) {
        this.endDate = endDate;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }
}
