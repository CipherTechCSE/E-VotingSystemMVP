package org.ciphertech.api_gateway.controller;

import org.ciphertech.api_gateway.service.VoteDelegationService;
import org.ciphertech.vote_authority_service.election.Election;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/votes")
public class VoteController {

    private final VoteDelegationService voteDelegationService;

    @Autowired
    public VoteController(VoteDelegationService voteDelegationService) {
        this.voteDelegationService = voteDelegationService;
    }

    @GetMapping("/elections")
    public List<Election> getAllElections() {
        return voteDelegationService.getAllElections();
    }

    // Add other endpoints as needed
}