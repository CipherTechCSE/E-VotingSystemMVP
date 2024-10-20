package org.ciphertech.vote_count_service;

import org.springframework.stereotype.Service;

@Service
public class VoteCountService {

    public String notifyVoteCount() {
        // Logic to notify the vote count service
        return "Vote count service notified!";
    }
}
