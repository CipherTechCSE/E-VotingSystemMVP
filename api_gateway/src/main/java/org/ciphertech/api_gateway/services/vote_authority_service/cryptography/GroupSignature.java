package org.ciphertech.api_gateway.services.vote_authority_service.cryptography;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import java.security.*;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@Component
public class GroupSignature {

    static {
        Security.addProvider(new BouncyCastleProvider());
    }

    // Master key pair (used for generating voter keys and revealing identity)
    private final KeyPair masterKeyPair;

    // Map to store each voter's signing keys (public and private)
    private final Map<String, KeyPair> voterKeys = new HashMap<>();

    // Group public key (used for verification, derived from individual public keys or a simple one)
    private PublicKey groupPublicKey;

    public GroupSignature() throws NoSuchAlgorithmException {
        // Generate the master key pair (4096 bits for higher security)
        this.masterKeyPair = generateMasterKeyPair();
        this.groupPublicKey = null;
    }

    // Generates the master RSA key pair
    private static KeyPair generateMasterKeyPair() throws NoSuchAlgorithmException {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(4096); // Larger size for the master key
        return keyPairGenerator.generateKeyPair();
    }

    // Generates an RSA key pair for a voter and adds them to the voter group
    public KeyPair joinVotersGroup(String voterId) throws NoSuchAlgorithmException {
        KeyPair voterKeyPair = generateVoterKeyPair();
        voterKeys.put(voterId, voterKeyPair);
        updateGroupPublicKey();  // Update the group public key after a new voter joins
        return voterKeyPair;     // Return the voter's private and public key to them
    }

    // Generates a voter's RSA key pair (individual signing keys)
    private KeyPair generateVoterKeyPair() throws NoSuchAlgorithmException {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(2048);  // Standard RSA size for voters
        return keyPairGenerator.generateKeyPair();
    }

    // Update the group public key by combining all voter public keys (this is simplified here)
    private void updateGroupPublicKey() {
        // In a real group signature scheme, we would combine the keys mathematically.
        // For simplicity, we take one of the voters' public keys as the group key.
        if (!voterKeys.isEmpty()) {
            this.groupPublicKey = voterKeys.values().iterator().next().getPublic(); // Simplified version
        }
    }

    // Returns the group public key for signature verification
    public PublicKey getGroupPublicKey() {
        return this.groupPublicKey;
    }

    // Voter signs data using their private key
    public static byte[] signData(byte[] data, PrivateKey privateKey) throws GeneralSecurityException {
        Signature signature = Signature.getInstance("SHA256withRSA");
        signature.initSign(privateKey);
        signature.update(data);
        return signature.sign();
    }

    // Verifies the signature using the group public key
    public boolean verifySignature(byte[] data, byte[] signatureBytes) throws GeneralSecurityException {
        if (groupPublicKey == null) {
            throw new IllegalStateException("Group public key is not initialized.");
        }
        Signature signature = Signature.getInstance("SHA256withRSA");
        signature.initVerify(groupPublicKey);
        signature.update(data);
        return signature.verify(signatureBytes);
    }

    // Reveals the voter's identity by checking which voter's public key matches the signature
    public String revealVoterIdentity(byte[] signedData, byte[] signatureBytes) throws GeneralSecurityException {
        for (Map.Entry<String, KeyPair> voterEntry : voterKeys.entrySet()) {
            PublicKey publicKey = voterEntry.getValue().getPublic();
            Signature signature = Signature.getInstance("SHA256withRSA");
            signature.initVerify(publicKey);
            signature.update(signedData);

            if (signature.verify(signatureBytes)) {
                return voterEntry.getKey();  // Voter ID found
            }
        }
        throw new IllegalArgumentException("Voter identity could not be revealed.");
    }

    // Encrypt the voter's identity using the master private key for identity reveal later
    public String encryptVoterIdentity(String voterId) throws GeneralSecurityException {
        PrivateKey masterPrivateKey = masterKeyPair.getPrivate();
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, masterPrivateKey);
        byte[] encryptedVoterId = cipher.doFinal(voterId.getBytes());
        return Base64.getEncoder().encodeToString(encryptedVoterId);
    }

    // Decrypt to reveal voter's identity using the master public key
    public String decryptVoterIdentity(String encryptedVoterId) throws GeneralSecurityException {
        PublicKey masterPublicKey = masterKeyPair.getPublic();
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE, masterPublicKey);
        byte[] decryptedData = cipher.doFinal(Base64.getDecoder().decode(encryptedVoterId));
        return new String(decryptedData);
    }
}
