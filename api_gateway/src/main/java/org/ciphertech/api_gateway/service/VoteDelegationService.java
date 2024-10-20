package org.ciphertech.api_gateway.service;

import org.ciphertech.vote_authority_service.election.Election;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
public class VoteDelegationService {

    private final RestTemplate restTemplate;

    // Base URLs for the other services
    private final String voteAuthorityServiceUrl = "http://localhost:8081/api/elections"; // Replace with actual URL
    private final String voteCountServiceUrl = "http://localhost:8082/api/voteCounts"; // Replace with actual URL

    @Autowired
    public VoteDelegationService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public List<Election> getAllElections() {
        // Call the vote authority service to get all elections
        Election[] elections = restTemplate.getForObject(voteAuthorityServiceUrl, Election[].class);
        return List.of(elections);
    }

    // Implement other service methods as needed
}
