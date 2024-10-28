package org.ciphertech.api_gateway.services.vote_storage_service.repository;

import org.ciphertech.api_gateway.services.vote_storage_service.entity.SubmittedVote;
import org.springframework.data.repository.CrudRepository;

public interface VoteRepository extends CrudRepository<SubmittedVote, Long> {
    SubmittedVote findByHash(String hash);
    // You can add custom query methods if needed
}
