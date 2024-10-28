package org.ciphertech.api_gateway.dto.authority;

public class CandidateCreationRequest {

    private String nic;

    private String name;

    private String party;

    private String electionId;

    // Getters and setters

    public CandidateCreationRequest() {
    }

    public CandidateCreationRequest(String nic, String name, String party, String electionId) {
        this.nic = nic;
        this.name = name;
        this.party = party;
        this.electionId = electionId;
    }

    public String getNic() {
        return nic;
    }

    public void setNic(String nic) {
        this.nic = nic;
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

    public Long getElectionId() {
        return electionId.isEmpty() ? null : Long.parseLong(electionId);
    }

    public void setElectionId(String electionId) {
        this.electionId = electionId;
    }
}
