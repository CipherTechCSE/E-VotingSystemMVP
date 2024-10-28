package org.ciphertech.api_gateway.services.vote_submission_service;

import org.ciphertech.api_gateway.services.vote_authority_service.entity.Ballot;
import org.ciphertech.api_gateway.services.vote_storage_service.VoteStorageService;
import org.ciphertech.api_gateway.services.vote_storage_service.entity.SubmittedVote;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class VoteSubmissionService {

    private final VoteStorageService voteStorageService;

    @Autowired
    public VoteSubmissionService(VoteStorageService voteStorageService) {
        this.voteStorageService = voteStorageService;
    }
    public void submitVote(Ballot ballot) {

        SubmittedVote vote = new SubmittedVote();
        vote.setEncryptedVote(ballot.getEncryptedVote());
        vote.setVoterSignature(ballot.getVoterSignature());
        vote.setMultiSignatures(ballot.getMultiSignatures());
        vote.setIssuedAt(ballot.getIssuedAt());
        vote.setSubmittedAt(ballot.getSubmittedAt());
        vote.setElection(ballot.getElection());

        voteStorageService.saveVote(vote);
    }
}
