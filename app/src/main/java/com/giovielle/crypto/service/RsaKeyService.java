package com.giovielle.crypto.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;

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
}