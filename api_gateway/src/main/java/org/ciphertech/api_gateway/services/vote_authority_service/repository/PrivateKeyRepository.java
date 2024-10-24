package org.ciphertech.api_gateway.services.vote_authority_service.repository;

import org.ciphertech.api_gateway.services.vote_authority_service.entity.PrivateKeyEntity;
import org.springframework.data.repository.CrudRepository;

public interface PrivateKeyRepository extends CrudRepository<PrivateKeyEntity, Long> {
    // You can add custom query methods if needed
}
