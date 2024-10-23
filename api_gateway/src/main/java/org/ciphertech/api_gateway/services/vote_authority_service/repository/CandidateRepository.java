package org.ciphertech.api_gateway.services.vote_authority_service.repository;

import org.ciphertech.api_gateway.services.vote_authority_service.entity.Candidate;
import org.springframework.data.repository.CrudRepository;

public interface CandidateRepository extends CrudRepository<Candidate, Long> {
    // You can add custom query methods if needed
}