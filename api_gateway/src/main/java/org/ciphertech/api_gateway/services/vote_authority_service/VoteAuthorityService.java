package org.ciphertech.api_gateway.services.vote_authority_service;

import org.ciphertech.api_gateway.services.vote_authority_service.entity.Candidate;
import org.ciphertech.api_gateway.services.vote_authority_service.entity.Election;
import org.ciphertech.api_gateway.services.vote_authority_service.repository.BallotRepository;
import org.ciphertech.api_gateway.services.vote_authority_service.repository.CandidateRepository;
import org.ciphertech.api_gateway.services.vote_authority_service.repository.ElectionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class VoteAuthorityService {

    private final ElectionRepository electionRepository;
    private final CandidateRepository candidateRepository;
    private final BallotRepository ballotRepository;

    @Autowired
    public VoteAuthorityService(ElectionRepository electionRepository, CandidateRepository candidateRepository, BallotRepository ballotRepository) {
        this.electionRepository = electionRepository;
        this.candidateRepository = candidateRepository;
        this.ballotRepository = ballotRepository;
    }

    // Add a candidate to the election
    public Candidate addCandidate(Candidate candidate) {

        // Validate inputs (e.g., ensure candidate name is not empty, etc.)
        if (candidate.getName().isEmpty()) {
            throw new IllegalArgumentException("Candidate name cannot be empty.");
        }

        // Save the candidate to the database
        return candidateRepository.save(candidate);
    }

    // Delete a candidate from the election
    public Boolean deleteCandidate(Integer id) {
        Candidate candidate = candidateRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Candidate not found with id: " + id));
        candidateRepository.delete(candidate);

        return true;
    }


    // Remove a candidate from the election
    public String removeCandidate(String name) {
        // Logic for removing a candidate
        return "Candidate removed!";
    }

    public Election createElection(Election election) {
        // Validate inputs (e.g., ensure endDate is after startDate, etc.)
        if (election.getEndDate().isBefore(election.getStartDate())) {
            throw new IllegalArgumentException("End date must be after start date.");
        }

        // Save the election to the database
        return electionRepository.save(election);
    }

    public Election updateElection(Election election, Integer id) {
        // Validate inputs (e.g., ensure endDate is after startDate, etc.)
        if (election.getEndDate().isBefore(election.getStartDate())) {
            throw new IllegalArgumentException("End date must be after start date.");
        }

        // Update the election in the database
        Election existingElection = electionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Election not found with id: " + id));

        existingElection.setStartDate(election.getStartDate());
        existingElection.setEndDate(election.getEndDate());
        existingElection.setElectionName(election.getElectionName());

        return electionRepository.save(existingElection);
    }

    public Boolean deleteElection(Integer id) {
        Election election = electionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Election not found with id: " + id));
        electionRepository.delete(election);

        return true;
    }

    // Start an election
    public String startElection() {
        // Logic for starting the election
        return "Election started!";
    }

    // End an election
    public String endElection() {
        // Logic for ending the election
        return "Election ended!";
    }

    // Generate group keys for eligible voters
    public String generateGroupKey() {
        // Logic for group key generation
        return "Group key generated for eligible voters!";
    }

    // Generate secret key for vote encryption
    public String generateSecretKey() {
        // Logic for secret key generation
        return "Secret key generated for vote encryption!";
    }

    // Sign a ballot to authenticate
    public String signBallot(String ballot) {
        // Logic for signing a ballot
        return "Ballot signed!";
    }

    // Notify the vote count service
    public String notifyVoteCount() {
        // Logic to notify the vote count service
        return "Vote count service notified!";
    }
}
