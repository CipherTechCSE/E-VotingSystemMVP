package org.ciphertech.api_gateway.controller;

import org.ciphertech.api_gateway.dto.authority.AuthorityResponse;
import org.ciphertech.api_gateway.services.auth_service.models.User;
import org.ciphertech.api_gateway.services.vote_authority_service.VoteAuthorityService;
import org.ciphertech.api_gateway.services.vote_authority_service.entity.Ballot;
import org.ciphertech.api_gateway.services.vote_authority_service.entity.Candidate;
import org.ciphertech.api_gateway.services.vote_authority_service.entity.Election;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/authority")
public class VoteAuthorityController {

    private final VoteAuthorityService voteAuthorityService;

    // Inject VoteAuthorityService into the controller
    @Autowired
    public VoteAuthorityController(VoteAuthorityService voteAuthorityService) {
        this.voteAuthorityService = voteAuthorityService;
    }

    // Add an Election
    @PostMapping("/election")
    public ResponseEntity<AuthorityResponse<Election>> createElection(@RequestBody Election election) {
        try {
            // Call the service method to create the election
            Election savedElection = voteAuthorityService.createElection(election);

            // Return 201 CREATED status with success message and saved election
            AuthorityResponse<Election> response = new AuthorityResponse<>("Election created successfully", savedElection);
            return new ResponseEntity<>(response, HttpStatus.CREATED);

        } catch (IllegalArgumentException e) {
            // Return 400 BAD REQUEST status with error message
            AuthorityResponse<Election> response = new AuthorityResponse<>("Error creating election: " + e.getMessage(), null);
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }

    // Update an Election
    @PutMapping("/election/{id}")
    public ResponseEntity<AuthorityResponse<Election>> updateElection(@RequestBody Election election, @PathVariable Long id) {
        try {
            // Call the service method to update the election
            Election updatedElection = voteAuthorityService.updateElection(election, id);

            AuthorityResponse<Election> response = new AuthorityResponse<>("Election updated successfully", updatedElection);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            AuthorityResponse<Election> response = new AuthorityResponse<>("Error updating election: " + e.getMessage(), null);
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }

    // Delete an Election
    @DeleteMapping("/election/{id}")
    public ResponseEntity<AuthorityResponse<Candidate>> deleteElection(@PathVariable Long id) {
        try {
            // Call the service method to delete the election
            if (voteAuthorityService.deleteElection(id))
                return new ResponseEntity<>(new AuthorityResponse<>("Election deleted successfully", null), HttpStatus.OK);
            else
                return new ResponseEntity<>(new AuthorityResponse<>("Error deleting election", null), HttpStatus.BAD_REQUEST);

        } catch (IllegalArgumentException e) {
            AuthorityResponse<Candidate> response = new AuthorityResponse<>("Error deleting election: " + e.getMessage(), null);
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }

    // Add a Candidate to an Election
    @PostMapping("/candidate")
    public ResponseEntity<AuthorityResponse<Candidate>> addCandidate(@RequestBody Candidate candidate) {
        try {
            // Call the service method to add the candidate
            Candidate savedCandidate = voteAuthorityService.addCandidate(candidate);

            AuthorityResponse<Candidate> response = new AuthorityResponse<>("Candidate created successfully", savedCandidate);
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            AuthorityResponse<Candidate> response = new AuthorityResponse<>("Error creating candidate: " + e.getMessage(), null);
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }

    // Delete a Candidate from an Election
    @DeleteMapping("/candidate/{id}")
    public ResponseEntity<AuthorityResponse<Candidate>> deleteCandidate(@PathVariable Long id) {
        try {
            // Call the service method to delete the candidate
            if (voteAuthorityService.deleteCandidate(id))
                return new ResponseEntity<>(new AuthorityResponse<>("Candidate deleted successfully", null), HttpStatus.OK);
            else
                return new ResponseEntity<>(new AuthorityResponse<>("Error deleting candidate", null), HttpStatus.BAD_REQUEST);

        } catch (IllegalArgumentException e) {
            AuthorityResponse<Candidate> response = new AuthorityResponse<>("Error deleting candidate: " + e.getMessage(), null);
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }

    // Start an election
    @PostMapping("/start-election")
    public ResponseEntity<AuthorityResponse<Candidate>> startElection() {
        try {
            // Call the service method to start the election
            String message = voteAuthorityService.startElection();

            AuthorityResponse<Candidate> response = new AuthorityResponse<>(message, null);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            AuthorityResponse<Candidate> response = new AuthorityResponse<>("Error starting election: " + e.getMessage(), null);
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }

    // End an election
    @PostMapping("/end-election")
    public ResponseEntity<AuthorityResponse<Candidate>> endElection() {
        try {
            // Call the service method to end the election
            String message = voteAuthorityService.endElection();

            AuthorityResponse<Candidate> response = new AuthorityResponse<>(message, null);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            AuthorityResponse<Candidate> response = new AuthorityResponse<>("Error ending election: " + e.getMessage(), null);
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }

    // Generate group key for eligible voters
    @PostMapping("/generate-group-key")
    public ResponseEntity<AuthorityResponse<Candidate>> generateGroupKey() {
        try {
            // Call the service method to generate the group key
            String message = voteAuthorityService.generateGroupKey();

            AuthorityResponse<Candidate> response = new AuthorityResponse<>(message, null);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            AuthorityResponse<Candidate> response = new AuthorityResponse<>("Error generating group key: " + e.getMessage(), null);
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }

    // Generate secret key for vote encryption
    @PostMapping("/generate-secret-key")
    public ResponseEntity<AuthorityResponse<Candidate>> generateSecretKey() {
        try {
            // Call the service method to generate the secret key
            String message = voteAuthorityService.generateSecretKey();

            AuthorityResponse<Candidate> response = new AuthorityResponse<>(message, null);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            AuthorityResponse<Candidate> response = new AuthorityResponse<>("Error generating secret key: " + e.getMessage(), null);
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/requestBallot/{electionID}")
    public ResponseEntity<AuthorityResponse<Ballot>> requestBallot(@PathVariable Long electionID) {
        try {

            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            User user = (User) authentication.getPrincipal();

            // Call the service method to generate the secret key
            Ballot ballot = voteAuthorityService.requestBallot(user, electionID);

            AuthorityResponse<Ballot> response = new AuthorityResponse<>("Ballot generated successfully", ballot);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            AuthorityResponse<Ballot> response = new AuthorityResponse<>("Error generating ballot: " + e.getMessage(), null);
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }

    // Sign a ballot
    @PostMapping("/sign-ballot")
    public ResponseEntity<AuthorityResponse<Ballot>> signBallot(@RequestBody Ballot ballot) {
        try {
            // Call the service method to sign the ballot
            Ballot signedBallot = voteAuthorityService.signBallot(ballot);

            AuthorityResponse<Ballot> response = new AuthorityResponse<>("Ballot signed successfully", signedBallot);
            return new ResponseEntity<>(response, HttpStatus.OK);

        } catch (IllegalArgumentException e) {
            AuthorityResponse<Ballot> response = new AuthorityResponse<>("Error signing ballot: " + e.getMessage(), null);
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }

    // Called by Vote submission service to confirm ballot submission
    @PostMapping("/internal/confirm-ballot-submission/{id}")
    public ResponseEntity<AuthorityResponse<Ballot>> confirmBallotSubmission(@PathVariable Long id) {
        try {
            // Call the service method to confirm the ballot submission
            Boolean confirmed = voteAuthorityService.confirmBallotSubmission(id);

            AuthorityResponse<Ballot> response = new AuthorityResponse<>(confirmed ? "Ballot submitted successfully" : "Error submitting ballot", null);
            return new ResponseEntity<>(response, confirmed ? HttpStatus.OK : HttpStatus.BAD_REQUEST);
        } catch (IllegalArgumentException e) {
            AuthorityResponse<Ballot> response = new AuthorityResponse<>("Error submitting ballot: " + e.getMessage(), null);
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }

    // Notify vote count service
    @PostMapping("/notify-vote-count")
    public ResponseEntity<AuthorityResponse<Candidate>> notifyVoteCount() {
        try {
            // Call the service method to notify the vote count service
            String message = voteAuthorityService.notifyVoteCount();

            AuthorityResponse<Candidate> response = new AuthorityResponse<>(message, null);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            AuthorityResponse<Candidate> response = new AuthorityResponse<>("Error notifying vote count service: " + e.getMessage(), null);
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }
}
