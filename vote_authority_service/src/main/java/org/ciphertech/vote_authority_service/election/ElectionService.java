package org.ciphertech.vote_authority_service.election;

import org.springframework.stereotype.Service;

@Service
public class ElectionService {

    private boolean electionActive = false;

    public void startElection() {
        if (electionActive) {
            throw new IllegalStateException("Election is already running.");
        }
        electionActive = true;
    }

    public void closeElection() {
        if (!electionActive) {
            throw new IllegalStateException("No election is running.");
        }
        electionActive = false;
    }

    public boolean isElectionActive() {
        return electionActive;
    }
}
