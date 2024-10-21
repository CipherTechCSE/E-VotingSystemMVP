package org.ciphertech.api_gateway.services.vote_authority_service.election;

import org.springframework.http.ResponseEntity;

public class ElectionController {
    private ElectionService electionService;

    public ResponseEntity<String> startElection() {
        electionService.startElection();
        return ResponseEntity.ok("Election started successfully");
    }

    public ResponseEntity<String> closeElection() {
        electionService.closeElection();
        return ResponseEntity.ok("Election closed successfully");
    }

    public ResponseEntity<String> notifyVoteCount() {
        electionService.notifyVoteCount();
        return ResponseEntity.ok("Vote count service notified");
    }
}
