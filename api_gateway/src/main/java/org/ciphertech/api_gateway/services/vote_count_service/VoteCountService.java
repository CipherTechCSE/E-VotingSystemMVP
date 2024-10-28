package org.ciphertech.api_gateway.services.vote_count_service;

import org.ciphertech.api_gateway.common.cryptography.GroupSignature;
import org.ciphertech.api_gateway.common.cryptography.MultiSignature;
import org.ciphertech.api_gateway.services.vote_storage_service.VoteStorageService;
import org.ciphertech.api_gateway.services.vote_storage_service.entity.SubmittedVote;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.PrivateKey;
import java.util.List;

@Service
public class VoteCountService {

        private GroupSignature groupSignature;
        private MultiSignature multiSignature;
        private PrivateKey privateKey;

        private VoteStorageService voteStorageService;

        @Autowired
        public VoteCountService(VoteStorageService voteStorageService) {
                this.voteStorageService = voteStorageService;
        }

        public void setGroupSignature(GroupSignature groupSignature) {
                this.groupSignature = groupSignature;
        }

        public void setMultiSignature(MultiSignature multiSignature) {
                this.multiSignature = multiSignature;
        }

        public void notifyVoteCount(Integer electionId) {

                List<SubmittedVote> votes = voteStorageService.getVotes();

                // Count decrypted votes after final verification

        }
}
