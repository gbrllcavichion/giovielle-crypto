package com.giovielle.crypto.service;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

public class AesService {

    private static final int IV_SIZE_BYTES = 16;

    private final SecureRandom secureRandom = new SecureRandom();

    public SecretKey generateKey(int keySize)
            throws NoSuchAlgorithmException {

        if (keySize != 128 && keySize != 256) {
            throw new IllegalArgumentException(
                    "O tamanho da chave deve ser 128 ou 256 bits."
            );
        }

        KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");

        keyGenerator.init(keySize, secureRandom);

        return keyGenerator.generateKey();
    }

    public byte[] generateIv() {

        byte[] iv = new byte[IV_SIZE_BYTES];

        secureRandom.nextBytes(iv);

        return iv;
    }
}