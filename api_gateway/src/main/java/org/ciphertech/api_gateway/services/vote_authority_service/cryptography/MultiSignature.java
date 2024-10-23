package org.ciphertech.api_gateway.services.vote_authority_service.cryptography;

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

@Component
public class MultiSignature {

    private final ServiceRepository serviceRepository;

    private static final String AES = "AES";
    private static final String SECRET_KEY = "1234567890123456"; // Replace with a more secure key management strategy
    private static final int RSA_KEY_SIZE = 2048; // RSA key length

    static {
        Security.addProvider(new BouncyCastleProvider());
    }

    public MultiSignature(ServiceRepository serviceRepository) {
        this.serviceRepository = serviceRepository;
    }

    // Encrypt the key using AES
    private byte[] encryptKey(byte[] key) throws GeneralSecurityException {
        SecretKeySpec secretKey = new SecretKeySpec(SECRET_KEY.getBytes(), AES);
        Cipher cipher = Cipher.getInstance(AES);
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        return cipher.doFinal(key);
    }

    // Decrypt the key using AES
    private byte[] decryptKey(byte[] encryptedKey) throws GeneralSecurityException {
        SecretKeySpec secretKey = new SecretKeySpec(SECRET_KEY.getBytes(), AES);
        Cipher cipher = Cipher.getInstance(AES);
        cipher.init(Cipher.DECRYPT_MODE, secretKey);
        return cipher.doFinal(encryptedKey);
    }

    // Store service keys and return the created Service object
    public VotingSystemService storeServiceKeys(Long serviceId, String serviceName, String description, String url) throws GeneralSecurityException {
        KeyPair keyPair = generateKeyPair();

        VotingSystemService service = new VotingSystemService();
        service.setId(serviceId);
        service.setName(serviceName);
        service.setDescription(description);
        service.setUrl(url);
        service.setPublicKey(encryptKey(keyPair.getPublic().getEncoded()));
        service.setPrivateKey(encryptKey(keyPair.getPrivate().getEncoded()));

        return serviceRepository.save(service);
    }

    // Retrieve service keys based on the service ID
    public KeyPair retrieveServiceKeys(Long serviceId) throws GeneralSecurityException {
        VotingSystemService service = serviceRepository.findById(serviceId).orElse(null);

        if (service != null) {
            byte[] decryptedPublicKey = decryptKey(service.getPublicKey());
            byte[] decryptedPrivateKey = decryptKey(service.getPrivateKey());

            // Convert decrypted byte arrays back into Key objects
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            PublicKey publicKey = keyFactory.generatePublic(new X509EncodedKeySpec(decryptedPublicKey));
            PrivateKey privateKey = keyFactory.generatePrivate(new PKCS8EncodedKeySpec(decryptedPrivateKey));

            return new KeyPair(publicKey, privateKey);
        }
        throw new IllegalArgumentException("Service not found: " + serviceId);
    }

    // Generate a new RSA key pair with a key length of 2048 bits
    public KeyPair generateKeyPair() throws NoSuchAlgorithmException {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(RSA_KEY_SIZE); // Set key size to 2048 bits        
        
        return keyPairGenerator.generateKeyPair();
    }

    // Sign the data using the service's private key
    public String signData(Long serviceId, byte[] data) throws Exception {
        KeyPair keyPair = retrieveServiceKeys(serviceId);
        PrivateKey privateKey = keyPair.getPrivate();

        Signature signature = Signature.getInstance("SHA256withRSA");
        signature.initSign(privateKey);
        signature.update(data);
        byte[] signedData = signature.sign();
        return Base64.getEncoder().encodeToString(signedData);
    }

    // Verify the signature using the service's public key
    public boolean verifySignature(Long serviceId, byte[] data, String signatureStr) throws Exception {
        KeyPair keyPair = retrieveServiceKeys(serviceId);
        PublicKey publicKey = keyPair.getPublic();

        Signature signature = Signature.getInstance("SHA256withRSA");
        signature.initVerify(publicKey);
        signature.update(data);
        byte[] signatureBytes = Base64.getDecoder().decode(signatureStr);
        return signature.verify(signatureBytes);
    }
}
