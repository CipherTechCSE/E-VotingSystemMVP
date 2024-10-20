package org.ciphertech.vote_authority_service.signatures;

import org.springframework.stereotype.Service;

@Service
public class SignatureService {

    public String signBallot(String ballot, String privateKey) {
        return CryptoUtils.signData(ballot, privateKey);
    }

    public boolean verifySignature(String ballot, String signature, String publicKey) {
        return CryptoUtils.verifySignature(ballot, signature, publicKey);
    }
}
