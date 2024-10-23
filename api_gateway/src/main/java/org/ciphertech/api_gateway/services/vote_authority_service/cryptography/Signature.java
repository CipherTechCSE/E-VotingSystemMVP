package org.ciphertech.api_gateway.services.vote_authority_service.cryptography;

import org.ciphertech.api_gateway.services.vote_authority_service.entity.Ballot;

public class Signature {

    public String signBallot(String ballot, String privateKey) {
        return CryptoUtils.signData(ballot, privateKey);
    }

    public boolean verifySignature(String ballot, String signature, String publicKey) {
        return CryptoUtils.verifySignature(ballot, signature, publicKey);
    }

    public Ballot signBallot(Ballot ballot) {
        // Sign the ballot

        return ballot;
    }
}
