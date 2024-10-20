package org.ciphertech.vote_authority_service.election;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
