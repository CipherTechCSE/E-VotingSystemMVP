package org.ciphertech.api_gateway.services.vote_count_service;

import org.ciphertech.api_gateway.services.vote_storage_service.VoteStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class VoteCountService {

        private VoteStorageService voteStorageService;

        @Autowired
        public VoteCountService(VoteStorageService voteStorageService) {
            this.voteStorageService = voteStorageService;
        }

        public void notifyVoteCount(Integer electionId) {
//            voteStorageService.verifyVote(electionId);
        }
}
