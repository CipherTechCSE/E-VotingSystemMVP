package org.ciphertech.api_gateway.services.vote_authority_service.repository;

import org.ciphertech.api_gateway.services.vote_authority_service.entity.Candidate;
import org.springframework.data.repository.CrudRepository;

import java.util.Collection;

public interface CandidateRepository extends CrudRepository<Candidate, Long> {
    Collection<Object> findByElectionId(Long election);
    // You can add custom query methods if needed
}