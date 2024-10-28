package org.ciphertech.api_gateway.services.vote_storage_service;

import org.ciphertech.api_gateway.services.vote_storage_service.entity.SubmittedVote;
import org.ciphertech.api_gateway.services.vote_storage_service.repository.VoteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class VoteStorageService {

    private final VoteRepository voteRepository;

    @Autowired
    public VoteStorageService(VoteRepository voteRepository) {
        this.voteRepository = voteRepository;
    }

    public void storeVote(SubmittedVote vote) {
        voteRepository.save(vote);
    }

    public List<SubmittedVote> getVotes() {
        return (List<SubmittedVote>) voteRepository.findAll();
    }

    public Boolean verifyVote(String hash) {

        SubmittedVote vote = voteRepository.findByHash(hash);

        if (vote == null) {
            return false;
        }

        return true;
    }

    public void saveVote(SubmittedVote vote) {

    }
}
