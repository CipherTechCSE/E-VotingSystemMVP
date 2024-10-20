package org.ciphertech.vote_authority_service.election;

import org.ciphertech.vote_count_service.VoteCountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ElectionService {

    private boolean electionActive = false;
    @Autowired
    private VoteCountService voteCountService;

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

    public void notifyVoteCount() {
        if (!electionActive) {
            throw new IllegalStateException("No election is running.");
        }
        voteCountService.notifyVoteCount();
    }

    public boolean isElectionActive() {
        return electionActive;
    }
}
