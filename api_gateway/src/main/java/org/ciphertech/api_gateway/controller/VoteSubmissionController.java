package org.ciphertech.api_gateway.controller;

import org.ciphertech.api_gateway.services.vote_submission_service.VoteSubmissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/vote-submission")
public class VoteSubmissionController {

    private final VoteSubmissionService voteSubmissionService;

    @Autowired
    public VoteSubmissionController(VoteSubmissionService voteSubmissionService) {
        this.voteSubmissionService = voteSubmissionService;
    }

    // Controller methods
    @RequestMapping("/submit-vote")
    public void submitVote() {
        voteSubmissionService.submitVote();
    }
}
