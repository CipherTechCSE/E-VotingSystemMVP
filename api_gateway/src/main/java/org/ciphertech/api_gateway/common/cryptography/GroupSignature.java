package org.ciphertech.api_gateway.common.cryptography;

import org.bouncycastle.asn1.pkcs.RSAPrivateKey;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.math.BigInteger;
import java.security.*;

public class GroupSignature {

    private static final String HASH_ALGORITHM = "SHA-256";
    private final int KEY_SIZE;  // RSA key size
    private final SecureRandom random = new SecureRandom();

    static {
        Security.addProvider(new BouncyCastleProvider());
    }

    private BigInteger n;
    private BigInteger e;
    private BigInteger d;
    private BigInteger g;
    private BigInteger a;

    public GroupSignature(int keySize, BigInteger n, BigInteger g, BigInteger a) {
        this.KEY_SIZE = keySize;
        this.n = n;
        this.g = g;
        this.a = a;
    }

    public GroupSignature(int keySize) throws NoSuchAlgorithmException, NoSuchProviderException {
        this.KEY_SIZE = keySize;
        setup();
    }

    // Group setup
    private void setup() {

        BigInteger p, q, phi;

        // Step 1: Select two large prime numbers p and q
        do {
            q = BigInteger.probablePrime(KEY_SIZE / 2, random);
            p = BigInteger.probablePrime(KEY_SIZE / 2, random);
        } while (p.equals(q)); // Ensure p and q are distinct

        // Step 2: Compute the composite n = p * q
        n = p.multiply(q);

        // Step 3: Compute the Euler Totient Function phi(n) = (p - 1)(q - 1)
        phi = p.subtract(BigInteger.ONE).multiply(q.subtract(BigInteger.ONE));

        // Step 4: Select an integer e such that 1 < e < phi(n) and gcd(e, phi(n)) = 1
        do {
            e = BigInteger.probablePrime(512, random);
        } while (e.compareTo(BigInteger.ONE) <= 0 || e.compareTo(phi) >= 0 || !e.gcd(phi).equals(BigInteger.ONE));

        // Step 5: Compute d such that e * d ≡ 1 mod n
        d = e.modInverse(n);

        // Step 6: Select a cyclic group generator g of order n
        g = BigInteger.probablePrime(512, random); // Choose g appropriately

        // Step 7: Select a large multiplicative order element a in Z*n
        a = new BigInteger(KEY_SIZE, random).modPow(p.subtract(BigInteger.ONE).divide(BigInteger.TWO), n);
    }

    public Integer getNonce() {
        return Math.abs(random.nextInt());
    }

    // Proving knowledge of x
    // s = r − (c⋅x) mod n
    private BigInteger[] proveKnowledge(BigInteger y, BigInteger x, BigInteger r) throws NoSuchAlgorithmException {
        // Step 2: Create a challenge c based on y and r
        MessageDigest hash = MessageDigest.getInstance("SHA-256");
        hash.update((y.toString() + r.toString()).getBytes());
        BigInteger T = new BigInteger(1, hash.digest());

        // Step 3: Compute response s
        BigInteger s = r.subtract(T.multiply(x)).mod(n);

        // Return the proof (y, c, s)
        return new BigInteger[]{y, T, s};
    }

    // Verification of proof
    // g^s.y^T mod n = g^r mod n
    // T is the proof challenge
    private boolean verifyKnowledgeProof(BigInteger y, BigInteger r, BigInteger T, BigInteger s) {
        // Check if g^s * y^c ≡ g^r mod n
        BigInteger leftSide = g.modPow(s, n).multiply(y.modPow(T, n)).mod(n);
        return leftSide.equals(g.modPow(r, n));
    }

    // Join process: Member sends y = a^x mod n to the group manager
    public BigInteger join(BigInteger y, BigInteger r, BigInteger T, BigInteger s) throws NoSuchAlgorithmException {

        // Check the knowledge of x
        if (!verifyKnowledgeProof(y, r, T, s)) {
            throw new SecurityException("Knowledge proof verification failed.");
        }

        // Compute the group signature
        BigInteger c = new BigInteger(256, random); // Example value for c (this should be defined as per your protocol)

        // Simulate certificate receipt

        // Return the computed y and the certificate for further processing
        // certificate = (y + c)^d mod n
        return (y.add(c)).modPow(d, n); // You may want to adjust what you return based on your needs
    }

