package org.ciphertech.api_gateway.services.vote_verifiaction_service;

import org.ciphertech.api_gateway.services.vote_storage_service.VoteStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class VoteVerificationService {

    private VoteStorageService voteStorageService;

    @Autowired
    public VoteVerificationService(VoteStorageService voteStorageService) {
        this.voteStorageService = voteStorageService;
    }

    public void verifyVote(String hash) {
        voteStorageService.verifyVote(hash);
    }
}
