package org.ciphertech.api_gateway.common.cryptography;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import javax.crypto.Cipher;
import java.math.BigInteger;
import java.security.*;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

public class GroupSignature {

    private static final int masterKeySize = 4096;  // Master key size
    private static final String ALGORITHM = "RSA";  // Algorithm for key generation
    private static final String SIGNATURE_ALGORITHM = "SHA256withRSA";  // Signature algorithm

    static {
        Security.addProvider(new BouncyCastleProvider());
    }

    // Master key pair (used for generating voter keys and revealing identity)
    private final KeyPair masterKeyPair;
    private final int RSA_KEY_SIZE;  // RSA key length for voters

    // Map to store each voter's signing keys (public and private)
    private final Map<String, KeyPair> voterKeys = new HashMap<>();

    // Group public key (used for verification, derived from individual public keys or a simple one)
    private PublicKey groupPublicKey;

    public GroupSignature(int keySize) {
        // Generate the master key pair (4096 bits for higher security)
        this.masterKeyPair = generateMasterKeyPair();
        this.groupPublicKey = null;
        this.RSA_KEY_SIZE = keySize;
    }

    // Generates the master RSA key pair
    private static KeyPair generateMasterKeyPair() {
        try {
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(ALGORITHM);
            keyPairGenerator.initialize(masterKeySize);
            return keyPairGenerator.generateKeyPair();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
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
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(ALGORITHM);
        keyPairGenerator.initialize(RSA_KEY_SIZE);
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
    public byte[] signData(String voterId, byte[] data) throws GeneralSecurityException {
        KeyPair voterKeyPair = voterKeys.get(voterId);
        if (voterKeyPair == null) {
            throw new IllegalArgumentException("Voter not found.");
        }
        return signData(data, voterKeyPair.getPrivate());
    }

    // Voter signs data using their private key
    public static byte[] signData(byte[] data, PrivateKey privateKey) throws GeneralSecurityException {
        Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);
        signature.initSign(privateKey);
        signature.update(data);
        return signature.sign();
    }

    // Verifies the signature using the group public key
    public boolean verifySignature(byte[] data, byte[] signatureBytes) throws GeneralSecurityException {
        if (groupPublicKey == null) {
            throw new IllegalStateException("Group public key is not initialized.");
        }
        Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);
        signature.initVerify(groupPublicKey);
        signature.update(data);
        return signature.verify(signatureBytes);
    }

    // Reveals the voter's identity by checking which voter's public key matches the signature
    private String revealVoterIdentity(byte[] signedData, byte[] signatureBytes) throws GeneralSecurityException {
        for (Map.Entry<String, KeyPair> voterEntry : voterKeys.entrySet()) {
            PublicKey publicKey = voterEntry.getValue().getPublic();
            Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);
            signature.initVerify(publicKey);
            signature.update(signedData);

            if (signature.verify(signatureBytes)) {
                return voterEntry.getKey();  // Voter ID found
            }
        }
        throw new IllegalArgumentException("Voter identity could not be revealed.");
    }

    // Method to generate a signature of knowledge
    public SignatureOfKnowledge signKnowledge(String voterId, String message) throws GeneralSecurityException {
        KeyPair voterKeyPair = voterKeys.get(voterId);
        if (voterKeyPair == null) {
            throw new IllegalArgumentException("Voter not found.");
        }

        // Compute r and c
        SecureRandom random = new SecureRandom();
        byte[] r = new byte[256];
        random.nextBytes(r); // Generate random bytes for r
        byte[] y = voterKeyPair.getPublic().getEncoded(); // Simplified, should use a proper representation

        // Compute c = H(m || y || g || g^r)
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        digest.update(message.getBytes());
        digest.update(y);
        // g and g^r are placeholder, replace with actual values
        digest.update("g".getBytes()); // Placeholder for g
        digest.update(r); // Use r directly
        byte[] c = digest.digest();

        // Compute s = r - cx mod n (use appropriate modulus, simplified here)
        BigInteger cx = new BigInteger(c); // Convert c to BigInteger
        BigInteger rValue = new BigInteger(r); // Convert r to BigInteger
        BigInteger n = new BigInteger(1, voterKeyPair.getPublic().getEncoded()); // Simplified
        BigInteger s = rValue.subtract(cx).mod(n);

        return new SignatureOfKnowledge(c, s.toByteArray()); // Returning signature of knowledge
    }

    public static class SignatureOfKnowledge {
        private final byte[] c;
        private final byte[] s;

        public SignatureOfKnowledge(byte[] c, byte[] s) {
            this.c = c;
            this.s = s;
        }

        public byte[] getC() {
            return c;
        }

        public byte[] getS() {
            return s;
        }
    }

    // Verify signature of knowledge
    public boolean verifySignatureOfKnowledge(String message, SignatureOfKnowledge signature, PublicKey publicKey) throws GeneralSecurityException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        digest.update(message.getBytes());
        byte[] y = publicKey.getEncoded(); // Public key encoded for hash
        // g and g^s are placeholder, replace with actual values
        digest.update("g".getBytes()); // Placeholder for g
        digest.update(signature.getS()); // Use s from the signature
        byte[] cPrime = digest.digest();

        return MessageDigest.isEqual(signature.getC(), cPrime); // Compare computed c' with c
    }
}
