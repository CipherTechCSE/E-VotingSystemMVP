package org.ciphertech.api_gateway.services.vote_authority_service.repository;

import org.ciphertech.api_gateway.services.vote_authority_service.entity.Ballot;
import org.springframework.data.repository.CrudRepository;

public interface BallotRepository extends CrudRepository<Ballot, Long> {
    // You can add custom query methods if needed
}