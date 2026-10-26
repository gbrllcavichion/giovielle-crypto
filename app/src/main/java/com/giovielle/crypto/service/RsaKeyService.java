package com.giovielle.crypto.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.security.GeneralSecurityException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

public class RsaKeyService {

    public KeyPair generateKeyPair()
            throws NoSuchAlgorithmException {

        KeyPairGenerator generator =
                KeyPairGenerator.getInstance("RSA");

        generator.initialize(3072);

        return generator.generateKeyPair();
    }

    public void saveKeyPair(KeyPair keyPair, String name) throws IOException {

        Path directory = Path.of("keys");

        Files.createDirectories(directory);

        Path publicKeyPath = directory.resolve(name + ".pu");
        Path privateKeyPath = directory.resolve(name + ".pr");

        Files.write(
            publicKeyPath,
            keyPair.getPublic().getEncoded(),
            StandardOpenOption.CREATE_NEW
        );

        Files.write(
            privateKeyPath,
            keyPair.getPrivate().getEncoded(),
            StandardOpenOption.CREATE_NEW
        );
    }

    public PublicKey loadPublicKey(Path path)
        throws IOException, GeneralSecurityException {

        byte[] keyBytes = Files.readAllBytes(path);

        X509EncodedKeySpec keySpec =
                new X509EncodedKeySpec(keyBytes);

        KeyFactory keyFactory =
                KeyFactory.getInstance("RSA");

        return keyFactory.generatePublic(keySpec);
    }

    public PrivateKey loadPrivateKey(Path path)
        throws IOException, GeneralSecurityException {

        byte[] keyBytes = Files.readAllBytes(path);

        PKCS8EncodedKeySpec keySpec =
            new PKCS8EncodedKeySpec(keyBytes);

        KeyFactory keyFactory =
            KeyFactory.getInstance("RSA");

        return keyFactory.generatePrivate(keySpec);
    }
}