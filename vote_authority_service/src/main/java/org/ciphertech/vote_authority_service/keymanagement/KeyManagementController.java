package org.ciphertech.vote_authority_service.keymanagement;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/keys")
public class KeyManagementController {

    @Autowired
    private KeyGenerationService keyGenerationService;

    @PostMapping("/generate")
    public ResponseEntity<KeyPair> generateKeys() {
        KeyPair keyPair = keyGenerationService.generateKeys();
        return ResponseEntity.ok(keyPair);
    }
}
