package org.ciphertech.api_gateway.services.vote_storage_service.repository;

import org.ciphertech.api_gateway.services.vote_authority_service.entity.Candidate;
import org.springframework.data.repository.CrudRepository;

public interface VoteRepository extends CrudRepository<Candidate, Long> {
    // You can add custom query methods if needed
}
