package org.ciphertech.vote_authority_service.election;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/election")
public class ElectionController {
    @Autowired
    private ElectionService electionService;

    @PostMapping("/start")
    public ResponseEntity<String> startElection() {
        electionService.startElection();
        return ResponseEntity.ok("Election started successfully");
    }

    @PostMapping("/close")
    public ResponseEntity<String> closeElection() {
        electionService.closeElection();
        return ResponseEntity.ok("Election closed successfully");
    }
}
