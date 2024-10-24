package org.ciphertech.api_gateway.common.cryptography;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.ciphertech.api_gateway.services.vote_authority_service.entity.VotingSystemService;
import org.ciphertech.api_gateway.services.vote_authority_service.repository.ServiceRepository;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

public class MultiSignature {

    private final int RSA_KEY_SIZE; // RSA key length
    private static final String ALGORITHM = "RSA";  // Algorithm for key generation
    private static final String SIGNATURE_ALGORITHM = "SHA256withRSA";  // Signature algorithm

    static {
        Security.addProvider(new BouncyCastleProvider());
    }

    public MultiSignature(int keySize) {
        this.RSA_KEY_SIZE = keySize;
    }

    // Store service keys and return the created Service object
    public VotingSystemService getServiceKeys(Long serviceId, String serviceName, String description, String url) throws GeneralSecurityException {
        KeyPair keyPair = generateKeyPair();

        VotingSystemService service = new VotingSystemService();
        service.setId(serviceId);
        service.setName(serviceName);
        service.setDescription(description);
        service.setUrl(url);
        service.setPublicKey(keyPair.getPublic().getEncoded());
        service.setPrivateKey(keyPair.getPrivate().getEncoded());

        return service;
    }

    // Generate a new RSA key pair with a key length of 2048 bits
    public KeyPair generateKeyPair() throws NoSuchAlgorithmException {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(ALGORITHM);
        keyPairGenerator.initialize(RSA_KEY_SIZE); // Set key size to 2048 bits        
        
        return keyPairGenerator.generateKeyPair();
    }

    // Sign the data using the service's private key
    public String signData(Long serviceId, byte[] data, KeyPair keyPair) throws Exception {
        PrivateKey privateKey = keyPair.getPrivate();

        Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);
        signature.initSign(privateKey);
        signature.update(data);
        byte[] signedData = signature.sign();
        return Base64.getEncoder().encodeToString(signedData);
    }

    // Verify the signature using the service's public key
    public boolean verifySignature(Long serviceId, byte[] data, String signatureStr, KeyPair keyPair) throws Exception {
        PublicKey publicKey = keyPair.getPublic();

        Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);
        signature.initVerify(publicKey);
        signature.update(data);
        byte[] signatureBytes = Base64.getDecoder().decode(signatureStr);
        return signature.verify(signatureBytes);
    }
}
