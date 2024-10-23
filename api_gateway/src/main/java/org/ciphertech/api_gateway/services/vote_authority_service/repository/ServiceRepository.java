package org.ciphertech.api_gateway.services.vote_authority_service.repository;

import org.ciphertech.api_gateway.services.vote_authority_service.entity.VotingSystemService;
import org.springframework.data.repository.CrudRepository;

public interface ServiceRepository extends CrudRepository<VotingSystemService, Long> {
    // You can add custom query methods if needed
}