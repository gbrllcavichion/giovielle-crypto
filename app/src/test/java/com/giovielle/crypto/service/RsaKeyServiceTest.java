
package com.giovielle.crypto.service;

import java.security.KeyPair;
import java.util.Arrays;

import javax.crypto.SecretKey;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import org.junit.jupiter.api.Test;

public class RsaKeyServiceTest {

    private final RsaKeyService rsaService = new RsaKeyService();
    private final AesService aesService = new AesService();

    @Test
    void shouldEncryptAndDecryptAesKey() throws Exception {

        KeyPair rsaKeys = rsaService.generateKeyPair();

        SecretKey aesKey = aesService.generateKey(256);

        byte[] original = aesKey.getEncoded();

        byte[] encrypted = rsaService.encryptBytes(
                rsaKeys.getPublic(),
                original
        );

        byte[] decrypted = rsaService.decryptBytes(
                rsaKeys.getPrivate(),
                encrypted
        );

        assertFalse(Arrays.equals(original, encrypted));

        assertArrayEquals(original, decrypted);
    }

    @Test
    void shouldEncryptAndDecryptIv() throws Exception {

        KeyPair rsaKeys = rsaService.generateKeyPair();

        byte[] originalIv = aesService.generateIv();

        byte[] encryptedIv = rsaService.encryptBytes(
                rsaKeys.getPublic(),
                originalIv
        );

        byte[] decryptedIv = rsaService.decryptBytes(
                rsaKeys.getPrivate(),
                encryptedIv
        );

        assertArrayEquals(originalIv, decryptedIv);
    }
}
