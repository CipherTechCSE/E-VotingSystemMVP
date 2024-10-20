package org.ciphertech.api_gateway.controller;

import org.ciphertech.vote_authority_service.VoteAuthorityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/authority")
public class VoteAuthorityController {

    private final VoteAuthorityService voteAuthorityService;

    // Inject VoteAuthorityService into the controller
    @Autowired
    public VoteAuthorityController(VoteAuthorityService voteAuthorityService) {
        this.voteAuthorityService = voteAuthorityService;
    }

    // Start an election
    @PostMapping("/start-election")
    public String startElection() {
        return voteAuthorityService.startElection();
    }

    // End an election
    @PostMapping("/end-election")
    public String endElection() {
        return voteAuthorityService.endElection();
    }

    // Generate group key for eligible voters
    @PostMapping("/generate-group-key")
    public String generateGroupKey() {
        return voteAuthorityService.generateGroupKey();
    }

    // Generate secret key for vote encryption
    @PostMapping("/generate-secret-key")
    public String generateSecretKey() {
        return voteAuthorityService.generateSecretKey();
    }

    // Sign a ballot
    @PostMapping("/sign-ballot")
    public String signBallot(@RequestBody String ballot) {
        return voteAuthorityService.signBallot(ballot);
    }

    // Notify vote count service
    @PostMapping("/notify-vote-count")
    public String notifyVoteCount() {
        return voteAuthorityService.notifyVoteCount();
    }
}
