package org.ciphertech.api_gateway.controller;

import org.ciphertech.api_gateway.services.vote_verifiaction_service.VoteVerificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController("/api/verify")
public class VoteVerificationController {

        private final VoteVerificationService voteVerificationService;

        @Autowired
        public VoteVerificationController(VoteVerificationService voteVerificationService) {
            this.voteVerificationService = voteVerificationService;
        }

        @GetMapping("/verify-vote")
        public ResponseEntity<Boolean> verifyVote(@RequestParam String hash) {
            return ResponseEntity.ok(voteVerificationService.verifyVote(hash));
        }
}
