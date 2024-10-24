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
import java.util.List;

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

    // Generate a new RSA key pair with a key length of 2048 bits
    public KeyPair generateKeyPair() throws NoSuchAlgorithmException {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(ALGORITHM);
        keyPairGenerator.initialize(RSA_KEY_SIZE); // Set key size to 2048 bits        
        
        return keyPairGenerator.generateKeyPair();
    }

    // Sign the data using the service's private key
    public String signData(byte[] data, PrivateKey privateKey) throws Exception {

        Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);
        signature.initSign(privateKey);
        signature.update(data);
        byte[] signedData = signature.sign();
        return Base64.getEncoder().encodeToString(signedData);
    }

    // Verify the signature using the service's public key
    public boolean verifySignature(byte[] data, String signatureStr, PublicKey publicKey) throws Exception {

        Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);
        signature.initVerify(publicKey);
        signature.update(data);
        byte[] signatureBytes = Base64.getDecoder().decode(signatureStr);
        return signature.verify(signatureBytes);
    }

    public String signMultiSignature(String message, PrivateKey privateKey, List<String> previousSignatures, List<PublicKey> publicKeys) throws Exception {
        // Prepare the data for signing: concatenate the message with the previous signatures
        StringBuilder sb = new StringBuilder(message);
        for (int i = 0; i < previousSignatures.size(); i++) {
            sb.append(previousSignatures.get(i));
            // Verify each previous signature with the corresponding public key
            boolean isVerified = verifySignature(sb.toString().getBytes(), previousSignatures.get(i), publicKeys.get(i));
            if (!isVerified) {
                throw new SecurityException("Signature verification failed for signature: " + previousSignatures.get(i));
            }
        }

        // Convert to bytes for signing
        byte[] dataToSign = sb.toString().getBytes();

        // Sign the data using the provided private key
        return signData(dataToSign, privateKey);
    }

    public boolean verifyMultiSignature(String message, List<String> signatures, List<PublicKey> publicKeys) throws Exception {
        // Step 1: Verify each signature
        for (int i = 0; i < signatures.size(); i++) {
            String currentSignature = signatures.get(i);
            PublicKey currentPublicKey = publicKeys.get(i);

            // Prepare the data for verification: concatenate the message with the previous signatures
            byte[] dataToVerify;
            if (i == 0) {
                // First step: verify the message
                dataToVerify = message.getBytes();
            } else {
                // For subsequent steps, include the previous signatures
                StringBuilder sb = new StringBuilder(message);
                for (int j = 0; j < i; j++) {
                    sb.append(signatures.get(j));
                }
                dataToVerify = sb.toString().getBytes();
            }

            // Verify the current signature
            boolean isValid = verifySignature(dataToVerify, currentSignature, currentPublicKey);
            if (!isValid) {
                return false; // If any signature is invalid, return false
            }
        }

        return true; // All signatures are valid
    }
}
