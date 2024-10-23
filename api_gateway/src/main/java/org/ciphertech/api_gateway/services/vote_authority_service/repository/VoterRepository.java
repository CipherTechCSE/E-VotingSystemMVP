package org.ciphertech.api_gateway.services.vote_authority_service.repository;

import org.ciphertech.api_gateway.services.vote_authority_service.entity.Voter;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface VoterRepository extends CrudRepository<Voter, Long> {
    Optional<Voter> findByUserId(Long userId);
}