package org.ciphertech.api_gateway.services.vote_authority_service;

import org.ciphertech.api_gateway.services.auth_service.models.User;
import org.ciphertech.api_gateway.common.cryptography.GroupSignature;
import org.ciphertech.api_gateway.common.cryptography.MultiSignature;
import org.ciphertech.api_gateway.services.vote_authority_service.entity.*;
import org.ciphertech.api_gateway.services.vote_authority_service.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.time.LocalDateTime;
import java.util.Base64;

@Service
public class VoteAuthorityService {

    private final String SECRET_KEY = "A7uaf9afyyahyOYG6T87h";
    private static final String AES = "AES"; // AES algorithm

    private final ElectionRepository electionRepository;
    private final CandidateRepository candidateRepository;
    private final BallotRepository ballotRepository;
    private final ServiceRepository serviceRepository;
    private final VoterRepository voterRepository;
    private final MultiSignature multiSignature;
    private final GroupSignature groupSignature;


    private enum ServiceId {
        VOTE_AUTHORITY_SERVICE(1),
        AUTH_SERVICE(2),
        VOTE_COUNT_SERVICE(3),
        VOTE_SUBMISSION_SERVICE(4),
        VOTE_VERIFICATION_SERVICE(5),
        VOTE_STORAGE_SERVICE(6),
        PUBLIC_SERVICE(7);

        private final int id;

        ServiceId(int id) {
            this.id = id;
        }

        public int getId() {
            return id;
        }
    }

    @Autowired
    public VoteAuthorityService(
            ElectionRepository electionRepository,
            CandidateRepository candidateRepository,
            BallotRepository ballotRepository,
            ServiceRepository serviceRepository,
            VoterRepository voterRepository,
            GroupSignature groupSignature  // Autowired bean
    ) {
        this.electionRepository = electionRepository;
        this.candidateRepository = candidateRepository;
        this.ballotRepository = ballotRepository;
        this.serviceRepository = serviceRepository;
        this.voterRepository = voterRepository;
        this.multiSignature = new MultiSignature(2048);
        this.groupSignature = groupSignature;

        if (multiSignature == null) {
            throw new IllegalStateException("MultiSignature bean not found!");
        }
        // Create a multi-signature key pair for the service
        try {
            VotingSystemService service = multiSignature.getServiceKeys(
                    (long) ServiceId.VOTE_AUTHORITY_SERVICE.getId(),
                    "Vote Authority Service",
                    "Service responsible for managing elections",
                    "http://localhost:8080"
            );

            // Store
            serviceRepository.save(service);

        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        }
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
        Voter voter = voterRepository.findByUserId(user.getId())
            .orElseThrow(() -> new IllegalArgumentException("Voter not found with user id: " + user.getId()));

        // Set the voter and election
        ballot.setVoter(voter);

        Election electionObj = electionRepository.findById(election)
                .orElseThrow(() -> new IllegalArgumentException("Election not found with id: " + election));

        ballot.setElection(electionObj);

        // Save the ballot to the database
        return ballotRepository.save(ballot);
    }

    public Ballot signBallot(Ballot ballot) throws Exception {
        // Sign the ballot
        // Get the ballot content and convert it into a byte stream
        byte[] ballotContent = ballot.getBallotContent();

        Long serviceId = (long) ServiceId.VOTE_AUTHORITY_SERVICE.getId();
        // Get the multi signature key pair
        KeyPair keyPair = retrieveServiceKeys(serviceId);

        // Get the multi signature
        String multiSignature = this.multiSignature.signData(serviceId, ballotContent, keyPair);

        ballot.addMultiSignature(multiSignature);

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

    // Generate group keys for a single eligible voter and store them
    public KeyPair joinVoterGroup(User user, Long electionId) throws NoSuchAlgorithmException {
        KeyPair voterKeyPair = groupSignature.joinVotersGroup(user.getId().toString()); // Generate a key pair for the voter
        String publicKey = Base64.getEncoder().encodeToString(voterKeyPair.getPublic().getEncoded()); // Encode the public key

        // Create a new Voter entity
        Voter voter = new Voter(user);
        voter.setPublicKey(publicKey);

        voterRepository.save(voter); // Save the voter entity to the database

        return voterKeyPair;  // Return the key pair of the voter
    }

    // Generate secret key for vote encryption
    public String generateSecretKey() {
        // Logic for secret key generation
        return "Secret key generated for vote encryption!";
    }

    // Allow a service to join and return the multi-signature for that service
    public VotingSystemService joinService(String serviceName, String serviceUrl, String serviceDescription) throws GeneralSecurityException {
        // Validate the service name
        if (serviceName == null || serviceName.isEmpty()) {
            throw new IllegalArgumentException("Service name cannot be empty.");
        }

        long serviceId;

        // Ensure the service name is one of the predefined values
        try {
            serviceId = (long) ServiceId.valueOf(serviceName.toUpperCase()).getId();
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid service name: " + serviceName);
        }

        // Create a new Service with multi-signature and store the service keys
        VotingSystemService service = multiSignature.getServiceKeys(serviceId,serviceName, serviceDescription, serviceUrl);

        // Save the service to the database
        return serviceRepository.save(service);
    }

    // Retrieve service keys based on the service ID
    public KeyPair retrieveServiceKeys(Long serviceId) throws GeneralSecurityException {
        VotingSystemService service = serviceRepository.findById(serviceId).orElse(null);

        if (service != null) {
            byte[] decryptedPublicKey = decryptKey(service.getPublicKey());
            byte[] decryptedPrivateKey = decryptKey(service.getPrivateKey());

            // Convert decrypted byte arrays back into Key objects
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            PublicKey publicKey = keyFactory.generatePublic(new X509EncodedKeySpec(decryptedPublicKey));
            PrivateKey privateKey = keyFactory.generatePrivate(new PKCS8EncodedKeySpec(decryptedPrivateKey));

            return new KeyPair(publicKey, privateKey);
        }
        throw new IllegalArgumentException("Service not found: " + serviceId);
    }

    // Notify the vote count service
    public String notifyVoteCount() {
        // Logic to notify the vote count service
        return "Vote count service notified!";
    }

    // Encrypt the key using AES
    private byte[] encryptKey(byte[] key) throws GeneralSecurityException {
        SecretKeySpec secretKey = new SecretKeySpec(SECRET_KEY.getBytes(), AES);
        Cipher cipher = Cipher.getInstance(AES);
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        return cipher.doFinal(key);
    }

    // Decrypt the key using AES
    private byte[] decryptKey(byte[] encryptedKey) throws GeneralSecurityException {
        SecretKeySpec secretKey = new SecretKeySpec(SECRET_KEY.getBytes(), AES);
        Cipher cipher = Cipher.getInstance(AES);
        cipher.init(Cipher.DECRYPT_MODE, secretKey);
        return cipher.doFinal(encryptedKey);
    }
}
