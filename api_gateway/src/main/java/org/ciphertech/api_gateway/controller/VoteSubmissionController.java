package org.ciphertech.api_gateway.controller;

import org.ciphertech.api_gateway.services.vote_authority_service.VoteAuthorityService;
import org.ciphertech.api_gateway.services.vote_authority_service.entity.Ballot;
import org.ciphertech.api_gateway.services.vote_submission_service.VoteSubmissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/vote-submission")
public class VoteSubmissionController {

    private final VoteSubmissionService voteSubmissionService;
    private final VoteAuthorityService voteAuthorityService;

    @Autowired
    public VoteSubmissionController(VoteSubmissionService voteSubmissionService, VoteAuthorityService voteAuthorityService) {
        this.voteSubmissionService = voteSubmissionService;
        this.voteAuthorityService = voteAuthorityService;
    }

    // Controller methods
    @PostMapping("/submit-vote")
    public void submitVote(@RequestBody Ballot ballot) {
        voteSubmissionService.submitVote(ballot);
        voteAuthorityService.confirmBallotSubmission(ballot.getId());
    }
}
