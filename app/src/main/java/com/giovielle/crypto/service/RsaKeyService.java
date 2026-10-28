package com.giovielle.crypto.service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileAlreadyExistsException;
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
import java.security.Signature;
import java.security.spec.MGF1ParameterSpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.Cipher;
import javax.crypto.spec.OAEPParameterSpec;
import javax.crypto.spec.PSource;

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

        if (Files.exists(publicKeyPath) || Files.exists(privateKeyPath)) {
            throw new FileAlreadyExistsException(
            "Já existem arquivos de chave com esse nome."
         );
        }
        
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

    public boolean keysMatch(PublicKey publicKey, PrivateKey privateKey)
        throws GeneralSecurityException {

        byte[] message = "Giovielle Crypto".getBytes(StandardCharsets.UTF_8);

        Signature signature = Signature.getInstance("SHA256withRSA");

        signature.initSign(privateKey);
        signature.update(message);

        byte[] signedMessage = signature.sign();

        signature.initVerify(publicKey);
        signature.update(message);

        return signature.verify(signedMessage);
    }

    public byte[] encryptBytes(PublicKey publicKey, byte[] data)
            throws GeneralSecurityException {

        Cipher cipher = Cipher.getInstance("RSA/ECB/OAEPPadding");

        OAEPParameterSpec oaepSpec = new OAEPParameterSpec(
                "SHA-256",
                "MGF1",
                MGF1ParameterSpec.SHA256,
                PSource.PSpecified.DEFAULT
        );

        cipher.init(Cipher.ENCRYPT_MODE, publicKey, oaepSpec);

        return cipher.doFinal(data);
    }


    public byte[] decryptBytes(PrivateKey privateKey, byte[] encryptedData)
            throws GeneralSecurityException {

        Cipher cipher = Cipher.getInstance("RSA/ECB/OAEPPadding");

        OAEPParameterSpec oaepSpec = new OAEPParameterSpec(
                "SHA-256",
                "MGF1",
                MGF1ParameterSpec.SHA256,
                PSource.PSpecified.DEFAULT
        );

        cipher.init(Cipher.DECRYPT_MODE, privateKey, oaepSpec);

        return cipher.doFinal(encryptedData);
    }
}