package org.ciphertech.api_gateway.services.vote_authority_service.entity;

import jakarta.persistence.*;

@Entity
public class Candidate {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String nic;

    private String name;

    private String party;

    @ManyToOne
    @JoinColumn(name = "election_id", nullable = false)
    private Election election;

    // Constructors, getters, setters
    public Candidate() {
    }

    public Candidate(String name, String party, String nic, Election election) {
        this.name = name;
        this.party = party;
        this.nic = nic;
        this.election = election;
    }

    public Candidate(String name, String party, String nic) {
        this.name = name;
        this.party = party;
        this.nic = nic;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getParty() {
        return party;
    }

    public void setParty(String party) {
        this.party = party;
    }

    public String getNic() {
        return nic;
    }

    public void setNic(String nic) {
        this.nic = nic;
    }

    public Election getElection() {
        return election;
    }

    public void setElection(Election election) {
        this.election = election;
    }
}