package org.ciphertech.api_gateway.services.vote_authority_service.repository;

import org.ciphertech.api_gateway.services.vote_authority_service.entity.Election;
import org.springframework.data.repository.CrudRepository;

public interface ElectionRepository extends CrudRepository<Election, Long> {
    // You can add custom query methods if needed
}