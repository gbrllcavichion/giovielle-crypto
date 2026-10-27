package com.giovielle.crypto.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.security.NoSuchAlgorithmException;

import javax.crypto.SecretKey;

import org.junit.jupiter.api.Test;

public class AesServiceTest {

    private final AesService aesService = new AesService();

    @Test
    void shouldGenerate128BitKey() throws NoSuchAlgorithmException {
        SecretKey key = aesService.generateKey(128);

        assertEquals(16, key.getEncoded().length);
        assertEquals("AES", key.getAlgorithm());
    }

    @Test
    void shouldGenerate256BitKey() throws NoSuchAlgorithmException {
        SecretKey key = aesService.generateKey(256);

        assertEquals(32, key.getEncoded().length);
        assertEquals("AES", key.getAlgorithm());
    }

    @Test
    void shouldGenerate16ByteIv() {
        byte[] iv = aesService.generateIv();

        assertEquals(16, iv.length);
    }

    @Test
    void shouldRejectInvalidKeySize() {
        assertThrows(
                IllegalArgumentException.class,
                () -> aesService.generateKey(192)
        );
    }
}