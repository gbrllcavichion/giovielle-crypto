package com.giovielle.crypto.service;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import javax.crypto.SecretKey;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import com.giovielle.crypto.utils.FileUtils;

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

    @Test
    void shouldEncryptFileWithAesCtr(@TempDir Path tempDir)
        throws Exception {

        Path inputFile = tempDir.resolve("mensagem.txt");
        Path encryptedFile = tempDir.resolve("Y.txt");

        byte[] original = "Mensagem de teste do Giovielle Crypto!"
                .getBytes(StandardCharsets.UTF_8);

        Files.write(inputFile, original);

        SecretKey key = aesService.generateKey(128);
        byte[] iv = aesService.generateIv();

        aesService.encryptFile(inputFile, encryptedFile, key, iv);

        assertTrue(Files.exists(encryptedFile));

        byte[] encrypted = FileUtils.readHex(encryptedFile);

        assertFalse(Arrays.equals(original, encrypted));

        assertEquals(original.length, encrypted.length);
    }


    @Test
    void shouldDecryptFileWithAesCtr(@TempDir Path tempDir)
            throws Exception {

        Path originalFile = tempDir.resolve("original.txt");
        Path encryptedFile = tempDir.resolve("Y.txt");
        Path decryptedFile = tempDir.resolve("recuperado.txt");

        byte[] original = "Olá, Giovielle Crypto!"
                .getBytes(StandardCharsets.UTF_8);

        Files.write(originalFile, original);

        SecretKey key = aesService.generateKey(256);
        byte[] iv = aesService.generateIv();

        aesService.encryptFile(
                originalFile,
                encryptedFile,
                key,
                iv
        );

        aesService.decryptFile(
                encryptedFile,
                decryptedFile,
                key,
                iv
        );

        byte[] decrypted = Files.readAllBytes(decryptedFile);

        assertArrayEquals(original, decrypted);
    }


    @Test
    void shouldSaveAesKeyAndIv(@TempDir Path tempDir)
            throws Exception {

        SecretKey key = aesService.generateKey(128);
        byte[] iv = aesService.generateIv();

        Path keyFile = tempDir.resolve("K.txt");
        Path ivFile = tempDir.resolve("IV.txt");

        FileUtils.saveHex(keyFile, key.getEncoded());
        FileUtils.saveHex(ivFile, iv);

        assertArrayEquals(
                key.getEncoded(),
                FileUtils.readHex(keyFile)
        );

        assertArrayEquals(
                iv,
                FileUtils.readHex(ivFile)
        );
    }

}