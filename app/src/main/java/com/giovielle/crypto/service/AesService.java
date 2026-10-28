package com.giovielle.crypto.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.security.GeneralSecurityException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;

import com.giovielle.crypto.utils.FileUtils;

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

    public void encryptFile(Path inputPath, Path outputPath, SecretKey key, byte[] iv) 
        throws IOException, GeneralSecurityException {

        if (iv == null || iv.length != IV_SIZE_BYTES) {
            throw new IllegalArgumentException(
                    "O IV deve conter 16 bytes."
            );
        }

        Cipher cipher = Cipher.getInstance("AES/CTR/NoPadding");

        IvParameterSpec ivSpec = new IvParameterSpec(iv);

        cipher.init(Cipher.ENCRYPT_MODE, key, ivSpec);

        byte[] plainText = Files.readAllBytes(inputPath);

        byte[] cipherText = cipher.doFinal(plainText);

        FileUtils.saveHex(outputPath, cipherText);
    }

    public void decryptFile(Path inputPath, Path outputPath, SecretKey key, byte[] iv) 
        throws IOException, GeneralSecurityException {

        if (iv == null || iv.length != IV_SIZE_BYTES) {
            throw new IllegalArgumentException(
                    "O IV deve conter 16 bytes."
            );
        }

        Cipher cipher = Cipher.getInstance("AES/CTR/NoPadding");

        IvParameterSpec ivSpec = new IvParameterSpec(iv);

        cipher.init(Cipher.DECRYPT_MODE, key, ivSpec);

        byte[] cipherText = FileUtils.readHex(inputPath);

        byte[] plainText = cipher.doFinal(cipherText);

        Files.write(
            outputPath,
            plainText,
            StandardOpenOption.CREATE_NEW
        );
    }

}