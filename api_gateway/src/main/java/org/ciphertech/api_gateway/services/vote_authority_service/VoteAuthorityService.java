package org.ciphertech.api_gateway.services.vote_authority_service;

import org.ciphertech.api_gateway.services.auth_service.models.User;
import org.ciphertech.api_gateway.services.vote_authority_service.cryptography.Signature;
import org.ciphertech.api_gateway.services.vote_authority_service.entity.Ballot;
import org.ciphertech.api_gateway.services.vote_authority_service.entity.Candidate;
import org.ciphertech.api_gateway.services.vote_authority_service.entity.Election;
import org.ciphertech.api_gateway.services.vote_authority_service.entity.Voter;
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
    public Boolean deleteCandidate(Long id) {
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

    public Election updateElection(Election election, Long id) {
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

    public Boolean deleteElection(Long id) {
        Election election = electionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Election not found with id: " + id));
        electionRepository.delete(election);

        return true;
    }

    public Ballot requestBallot(User user, Long election) {
                                // Validate inputs (e.g., ensure voter is eligible, etc.)

        // Create a new ballot
        Ballot ballot = new Ballot();
        Voter voter = new Voter(user);

        // Set the voter and election
        ballot.setVoter(voter);

        Election electionObj = electionRepository.findById(election)
                .orElseThrow(() -> new IllegalArgumentException("Election not found with id: " + election));

        ballot.setElection(electionObj);

        // Save the ballot to the database
        return ballotRepository.save(ballot);
    }

    public Ballot signBallot(Ballot ballot) {
        // Sign the ballot
        Signature signatureService = new Signature();
        ballot = signatureService.signBallot(ballot);

        return ballot;
    }

    public Boolean confirmBallotSubmission(Long id) {
        Ballot ballot = ballotRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Ballot not found with id: " + id));
        ballot.setSubmittedAt(LocalDateTime.now());
        ballotRepository.save(ballot);

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