    // Signing method for a member
    public String sign(BigInteger x, String message) throws NoSuchAlgorithmException {
        BigInteger y = a.modPow(x, n);
    
        // Step 1: Compute gTilde and zTilde
        BigInteger r = new BigInteger(KEY_SIZE, random).mod(n);
        BigInteger gTilde = g.modPow(r, n);
        BigInteger zTilde = gTilde.modPow(y, n);
    
        // Step 2: Compute V1 (SKLOGLOG) as a proof of knowledge of x
        BigInteger r1 = new BigInteger(KEY_SIZE, random).mod(n);
        BigInteger t1 = gTilde.modPow(r1, n);
    
        MessageDigest hash = MessageDigest.getInstance(HASH_ALGORITHM);
        hash.update((message + zTilde.toString() + gTilde.toString() + t1.toString()).getBytes());
        BigInteger c1 = new BigInteger(1, hash.digest());
        BigInteger s1 = r1.subtract(c1.multiply(x)).mod(n);
    
        // Step 3: Compute V2 (SKROOTLOG) as a proof of knowledge of v
        BigInteger v = y.add(c1).mod(n);  // Assume d = 1 for simplicity; adjust as needed.
        BigInteger r2 = new BigInteger(KEY_SIZE, random).mod(n);
        BigInteger t2 = gTilde.modPow(r2, n);
    
        hash.reset();
        hash.update((message + zTilde.toString() + gTilde.toString() + t2.toString()).getBytes());
        BigInteger c2 = new BigInteger(1, hash.digest());
        BigInteger s2 = r2.subtract(c2.multiply(v)).mod(n);
    
        // Step 4: Concatenate and return the full signature tuple as a single string (for simplicity)
        return message + ":" + gTilde.toString(16) + ":" + zTilde.toString(16) + ":" +
                c1.toString(16) + ":" + s1.toString(16) + ":" + c2.toString(16) + ":" + s2.toString(16);
    }

    // Verify signature
    public boolean verify(String message, BigInteger y, byte[] signature) throws Exception {
        // Parse the signature components from the hex-encoded string
        String[] sigParts = new String(signature).split(":");
        BigInteger gTilde = new BigInteger(sigParts[1], 16);
        BigInteger zTilde = new BigInteger(sigParts[2], 16);
        
        // V1: Parse challenge c1 and response s1
        BigInteger c1 = new BigInteger(sigParts[3], 16);
        BigInteger s1 = new BigInteger(sigParts[4], 16);

        // V2: Parse challenge c2 and response s2
        BigInteger c2 = new BigInteger(sigParts[5], 16);
        BigInteger s2 = new BigInteger(sigParts[6], 16);

        // Recompute t1 for V1: Check if c1 is consistent with z̃ = g̃^a
        BigInteger t1 = (gTilde.modPow(s1, n).multiply(zTilde.modPow(c1, n))).mod(n);
        MessageDigest hash = MessageDigest.getInstance(HASH_ALGORITHM);
        hash.update((message + zTilde.toString() + gTilde.toString() + t1.toString()).getBytes());
        BigInteger c1Prime = new BigInteger(1, hash.digest());
        
        // Verify if c1 matches recomputed c1Prime
        boolean v1Verified = c1.equals(c1Prime);

        // Compute v for V2: v = (y + c2) * d mod n
        BigInteger v = (y.add(c2)).multiply(d).mod(n);

        // Recompute t2 for V2: Check if c2 is consistent with z̃ * g̃^c2 = g̃^v
        BigInteger t2 = (gTilde.modPow(s2, n).multiply((zTilde.multiply(gTilde.modPow(c2, n))).mod(n))).mod(n);
        hash.reset();
        hash.update((message + zTilde.toString() + gTilde.toString() + t2.toString()).getBytes());
        BigInteger c2Prime = new BigInteger(1, hash.digest());

        // Verify if c2 matches recomputed c2Prime
        boolean v2Verified = c2.equals(c2Prime);

        // Both V1 and V2 need to be verified for overall signature validity
        return v1Verified && v2Verified;
    }

    // Utility method to generate a random secret x in Z*n for joining
    public BigInteger generateRandomSecret() {
        return new BigInteger(KEY_SIZE, random).mod(n);
    }

    public BigInteger getN() {
        return n;
    }

    public void setN(BigInteger n) {
        this.n = n;
    }

    public BigInteger getA() {
        return a;
    }

    public void setA(BigInteger a) {
        this.a = a;
    }

    public BigInteger getG() {
        return g;
    }

    public void setG(BigInteger g) {
        this.g = g;
    }
}
