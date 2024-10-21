package org.ciphertech.api_gateway.services.vote_authority_service.keymanagement;

import org.ciphertech.api_gateway.services.vote_authority_service.signatures.CryptoUtils;
import org.springframework.stereotype.Service;

@Service
public class KeyGenerationService {

    public KeyPair generateKeys() {
        // Example logic for generating a key pair
        String publicKey = CryptoUtils.generatePublicKey();
        String privateKey = CryptoUtils.generatePrivateKey();

        return new KeyPair(publicKey, privateKey);
    }
}